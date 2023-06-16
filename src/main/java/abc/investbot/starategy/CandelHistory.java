package abc.investbot.starategy;

import abc.investbot.model.CachedCandle;
import abc.investbot.model.RSIStrategyConfig;
import abc.investbot.service.AccountService;
import abc.investbot.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.CandleInterval;
import ru.tinkoff.piapi.contract.v1.MarketDataResponse;
import ru.tinkoff.piapi.contract.v1.SubscriptionInterval;
import ru.tinkoff.piapi.contract.v1.SubscriptionStatus;
import ru.tinkoff.piapi.core.MarketDataService;
import ru.tinkoff.piapi.core.exception.ApiRuntimeException;
import ru.tinkoff.piapi.core.stream.StreamProcessor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CandelHistory {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final static int DAYS_CANDLES = 3;

    private final Map<String, TreeSet<CachedCandle>> cache = new HashMap<>();
    private final AccountService accountService;
    private final RSIHandler signalHandler;
    private final RSICache rsiCacheService;
    private final InstrumentsHistory instrumentsHistory;

    public CandelHistory(AccountService accountService, RSIHandler signalHandler, RSICache rsiCacheService, InstrumentsHistory instrumentsHistory) {
        this.accountService = accountService;
        this.signalHandler = signalHandler;
        this.rsiCacheService = rsiCacheService;
        this.instrumentsHistory = instrumentsHistory;
    }


    public Set<CachedCandle> collectCandlesFor1Day(MarketDataService marketDataService, String figi, Instant endDate, BigDecimal lot) throws InterruptedException {
        var startDate = endDate.minus(1, ChronoUnit.DAYS);
        try {
            return marketDataService.getCandlesSync(figi, startDate, endDate, CandleInterval.CANDLE_INTERVAL_5_MIN)
                    .stream()
                    .map(candle -> CachedCandle.ofHistoricCandle(candle, lot))
                    .collect(Collectors.toSet());
        } catch (ApiRuntimeException exception) {
            Thread.sleep(1000);
            return collectCandlesFor1Day(marketDataService, figi, endDate, lot);
        }
    }


    public void initCache(List<RSIStrategyConfig> configs) throws InterruptedException {
        collectHistoricalCandles(configs);

        for (RSIStrategyConfig config : configs) {
            subscribeCandles(config);
        }
    }

    public void collectHistoricalCandles(List<RSIStrategyConfig> configs) throws InterruptedException {
        for (RSIStrategyConfig config : configs) {
            var figiList = config.getFigi();
            log.info("init cache for {} figi", figiList.size());
            for (String figi : figiList) {
                var lot = instrumentsHistory.getLot(figi);
                if (cache.containsKey(figi)) {
                    continue;
                }

                var mdService = accountService.getInvestApi().getMarketDataService();
                var endDate = OffsetDateTime.now().toInstant();
                log.info("figi: {}. init candles", figi);
                log.info("figi: {}. end date for candles {}", figi, OffsetDateTime.ofInstant(endDate, ZoneId.of("UTC")));

                var candles = new TreeSet<CachedCandle>(Comparator.comparingLong(candle -> candle.getTimestamp().getSeconds()));
                cache.put(figi, candles);

                for (var i = 0; i < DAYS_CANDLES; i++) {
                    var collectedCandles = collectCandlesFor1Day(mdService, figi, endDate, lot);
                    candles.addAll(collectedCandles);
                    endDate = endDate.minus(1, ChronoUnit.DAYS);
                }
                log.info("figi: {}. start date for candles {}", figi, OffsetDateTime.ofInstant(endDate, ZoneId.of("UTC")));
                log.info("figi: {}. collected {} candles", figi, candles.size());
                rsiCacheService.calculateRSI(figi, cache, config);
            }

        }
    }

    private void subscribeCandles(RSIStrategyConfig config) {

        Consumer<Throwable> onErrorCallback = error -> log.error(error.toString());
        StreamProcessor<MarketDataResponse> processor = response -> {
            if (response.hasCandle()) {
                var figi = response.getCandle().getFigi();
//                log.info("new candles data for figi {}", figi);

                var lot = instrumentsHistory.getLot(figi);
                var candle = CachedCandle.ofStreamCandle(response.getCandle(), lot);
                cache.get(figi).add(candle);

                rsiCacheService.calculateRSI(figi, cache, config);
                signalHandler.handle(candle.getClosePrice(), config, figi);
            } else if (response.hasSubscribeCandlesResponse()) {
                var successCount = response.getSubscribeCandlesResponse()
                        .getCandlesSubscriptionsList()
                        .stream()
                        .filter(el -> el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS))
                        .count();
                var errorCount = response.getSubscribeTradesResponse()
                        .getTradeSubscriptionsList()
                        .stream()
                        .filter(el -> !el.getSubscriptionStatus().equals(SubscriptionStatus.SUBSCRIPTION_STATUS_SUCCESS))
                        .count();
                log.info("success candles subscriptions: {}", successCount);
                log.info("error candles subscriptions: {}", errorCount);
            }
        };
        var marketDataStreamService = accountService.getInvestApi().getMarketDataStreamService();

        //todo. Придумать способ, как открывать следующий стрим, если инструментов больше 300
        var streamName = "default stream id";
        var stream = marketDataStreamService.getStreamById(streamName);
        if (stream == null) {
            stream = marketDataStreamService.newStream(streamName, processor, onErrorCallback);
        }

        stream.subscribeCandles(config.getFigi(), SubscriptionInterval.SUBSCRIPTION_INTERVAL_ONE_MINUTE);
    }
}
