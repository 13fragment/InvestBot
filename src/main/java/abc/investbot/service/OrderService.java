package abc.investbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final Map<String, String> figi = new HashMap<String, String>();
    private final AccountService accountService;

    public OrderService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void buyMarket(String figi, long quantity) {
        var accountId = accountService.getAccountId();
        var orderId = accountService.getInvestApi().getSandboxService().postOrderSync(figi, quantity, Quotation.getDefaultInstance(), OrderDirection.ORDER_DIRECTION_BUY, accountId, OrderType.ORDER_TYPE_MARKET,
                UUID.randomUUID().toString()).getOrderId();
        log.info("выставлена заявка с id: {}", orderId);
    }
}
