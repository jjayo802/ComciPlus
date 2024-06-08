package com.jjayo802.comciplus;

public class DateUtils {

    public static String getWeekDayString(int dateOfWeek){
        return switch (dateOfWeek) {
            case 1 -> "월";
            case 2 -> "화";
            case 3 -> "수";
            case 4 -> "목";
            case 5 -> "금";
            default -> "?";
        };
    }
}
