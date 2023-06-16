package abc.investbot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RSIStrategyConfig {

    private List<String> figi;
    private BigDecimal upperRsiThreshold = BigDecimal.valueOf(70);
    private BigDecimal lowerRsiThreshold = BigDecimal.valueOf(30);
    private BigDecimal takeProfit = BigDecimal.valueOf(0.15);
    private BigDecimal stopLoss = BigDecimal.valueOf(0.05);
    private int rsiPeriod = 14;

    public List<String> getFigi() {
        return this.figi;
    }

    public int getRsiPeriod() {
        return this.rsiPeriod;
    }

    public BigDecimal getLowerRsiThreshold() {
        return this.lowerRsiThreshold;
    }

    public BigDecimal getUpperRsiThreshold() {
        return this.upperRsiThreshold;
    }

    public BigDecimal getTakeProfit() {
        return this.takeProfit;
    }

    public BigDecimal getStopLoss() {
        return this.stopLoss;
    }

}
