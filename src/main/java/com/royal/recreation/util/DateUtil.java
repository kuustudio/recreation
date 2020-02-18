package com.royal.recreation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DateTimeFormatter QUERY_DATE__FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter QUERY_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static long dateTimeToMilliSecond(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static int dateToTime(LocalDate date) {
        return date.getYear() * 10000 + date.getMonthValue() * 100 + date.getDayOfMonth();
    }

    public static int dateTimeToTime(LocalDateTime dateTime) {
        return dateTime.getYear() * 10000 + dateTime.getMonthValue() * 100 + dateTime.getDayOfMonth();
    }

    public static LocalDateTime secondToDateTime(long second) {
        return LocalDateTime.ofEpochSecond(second, 0, ZoneOffset.ofHours(8));
    }

}
