package org.annill.util;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.annill.model.OperationType;
import org.annill.model.Transaction;

public final class TransactionLogParser {

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^\\[(.+?)]\\s+" +               // Временная метка
            "(\\w+)\\s+" +                  // Пользователь
            "(\\w+(?:\\s+\\w+)?)\\s+" +     // Тип операции (может быть из двух слов, напр. "balance inquiry")
            "(\\d+(?:\\.\\d+)?)" +          // Сумма
            "(?:\\s+to\\s+(\\w+))?$"        // Опциональный получатель
    );

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
            return LocalDateTime.parse(timestampStr, DateFormatUtils.LOG_FORMATTER);
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
            case BALANCE_INQUIRY, WITHDREW -> new Transaction(timestamp, user, operation, amount, null);

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
