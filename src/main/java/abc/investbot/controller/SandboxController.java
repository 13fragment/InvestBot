package abc.investbot.controller;

import abc.investbot.model.RSIStrategyConfig;
import abc.investbot.service.SandboxService;
import abc.investbot.service.OrderService;
import abc.investbot.starategy.CandelHistory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController()
@RequestMapping("/sandbox")
public class SandboxController {
    private final SandboxService sandboxService;
    private final OrderService orderService;
    private final CandelHistory candelHistory;

    public SandboxController(SandboxService sandboxService, OrderService orderService, @Value("${app.trading.figi.SBER}") String figi, @Value("${app.trading.quantity}") long quantity, CandelHistory candelHistory) {
        this.sandboxService = sandboxService;
        this.orderService = orderService;
        //orderService.buyMarket(figi, quantity);
        //orderService.sellMarket(figi, quantity);
        this.candelHistory = candelHistory;
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

    // загадка жака фреско на GetMapping и PostMapping
    @PostMapping("/rsi") // или @GetMapping("/rsi")
    public List<RSIStrategyConfig> start(@RequestBody List<RSIStrategyConfig> configs) throws InterruptedException {
        candelHistory.initCache(configs);
        return configs;
    }
}
