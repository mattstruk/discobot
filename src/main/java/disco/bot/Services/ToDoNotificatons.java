package disco.bot.Services;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToDoNotificatons {

    public static String notifications(String str, String hour) throws ParseException {
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

    private static Date extractDay( String str ) throws ParseException {
        String regex = "((\\d{1,2}-\\d{1,2}-\\d{4})|(\\d{1,2}\\.\\d{1,2}\\.\\d{4})|(\\d{1,2}/\\d{2}/\\d{4}))";
        Matcher m = Pattern.compile(regex).matcher( str );
        String[] dividersToBeReplaced = {".", "/"};
        return m.find() ? new SimpleDateFormat("dd-MM-yyyy").parse(
                StringUtils.replaceEach( m.group(1), dividersToBeReplaced, Collections.nCopies(dividersToBeReplaced.length, "-").toArray(new String[dividersToBeReplaced.length])) )
                : DateUtils.addDays(new Date(), 4);
    }
}
