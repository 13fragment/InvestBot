package abc.investbot.starategy;

import abc.investbot.model.CachedOrder;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.core.utils.MapperUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrdersHistory {
    private final Map<String, CachedOrder> cache = new HashMap<>();
    private CachedOrder getCachedOrder(String figi) {
        var cachedOrder = cache.get(figi);
        if (cachedOrder == null) {
            cachedOrder = new CachedOrder();
            cache.put(figi, cachedOrder);
        }
        return cachedOrder;
    }

    public void setPrice(String figi, MoneyValue openPrice) {
        getCachedOrder(figi).setOpenPrice(MapperUtils.moneyValueToBigDecimal(openPrice));
    }

    public BigDecimal getPrice(String figi) {
        return getCachedOrder(figi).getOpenPrice();
    }

    public boolean shortOpen(String figi) {
        return getCachedOrder(figi).isShortOpen();
    }

    public boolean longOpen(String figi) {
        return getCachedOrder(figi).isLongOpen();
    }

    public void setLong(String figi, boolean value) {
        getCachedOrder(figi).setLongOpen(value);
    }

    public void setShort(String figi, boolean value) {
        getCachedOrder(figi).setShortOpen(value);
    }
}
