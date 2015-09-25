package com.spruds.transport.spider.model;

public enum DayType {
    MONDAY_FRIDAY(31),
    SATURDAY(32),
    SUNDAY(64),
    SATURDAY_SUNDAY(96),
    EVERY_DAY(127);

    private int value;
    
    DayType(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name();
    }

    public int getValue() {
        return value;
    }
}
