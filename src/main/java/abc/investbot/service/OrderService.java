package abc.investbot.service;

import abc.investbot.starategy.OrdersHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final AccountService accountService;
    private final OrdersHistory ordersHistory;

    public OrderService(AccountService accountService, OrdersHistory ordersHistory) {
        this.accountService = accountService;
        this.ordersHistory = ordersHistory;
    }

    List<String> figiList = List.of("BBG004730N88");
    private int numberOfLots = 10;
/*
    public void buyMarket(String figi, long quantity) {
        List<LastPrice> lastPricesList = accountService.getInvestApi().getMarketDataService().getLastPricesSync(figiList);
        var stablePrice = Quotation.newBuilder().setUnits(250).setNano(100).build();
        for (LastPrice price : lastPricesList) {
            if (price.getPrice().getUnits() < stablePrice.getUnits()) {
                var accountId = accountService.getAccountId();
                var orderId = accountService.getInvestApi().getSandboxService().postOrderSync(figi, quantity, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET,
                        UUID.randomUUID().toString()).getOrderId();
                log.info("выставлена заявка с id: {}", orderId);
            }
        }

    }


    public void sellMarket(String figi, long quantity) {
        List<LastPrice> lastPricesList = accountService.getInvestApi().getMarketDataService().getLastPricesSync(figiList);
        var stablePrice = Quotation.newBuilder().setUnits(270).setNano(100).build();
        for (LastPrice price : lastPricesList) {
            if (price.getPrice().getUnits() > stablePrice.getUnits()) {
                var accountId = accountService.getAccountId();
                var orderId = accountService.getInvestApi().getSandboxService().postOrderSync(figi, quantity, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET,
                        UUID.randomUUID().toString()).getOrderId();
                log.info("продана заявка с id: {}", orderId);
            }
        }

    }*/

    public void sellMarketLong(String figi) {
        var executedPrice = sellMarket(figi);
        ordersHistory.setLong(figi, false);
        ordersHistory.setPrice(figi, executedPrice);
    }

    public void buyMarketLong(String figi) {
        var executedPrice = buyMarket(figi);
        ordersHistory.setLong(figi, true);
        ordersHistory.setPrice(figi, executedPrice);
    }

    public void sellMarketShort(String figi) {
        var executedPrice = buyMarket(figi);
        ordersHistory.setShort(figi, false);
        ordersHistory.setPrice(figi, executedPrice);
    }

    public void buyMarketShort(String figi) {
        var executedPrice = sellMarket(figi);
        ordersHistory.setShort(figi, true);
        ordersHistory.setPrice(figi, executedPrice);
    }

    private MoneyValue sellMarket(String figi) {
        var orderId = UUID.randomUUID().toString();
        var accountId = accountService.getAccountId();
        return accountService.getInvestApi().getSandboxService().postOrderSync(figi, numberOfLots, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, orderId).getTotalOrderAmount();
    }

    private MoneyValue buyMarket(String figi) {
        var orderId = UUID.randomUUID().toString();
        var accountId = accountService.getAccountId();
        return accountService.getInvestApi().getSandboxService().postOrderSync(figi, numberOfLots, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET, orderId).getTotalOrderAmount();
    }
}
