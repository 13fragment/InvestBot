package abc.investbot.starategy;

import abc.investbot.model.CachedCandle;
import abc.investbot.model.RSIStrategyConfig;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

@Service
public class RSICache {

    private final Map<String, Map<Integer, BigDecimal>> cache = new HashMap<>();

    public Map<String, Map<Integer, BigDecimal>> getCache() {
        return cache;
    }

    public void calculateRSI(String figi, Map<String, TreeSet<CachedCandle>> candlesCache, RSIStrategyConfig config) {
        var rsiPeriod = config.getRsiPeriod();
        calculateRSI(rsiPeriod, figi, candlesCache);
    }


    private void calculateRSI(int limit, String figi, Map<String, TreeSet<CachedCandle>> candlesCache) {
        if (candlesCache.size() <= 0) {
            return;
        }

        var totalGain = BigDecimal.ZERO;
        var gainAmount = 0;
        var totalLoss = BigDecimal.ZERO;
        var lossAmount = 0;

        var candles = candlesCache.get(figi);

        //берем последние n свечей до текущего времени
        var toIndex = candles.size() - 1;
        var fromIndex = Math.max(0, toIndex - limit);
        var dataset = new ArrayList<>(candles).subList(fromIndex, toIndex);
        for (int i = 1; i < dataset.size(); i++) {
            var candleClosePrice = dataset.get(i).getClosePrice();
            var prevCandleClosePrice = dataset.get(i - 1).getClosePrice();
            var change = candleClosePrice.subtract(prevCandleClosePrice);
            if (candleClosePrice.equals(prevCandleClosePrice)) continue;

            if (change.compareTo(BigDecimal.ZERO) >= 0) {
                totalGain = totalGain.add(change);
                gainAmount++;
            } else {
                totalLoss = totalLoss.add(change);
                lossAmount++;
            }
        }
        if (gainAmount == 0) gainAmount = 1;
        if (lossAmount == 0) lossAmount = 1;

        var avgGain = totalGain.divide(BigDecimal.valueOf(gainAmount), RoundingMode.DOWN);
        if (avgGain.equals(BigDecimal.ZERO)) avgGain = BigDecimal.ONE;

        var avgLoss = totalLoss.divide(BigDecimal.valueOf(lossAmount), RoundingMode.DOWN);
        if (avgLoss.equals(BigDecimal.ZERO)) avgLoss = BigDecimal.ONE;

        var rs = avgGain.divide(avgLoss, RoundingMode.DOWN).abs();
        //100 - 100 / (1 + rs);
        var rsi = BigDecimal.valueOf(100)
                .subtract(
                        BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), RoundingMode.DOWN)
                );

        if (!cache.containsKey(figi)) {
            cache.put(figi, new HashMap<>());
        }
        var rsiValue = cache.get(figi);
        rsiValue.put(limit, rsi);
    }
}
