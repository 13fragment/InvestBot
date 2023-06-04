package abc.investbot.controller;
import abc.investbot.service.AccountService;
import abc.investbot.service.SandboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController ()
public class ApplicationController {
    private final SandboxService sandboxService;
    private static final Logger log = LoggerFactory.getLogger(SandboxService.class);

    public ApplicationController(AccountService accountService, SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }
    @GetMapping("/portfolio")
    public String portfolioRequest(){
        return String.valueOf(sandboxService.getPortfolio());
    }
}
