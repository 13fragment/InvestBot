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

    public SandboxController(SandboxService sandboxService, OrderService orderService, @Value("${app.trading.figi.SBER}") String figi, @Value("${app.trading.quantity}") long quantity) {
        this.sandboxService = sandboxService;
        this.orderService = orderService;
        orderService.buyMarket(figi, quantity);
        orderService.sellMarket(figi, quantity);
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
        return String.valueOf(orderService.ordersRequest());
    }

    @GetMapping(value = "/orders/{orderId}")
    public String orderStateRequest(@PathVariable String orderId) {
        return String.valueOf(sandboxService.orderState(orderId));
    }
}
