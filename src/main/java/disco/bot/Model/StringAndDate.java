package disco.bot.Model;

import disco.bot.Utils.Utils;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@ToString
public class StringAndDate {
    String string;
    Date date;
    boolean isBeforeDate;

    public StringAndDate( String s ) {
        this.string = s;
        this.date = getDateFromString();
        this.isBeforeDate = hasDatePassed( this.date );
        removeUnderlineAndExclamationSign();
    }

    private Date getDateFromString( ) {
        String regex = "((\\d{1,2}-\\d{1,2}-\\d{4})|(\\d{1,2}\\.\\d{1,2}\\.\\d{4})|(\\d{1,2}/\\d{2}/\\d{4}))";
        Matcher matcher = Pattern.compile(regex).matcher( this.string );
        return matcher.find()
                    ? Utils.parseStringToDate( matcher.group(0) )
                    : null;
    }

    private boolean hasDatePassed( Date date ) {
        return date == null || new Date().before(date);
    }

    private void removeUnderlineAndExclamationSign() {
        this.string = this.string.replace("__", "")
                                 .replace("❗", "");
    }
}
