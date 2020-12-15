package disco.bot.Model;

import java.util.List;

public class Season {
    int id;
    String name;
    List<Race> races;
    List<String> usersToNotify;

    public Season(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Race> getRaces() {
        return races;
    }

    public void setRaces(List<Race> races) {
        this.races = races;
    }

    public List<String> getUsersToNotify() {
        return usersToNotify;
    }

    public void setUsersToNotify(List<String> usersToNotify) {
        this.usersToNotify = usersToNotify;
    }
}