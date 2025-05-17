package org.annill.model;

import java.time.LocalDateTime;
import org.annill.util.DateFormatUtils;

public record Transaction(LocalDateTime timestamp, String user, OperationType operationType, double amount,
                          String targetUser) {

    @Override
    public String toString() {
        String action = "";
        switch (operationType) {
            case TRANSFERRED -> action = "transferred " + amount + " to " + targetUser;
            case BALANCE_INQUIRY -> action = "balance inquiry " + amount;
            case WITHDREW -> action = "withdrew " + amount;
        }

        return String.format("[%s] %s %s", DateFormatUtils.getFormattedTimestamp(timestamp), user, action);
    }
}