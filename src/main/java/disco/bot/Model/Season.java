package disco.bot.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Season {
    int id;
    String name;
    List<Race> races;
    List<String> usersToNotify;

    public Season(int id, String name) {
        this.id = id;
        this.name = name;
    }
}