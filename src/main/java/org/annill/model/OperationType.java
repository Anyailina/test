package org.annill.model;

public enum OperationType {
    BALANCE_INQUIRY("balance inquiry"),
    TRANSFERRED("transferred");

    private final String text;

    OperationType(String text) {
        this.text = text;
    }

    public static OperationType fromString(String text) {
        for (OperationType op : OperationType.values()) {
            if (op.text.equalsIgnoreCase(text)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Неизвестная операция: " + text);
    }

    public static String getPatternString() {
        return String.join("|",
            BALANCE_INQUIRY.text,
            TRANSFERRED.text);
    }
}
