package abc.investbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final AccountService accountService;

    public OrderService(AccountService accountService) {
        this.accountService = accountService;
    }

    List<String> figiList = Arrays.asList("BBG004730N88");

    public void buyMarket(String figi, long quantity) {
        List<LastPrice> lastPricesList = accountService.getInvestApi().getMarketDataService().getLastPricesSync(figiList);
        var stablePrice = Quotation.newBuilder().setUnits(400).setNano(100).build();
        for (LastPrice price : lastPricesList) {
            if (price.getPrice().getUnits() < stablePrice.getUnits()) {
                var accountId = accountService.getAccountId();
                var orderId = accountService.getInvestApi().getSandboxService().postOrderSync(figi, quantity, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET,
                        UUID.randomUUID().toString()).getOrderId();
                log.info("выставлена заявка с id: {}. lastPricesList: {}. stablePrice: {}.", orderId, lastPricesList, stablePrice);
            }
        }

    }


    public void sellMarket(String figi, long quantity) {
        var accountId = accountService.getAccountId();
        var orderId = accountService.getInvestApi().getSandboxService().postOrderSync(figi, quantity, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()).getOrderId();
        log.info("продана заявка с id: {}", orderId);
    }
}
