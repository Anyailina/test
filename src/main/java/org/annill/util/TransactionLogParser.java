package org.annill.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.annill.model.Transaction;
import org.annill.model.OperationType;

public final class TransactionLogParser {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^\\[(.*?)]\\s" +
            "(\\w+)\\s" +
            "(" + OperationType.getPatternString() + ")\\s" +
            "(\\d+(?:\\.\\d+)?)" +
            "(?:\\s+to\\s+(\\w+))?$");

    private TransactionLogParser() {
    }

    public static Transaction parseLogLine(String logLine) {
        if (logLine == null || logLine.isBlank()) {
            throw new IllegalArgumentException("Строка лога не может быть пустой");
        }

        Matcher matcher = LOG_PATTERN.matcher(logLine.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неправильный формат строки лога: " + logLine);
        }

        LocalDateTime timestamp = parseTimestamp(matcher.group(1));
        String user = matcher.group(2);
        OperationType operation = OperationType.fromString(matcher.group(3));
        double amount = parseAmount(matcher.group(4));
        String targetUser = matcher.group(5);

        return createTransaction(timestamp, user, operation, amount, targetUser, logLine);
    }

    private static LocalDateTime parseTimestamp(String timestampStr) {
        try {
            return LocalDateTime.parse(timestampStr, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Неправильный формат времени: " + timestampStr);
        }
    }

    private static double parseAmount(String amountStr) {
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неправильный формат суммы: " + amountStr);
        }
    }

    private static Transaction createTransaction(
        LocalDateTime timestamp,
        String user,
        OperationType operation,
        double amount,
        String targetUser,
        String originalLogLine) {

        return switch (operation) {
            case BALANCE_INQUIRY -> new Transaction(timestamp, user, operation, amount, null);

            case TRANSFERRED -> {
                if (targetUser == null) {
                    throw new IllegalArgumentException(
                        "Отсутствует получатель для перевода: " + originalLogLine);
                }
                yield new Transaction(timestamp, user, operation, amount, targetUser);
            }

        };
    }

}
