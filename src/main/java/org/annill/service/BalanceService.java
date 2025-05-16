package org.annill.service;



import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.annill.model.Transaction;

public class BalanceService {
    private final Map<String, Double> balances = new HashMap<>();

    public void processTransaction(Transaction t) {
        switch (t.operationType()) {
            case BALANCE_INQUIRY -> balances.put(t.user(), t.amount());
            case TRANSFERRED -> {
                balances.computeIfAbsent(t.user(), k -> 0.0);
                balances.computeIfAbsent(t.targetUser(), k -> 0.0);
                balances.compute(t.user(), (k, v) -> v - t.amount());
                balances.compute(t.targetUser(), (k, v) -> v + t.amount());
            }
        }
    }

    public Map<String, Double> getBalances() {
        return Collections.unmodifiableMap(balances);
    }
}
