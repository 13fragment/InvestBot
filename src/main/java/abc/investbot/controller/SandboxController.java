package abc.investbot.controller;

import abc.investbot.service.SandboxService;
import abc.investbot.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController()
@RequestMapping("/sandbox")
public class SandboxController {
    private final SandboxService sandboxService;
    private final OrderService orderService;

    @Value("${app.trading.quantity}")
    private long quantity;

    @Value("${app.trading.figi.SBER}")
    private String figi;

    public SandboxController(SandboxService sandboxService, OrderService orderService) {
        this.sandboxService = sandboxService;
        this.orderService = orderService;
        orderService.buyMarket("BBG004730N88", 1);
    }

    @GetMapping(value = "/portfolio")
    public String portfolioRequest() {
        return String.valueOf(sandboxService.getPortfolio());
    }

    @GetMapping(value = "/receipt/{sum}")
    public String receipt(@PathVariable long sum) {
        sandboxService.addMoney(sum);
        return "Баланс был пополнен на " + sum + " RUB";
    }

    @GetMapping(value = "/orders")
    public String ordersRequest() {
        return String.valueOf(sandboxService.orders());
    }

    @GetMapping(value = "/orders/{orderId}")
    public String orderStateRequest(@PathVariable String orderId) {
        return String.valueOf(sandboxService.orderState(orderId));
    }
}
