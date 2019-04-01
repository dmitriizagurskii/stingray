package com.hellokoding.auth.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateService {



    public static String getDateDiff(Calendar deadlineDate) {
        Date date2 = deadlineDate.getTime();
        Date date1 = Calendar.getInstance().getTime();
        TimeUnit unit = TimeUnit.MINUTES;
        long diffInMillies = date2.getTime() - date1.getTime();
        long diff = unit.convert(diffInMillies,TimeUnit.MILLISECONDS);

        return showRestTime(convertMinutes(diff));
    }


    public DateService() {}

    public static Calendar convertToCalendar(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date actualDate = format.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(actualDate);
        return calendar;
    }

    public static String viewDate(Calendar date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
        return format.format(date.getTime());
    }

    private static Map<String, Long> convertMinutes (long minutes) {
        Map<String, Long> map = new HashMap<>();
        long hours = minutes / 60;
        long pureMinutes;
        long pureHours;
        if (hours > 0) {
            pureMinutes = minutes % 60;
            map.put("Minutes", pureMinutes);
        }
        else {
            map.put("Minutes", minutes);
            return map;
        }
        long days = hours / 24;
        if (days > 0) {
            pureHours = hours % 24;
            map.put("Hours", pureHours);
            map.put("Days", days);
            return map;
        }
        else {
            map.put("Hours", hours);
            return map;
        }

    }

    private static String showRestTime(Map<String, Long> map) {
        String output;
        switch (map.size()) {
            case 1: {
                output =" 00:"+map.get("Minutes").toString();
                break;
            }
            case 2: {
                output = map.get("Hours").toString() + ":" +map.get("Minutes").toString();
                break;
            }
            case 3: {
                output = map.get("Days").toString() + " day(s), " + map.get("Hours").toString() + ":" +map.get("Minutes").toString();
                break;
            }
            default: {
                output = "ERROR";
                break;
            }
        }
        return output;
    }




}

