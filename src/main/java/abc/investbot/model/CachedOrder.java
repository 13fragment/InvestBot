package abc.investbot.model;

import java.math.BigDecimal;

public class CachedOrder {

    private boolean shortOpen = false;
    private boolean longOpen = false;
    //цена * лот
    private BigDecimal openPrice = BigDecimal.TEN;

    public BigDecimal getOpenPrice() {
        return this.openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public void setLongOpen(boolean longOpen) {
        this.longOpen = longOpen;
    }


    public void setShortOpen(boolean shortOpen) {
        this.shortOpen = shortOpen;
    }

    public boolean isShortOpen() {
        return shortOpen;
    }

    public boolean isLongOpen() {
        return longOpen;
    }

}

