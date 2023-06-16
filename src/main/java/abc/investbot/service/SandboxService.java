package abc.investbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MoneyValue;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.PortfolioResponse;

import java.util.List;

@Service
public class SandboxService {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final AccountService accountService;

    public SandboxService(AccountService accountService) {
        this.accountService = accountService;
    }

    public PortfolioResponse getPortfolio() {
        log.info("Получена информация о портфеле: ");
        return accountService.getInvestApi().getSandboxService().getPortfolioSync(accountService.getAccountId());
    }

    public void addMoney(long sum) {
        log.info("Портфель был пополнен на " + sum + " RUB");
        accountService.getInvestApi().getSandboxService().payInSync(accountService.getAccountId(), MoneyValue.newBuilder().setUnits(sum).setCurrency("RUB").build());
    }

    public List<OrderState> orders() {
        return accountService.getInvestApi().getSandboxService().getOrdersSync(accountService.getAccountId());
    }

    public OrderState orderState(String orderId) {
        return accountService.getInvestApi().getSandboxService().getOrderStateSync(accountService.getAccountId(), orderId);
    }
}
