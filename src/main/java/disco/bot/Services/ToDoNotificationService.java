package disco.bot.Services;

import disco.bot.Utils.Utils;
import org.apache.commons.lang3.time.DateUtils;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ToDoNotificationService {

    public static String notifications(String str, String hour) {
        String returnMessage = "";
        for (String s : str.split("\n")) {
            if ( getHoursByDaysRemaining( Math.abs( ChronoUnit.DAYS.between( extractDay( s ).toInstant(), new Date().toInstant() ) ) ).contains(  hour ) )
                returnMessage += s + "\n";
        }
        return returnMessage;
    }

    private static List<String> getHoursByDaysRemaining(long days ) {
        return days > 4 ? Collections.singletonList("18") :
               days > 3 ? Arrays.asList("12", "18") :
               days > 2 ? Arrays.asList("12", "15", "18") :
               days > 1 ? Arrays.asList("9", "12", "15", "18") :
                       Arrays.asList("9", "12", "15", "18", "21");
    }

    private static Date extractDay( String str ) {
        Date date = Utils.extractDateFromString( str );
        return date != null ? date : DateUtils.addDays(new Date(), 4);
    }
}
