/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author ASUS
 */
public class ScheduleObserver {

    String dayENOrder[] = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
    String dayIDOrder[] = {"ahad", "senin", "selasa", "rabu", "kamis", "jumat", "sabtu"};

    int indexFound = -1;
    int manyDays = 0;
    String timeSet;
    String nowDaySet, daySet, todaySet, estimatedNextDate;
    int hour, minute;
    String hourText, minuteText;
    Date realDate, nowDate;

    // using mysql format 
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterComplete = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dayOnlyformatter = new SimpleDateFormat("EEEE");

    public ScheduleObserver() {

    }

    public String getDay(String dayIn, String formatDestinationCode) {

        String result = null;

        if (formatDestinationCode.equalsIgnoreCase("en")) {
            // to english
            result = find(dayIn, dayIDOrder, dayENOrder);
        } else {

            // to indonesian
            result = find(dayIn, dayENOrder, dayIDOrder);
        }

        return result;

    }

    private String find(String dayLook, String[] dataSource, String[] dataMatched) {
        String res = null;
        int i = 0;
        for (String hari : dataSource) {

            if (hari.equalsIgnoreCase(dayLook)) {
                res = dataMatched[i];
                break;
            }

            i++;
        }

        return res;

    }

    public void setDate(String formattedDate) {
        // input is DAY <space> TIME, for example
        // monday 12:00

        String dataRaw[] = formattedDate.split(" ");
        daySet = dataRaw[0];
        // additional for measuring differences later with precision
        timeSet = dataRaw[1] + ":00";

        String dataRaw2[] = dataRaw[1].split(":");
        hourText = dataRaw2[0];
        minuteText = dataRaw2[1];

        hour = Integer.parseInt(hourText);
        minute = Integer.parseInt(minuteText);

        nowDate = new Date();
        nowDaySet = dayOnlyformatter.format(nowDate).toLowerCase();

        manyDays = countDifferenceDay(nowDaySet, daySet);

        System.out.println("looking for " + nowDaySet + " to " + daySet + " found " + manyDays);

        Calendar c = Calendar.getInstance();
        c.setTime(nowDate); // Now use today date.
        c.add(Calendar.DATE, manyDays); // Adding 5 days

        estimatedNextDate = formatter.format(c.getTime()) + " " + timeSet;
        //UIEffect.popup(estimatedNextDate, null);

    }

    private int getIndexOf(String dayFind) {

        int val = -1;
        indexFound = -1;
        // search once more
        for (String name : dayENOrder) {
            indexFound++;
            if (name.equalsIgnoreCase(dayFind)) {
                val = indexFound;
                break;
            }
        }

        return val;
    }

    private int countDifferenceDay(String todayDay, String nextDay) {
        int val = 0;
        int indexToday = 0, indexNextDay = 0;

        indexNextDay = getIndexOf(nextDay);
        indexToday = getIndexOf(todayDay);

        // if they're in the same position
        // no difference day
        if (indexNextDay == indexToday) {
            val = 0;
            System.out.println("Same day");
        } else if (indexNextDay < indexToday) {
            val = 7 - (indexToday - indexNextDay);
            System.out.println("next day is on next week");
        } else if (indexNextDay > indexToday) {
            val = indexNextDay - indexToday;
            System.out.println("next day is on the same week");
        }

        return val;
    }

    public Date getDate() {
        Date foundDate = null;
        try {
            foundDate = formatterComplete.parse(estimatedNextDate);

        } catch (Exception e) {

        }

        return foundDate;
    }
}
