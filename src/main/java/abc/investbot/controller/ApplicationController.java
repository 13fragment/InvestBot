package abc.investbot.controller;
import abc.investbot.service.SandboxService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController ()
@RequestMapping("/sandbox")
public class ApplicationController {
    private final SandboxService sandboxService;

    public ApplicationController(SandboxService sandboxService) {
        this.sandboxService = sandboxService;
    }
    @GetMapping("/portfolio")
    public String portfolioRequest(){
        return String.valueOf(sandboxService.getPortfolio());
    }
}
