package disco.bot.Model;

import disco.bot.Utils.Utils;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class StringAndDate {
    String string;
    Date date;
    boolean isBeforeDate;

    public StringAndDate( String s ) {
        this.string = s;
        this.date = Utils.extractDateFromString( s );
        this.isBeforeDate = hasDatePassed( this.date );
        removeUnderlineAndExclamationSign();
    }

    private boolean hasDatePassed( Date date ) {
        return date == null || new Date().before(date);
    }

    private void removeUnderlineAndExclamationSign() {
        this.string = this.string.replace("__", "")
                                 .replace("❗", "");
    }
}
