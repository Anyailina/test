package org.annill.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Transaction(
    LocalDateTime timestamp,
    String user,
    OperationType operationType,
    double amount,
    String targetUser) {

    public static final DateTimeFormatter LOG_FORMATTER =
        DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");

    public String getFormattedTimestamp() {
        return timestamp.format(LOG_FORMATTER);
    }

    @Override
    public String toString() {
        String action;
        if (operationType == OperationType.BALANCE_INQUIRY) {
            action = "balance inquiry " + amount;
        } else if (operationType == OperationType.TRANSFERRED) {
            action = "transferred " + amount + " to " + targetUser;
        } else {
            action = "не";
        }

        return String.format(
            "[%s] %s %s",
            getFormattedTimestamp(),
            user,
            action
        );
    }
}