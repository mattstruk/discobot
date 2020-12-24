package disco.bot.Model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Data
public class DateAndTime {
    private String day;
    private String month;
    private String year;
    private String hour;
    private String minute;
    private String second;

    public DateAndTime() {
        String[] data = new SimpleDateFormat("HH:mm:ss:dd:MM:yyyy").format(Calendar.getInstance().getTime()).split(":");
        this.hour = data[0];
        this.minute = data[1];
        this.second = data[2];
        this.day = data[3];
        this.month = data[4];
        this.year = data[5];
    }

    public boolean isDateMatching( String day, String month ) {
        return this.day.equals(day) && this.month.equals(month);
    }

    public boolean isTimeMatching( int hour ) {
        return this.hour.equals( parseIntToString( hour ) ) && this.minute.equals("00");
    }

    public boolean isTimeMatching( int hour, int minute ) {
        return this.hour.equals( parseIntToString( hour ) ) && this.minute.equals( parseIntToString( minute ) );
    }

    public boolean isCurrentHourInList( List<Integer> list ) {
        return list.stream().anyMatch( this::isTimeMatching );
    }

    public boolean isCurrentDayAndMonthInList( List<String> monthDotDay ) {
        return monthDotDay.stream()
                .map(s -> s.split("\\."))
                .anyMatch( dayMonth -> this.isDateMatching( dayMonth[0], dayMonth[1] ) );
    }

    private String parseIntToString( int i ) {
        return StringUtils.leftPad( String.valueOf( i ), 2, "0" );
    }
}
