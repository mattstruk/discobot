package disco.bot.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final String RESET = "\033[0m";
    public static final String RED = "\033[0;31m";

    public static Date parseStringToDate( String s) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parse( s.replaceAll(",", ".").replaceAll("/", ".") );
        } catch (ParseException e) { e.printStackTrace(); }
        return null;
    }
}
