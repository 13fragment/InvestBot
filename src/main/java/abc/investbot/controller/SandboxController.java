package abc.investbot.controller;
import abc.investbot.service.SandboxService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController ()
@RequestMapping("/sandbox")
public class SandboxController {
    private final SandboxService sandboxService;
    public SandboxController(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }
    @GetMapping(value ="/portfolio")
    public String portfolioRequest(){
        return String.valueOf(sandboxService.getPortfolio());
    }
    @GetMapping(value = "/receipt/{sum}")
    public String receipt(@PathVariable long sum) {
        sandboxService.addMoney(sum);
        return "Баланс был пополнен на "+sum+" RUB";
    }
}
