package disco.bot.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";

    public static Date parseStringToDate( String s ) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parse( s.replaceAll(",", ".").replaceAll("/", ".") );
        } catch (ParseException e) { e.printStackTrace(); }
        return null;
    }

    public static long getDayDifference( Date date ) {
        return Math.abs( ChronoUnit.DAYS.between( date.toInstant(), new Date().toInstant() ) );
    }

    public static Date extractDateFromString( String str ) {
        String regex = "((\\d{1,2}-\\d{1,2}-\\d{4})|(\\d{1,2}\\.\\d{1,2}\\.\\d{4})|(\\d{1,2}/\\d{2}/\\d{4}))";
        Matcher matcher = Pattern.compile(regex).matcher( str );
        return matcher.find()
                ? Utils.parseStringToDate( matcher.group(0) )
                : null;
    }
}
