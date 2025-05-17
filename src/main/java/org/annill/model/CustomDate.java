package org.annill.model;

import java.time.LocalDateTime;
import java.util.Objects;
// Класс был создан так как если в разных файлах одинаковое время в логах, то они затирались
// Класс LocalDateTime не подходит так как там переопределен метод equals
public record CustomDate(LocalDateTime time) implements Comparable<CustomDate> {

    @Override
    public int compareTo(CustomDate other) {
        Objects.requireNonNull(other, "Сравниваемый объект не может быть null");
        return this.time.compareTo(other.time);
    }
}