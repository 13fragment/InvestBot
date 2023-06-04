package abc.investbot.service;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService{
    private final Map<String, String> figi = new HashMap<String, String>();
    private final AccountService accountService;
    public OrderService(AccountService accountService) {
        this.accountService = accountService;
    }
}
