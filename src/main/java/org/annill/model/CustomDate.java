package org.annill.model;

import java.time.LocalDateTime;
import java.time.Month;

public final class CustomDate implements Comparable<CustomDate> {

    private final int year;
    private final Month month;
    private final int day;
    private final int hour;
    private final int minute;
    private final int second;

    public CustomDate(LocalDateTime localDateTime) {
        this.year = localDateTime.getYear();
        this.month = localDateTime.getMonth();
        this.day = localDateTime.getDayOfMonth();
        this.hour = localDateTime.getHour();
        this.minute = localDateTime.getMinute();
        this.second = localDateTime.getSecond();
    }

    @Override
    public int compareTo(CustomDate other) {
        if (this.year != other.year) {
            return Integer.compare(this.year, other.year);
        }
        if (this.month != other.month) {
            return this.month.compareTo(other.month);
        }
        if (this.day != other.day) {
            return Integer.compare(this.day, other.day);
        }
        if (this.hour != other.hour) {
            return Integer.compare(this.hour, other.hour);
        }
        if (this.minute != other.minute) {
            return Integer.compare(this.minute, other.minute);
        }
        return Integer.compare(this.second, other.second);
    }


}