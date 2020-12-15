package disco.bot.Model;

public class Race implements Comparable<Race>{
    int id;
    String date;
    String type;
    String name;

    public Race(int id, String date, String type, String name) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
