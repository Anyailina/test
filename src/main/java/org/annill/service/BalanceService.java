package org.annill.service;


import java.util.HashMap;
import java.util.Map;
import org.annill.model.Transaction;

public class BalanceService {

    private final Map<String, Double> balances = new HashMap<>();

    public void processTransaction(Transaction t) {
        String user = t.user();
        switch (t.operationType()) {
            case BALANCE_INQUIRY -> balances.put(user, t.amount());
            case TRANSFERRED -> {
                String targetUser = t.targetUser();
                Double balanceUser = balances.get(user);
                Double balanceTargetUser = balances.get(targetUser);
                if (balanceUser == null) {
                    balances.put(user, null);
                    return;
                }
                if (balanceTargetUser == null) {
                    return;
                }
                balances.put(user, balanceUser - t.amount());
                balances.put(targetUser, balanceTargetUser + t.amount());
            }
            case WITHDREW -> {
                balances.compute(user, (k, balanceUser) -> balanceUser - t.amount());
            }
        }
    }

    public Map<String, Double> getBalances() {
        return balances;
    }
}
