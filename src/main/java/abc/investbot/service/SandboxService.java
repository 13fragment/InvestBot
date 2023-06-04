package abc.investbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.MoneyValue;

import java.util.concurrent.CompletableFuture;

@Service
public class SandboxService {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final AccountService accountService;

    public SandboxService(AccountService accountService) {
        this.accountService = accountService;
    }
    public String getPortfolio(){
        log.info("Получена информация о портфеле: ");
        return String.valueOf(accountService.getInvestApi().getSandboxService().getPortfolioSync(accountService.getAccountId()));
    }
    public CompletableFuture<MoneyValue> addMoney(long sum){
        log.info("Портфель был пополнен на "+sum+" RUB");
        return accountService.getInvestApi().getSandboxService().payIn(accountService.getAccountId(), MoneyValue.newBuilder().setUnits(sum).setCurrency("RUB").build());
    }
}
