package abc.investbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.tinkoff.piapi.core.InvestApi;
@Configuration
public class AccountService {
    private InvestApi investApi;
    private final String accountId;
    /** Create setter for sandboxAccount and getter for investApi */
    public AccountService(@Value("${app.config.token}") String token, @Value("${app.config.sandbox-mode}") boolean sandboxMode, @Value("${app.config.account-id}") String accountId)
    {
        this.accountId = accountId;
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token is not defined");
        }
        if (sandboxMode){
            investApi = InvestApi.createSandbox(token);
        }
    }
    public InvestApi getInvestApi() {
        return investApi;
    }

    public String getAccountId() {
        return accountId;
    }
}
