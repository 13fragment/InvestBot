package abc.investbot.starategy;

import abc.investbot.service.AccountService;
import org.springframework.stereotype.Service;
import ru.tinkoff.piapi.contract.v1.Instrument;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class InstrumentsHistory {
    private final Map<String, Instrument> instruments = new HashMap<>();

    private final AccountService accountService;

    public InstrumentsHistory(AccountService accountService) {
        this.accountService = accountService;
    }

    public void add(String figi) {
        var instrument = accountService.getInvestApi().getInstrumentsService().getInstrumentByFigiSync(figi);
        instruments.put(figi, instrument);
    }

    public BigDecimal getLot(String figi) {
        if (!instruments.containsKey(figi)) {
            add(figi);
        }
        var lot = BigDecimal.valueOf(instruments.get(figi).getLot());
        if (lot.equals(BigDecimal.ZERO)) {
            throw new IllegalArgumentException("лот не может быть равен 0. figi: " + figi);
        }
        return lot;
    }
}
