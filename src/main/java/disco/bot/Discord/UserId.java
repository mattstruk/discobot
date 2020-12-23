package disco.bot.Discord;

public enum UserId {
    LUKA        ("474322029856686080"),
    JARZOMB     ("408729434229833729"),
    DUBSON      ("633416304954441758"),
    LUCJAN      ("320695302841434112"),
    MANIAK      ("658052410559823882"),
    MATEUSZ     ("471116345610862595");

    private final String id;

    UserId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
