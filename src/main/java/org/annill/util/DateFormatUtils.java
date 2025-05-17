package org.annill.util;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatUtils {

    public static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");

    public static String getFormattedTimestamp(LocalDateTime localDateTime) {
        return localDateTime.format(LOG_FORMATTER);
    }
}
