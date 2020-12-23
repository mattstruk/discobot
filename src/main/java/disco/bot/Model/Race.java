package disco.bot.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Race implements Comparable<Race>{
    int id;
    String date;
    String type;
    String name;

    @Override
    public int compareTo(Race o) {
        if (getDate() == null || o.getDate() == null) {
            return 0;
        }

        int result = getDate().substring(6).compareTo(o.getDate().substring(6));
        if (result == 0) {
            result = getDate().substring(3,5).compareTo(o.getDate().substring(3,5));;
            if (result == 0) {
                result = getDate().substring(0, 2).compareTo(o.getDate().substring(0, 2));
            };
        }
        return result;
    }

}
