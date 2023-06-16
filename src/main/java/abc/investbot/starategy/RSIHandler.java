package abc.investbot.starategy;

import abc.investbot.model.RSIStrategyConfig;
import abc.investbot.service.OrderService;
import abc.investbot.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RSIHandler {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final RSICache rsiCache;
    private final OrderService orderService;
    private final OrdersHistory ordersHistory;

    public RSIHandler(RSICache rsiCache, OrderService orderService, OrdersHistory ordersHistory) {
        this.rsiCache = rsiCache;
        this.orderService = orderService;
        this.ordersHistory = ordersHistory;
    }


    public void handle(BigDecimal closePrice, RSIStrategyConfig config, String figi) {
        var rsiPeriod = config.getRsiPeriod();
        var rsi = rsiCache.getCache().get(figi).get(rsiPeriod);

        checkForOpenShort(rsi, figi, closePrice, config);
        checkForOpenLong(rsi, figi, closePrice, config);
        checkForCloseShort(figi, closePrice, config);
        checkForCloseLong(figi, closePrice, config);
    }

    private void checkForOpenLong(BigDecimal rsi, String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var lower = config.getLowerRsiThreshold();
        // открываем лонг, если RSI < lower (30)
        if (rsi.compareTo(lower) >= 0) {
            return;
        }
        var longOpen = ordersHistory.longOpen(figi);
        //Если уже куплен в лонг - выходим
        if (longOpen) {
            return;
        }

        //закрываем шорт, если есть
        if (ordersHistory.shortOpen(figi)) {
            var openPrice = ordersHistory.getPrice(figi);
            sellShort(figi, closePrice, openPrice, "open long");
        }

        buyLong(rsi, figi, closePrice);
    }

    private void checkForOpenShort(BigDecimal rsi, String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var upper = config.getUpperRsiThreshold();
        // открываем шорт, если RSI > upper (70)
        if (rsi.compareTo(upper) <= 0) {
            return;
        }
        var shortOpen = ordersHistory.shortOpen(figi);
        //Если уже куплен в шорт - выходим
        if (shortOpen) {
            return;
        }

        //закрываем лонг, если есть
        if (ordersHistory.longOpen(figi)) {
            var openPrice = ordersHistory.getPrice(figi);
            sellLong(figi, closePrice, openPrice, "open short");
        }

        buyShort(rsi, figi, closePrice);
    }

    private void checkForCloseShort(String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var shortOpen = ordersHistory.shortOpen(figi);
        //Если уже продан в шорт - выходим
        if (!shortOpen) {
            return;
        }

        var takeProfit = BigDecimal.ONE.subtract(config.getTakeProfit());
        var stopLoss = config.getStopLoss().add(BigDecimal.ONE);

        var openPrice = ordersHistory.getPrice(figi);
        var longOpen = ordersHistory.longOpen(figi);
        String reason = null;
        if (longOpen) {
            reason = "long opened";
        } else if (openPrice.multiply(takeProfit).compareTo(closePrice) >= 0) {
            reason = "take profit";
        } else if (openPrice.multiply(stopLoss).compareTo(closePrice) <= 0) {
            reason = "stop loss";
        }

        if (reason != null) {
            sellShort(figi, closePrice, openPrice, reason);
        }
    }

    private void checkForCloseLong(String figi, BigDecimal closePrice, RSIStrategyConfig config) {
        var longOpen = ordersHistory.longOpen(figi);

        //Если уже продан в лонг, либо ничего нет - выходим
        if (!longOpen) {
            return;
        }

        var takeProfit = config.getTakeProfit().add(BigDecimal.ONE);
        var stopLoss = BigDecimal.ONE.subtract(config.getStopLoss());

        var openPrice = ordersHistory.getPrice(figi);
        var shortOpen = ordersHistory.shortOpen(figi);
        String reason = null;
        if (shortOpen) {
            reason = "short opened";
        } else if (openPrice.multiply(takeProfit).compareTo(closePrice) <= 0) {
            reason = "take profit";
        } else if (openPrice.multiply(stopLoss).compareTo(closePrice) >= 0) {
            reason = "stop loss";
        }

        if (reason != null) {
            sellLong(figi, closePrice, openPrice, reason);
        }
    }

    private void buyLong(BigDecimal rsi, String figi, BigDecimal closePrice) {
        log.info("opening long. figi: {}, close price: {}, RSI: {}", figi, closePrice, rsi);
        orderService.buyMarketLong(figi);
    }

    private void buyShort(BigDecimal rsi, String figi, BigDecimal closePrice) {
        log.info("opening short. figi: {}, close price: {}, RSI: {}", figi, closePrice, rsi);
        orderService.buyMarketShort(figi);
    }

    private void sellShort(String figi, BigDecimal closePrice, BigDecimal openPrice, String reason) {
        //для профита шорта цена открытия должна быть больше цены закрытия
        var profit = openPrice.subtract(closePrice);
        log.info("closing short. figi: {}, open price: {}, close price: {}, profit: {}, reason: {}", figi, openPrice, closePrice, profit, reason);
        orderService.sellMarketShort(figi);
    }

    private void sellLong(String figi, BigDecimal closePrice, BigDecimal openPrice, String reason) {
        var profit = closePrice.subtract(openPrice);
        log.info("closing long. figi: {}, open price: {}, close price: {}, profit: {}, reason: {}", figi, openPrice, closePrice, profit, reason);
        orderService.sellMarketLong(figi);
    }

}
