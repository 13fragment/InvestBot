package abc.investbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SandboxService {
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);
    private final AccountService accountService;

    public SandboxService(AccountService accountService) {
        this.accountService = accountService;
    }
    public String getPortfolio(){
        log.info("Сведения о портфолио: ");
        return String.valueOf(accountService.getInvestApi().getSandboxService().getPortfolioSync(accountService.getAccountId()));
    }
}
