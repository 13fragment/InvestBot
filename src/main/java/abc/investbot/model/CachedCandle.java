package abc.investbot.model;

import com.google.protobuf.Timestamp;
import ru.tinkoff.piapi.contract.v1.Candle;
import ru.tinkoff.piapi.contract.v1.HistoricCandle;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;

public class CachedCandle {

    //цена * лот
    private final BigDecimal closePrice;
    private final Timestamp timestamp;

    private CachedCandle(Quotation closePrice, Timestamp timestamp, BigDecimal lot) {
        this.closePrice = MapperUtils.quotationToBigDecimal(closePrice).multiply(lot);
        this.timestamp = timestamp;
    }

    public static CachedCandle ofHistoricCandle(HistoricCandle candle, BigDecimal lot) {
        return new CachedCandle(candle.getClose(), candle.getTime(), lot);
    }

    public static CachedCandle ofStreamCandle(Candle candle, BigDecimal lot) {
        return new CachedCandle(candle.getClose(), candle.getTime(), lot);
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }
}