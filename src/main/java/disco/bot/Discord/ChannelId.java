package disco.bot.Discord;

public enum ChannelId {
    ANKIETY     ("767774988354453515"),
    TODO        ("767780095846776833"),
    LINKS       ("735196193964949606"),
    POGADANKI   ("700694946503720992"),
    ACL_WEK     ("778000167516373012"),
    OGLOSZENIA  ("700670367131500624"),
    KALENDARZ   ("767789225458270227"),
    PRIV_DIRECT ("784106290523537438"),
    TEST_CHNL   ("787522026348478484"),
    TEST_TODO   ("787553581208961044"),
    SIM_GRID    ("767804272057909258"),
    TEST        ("768847735095820369");

    private final String id;

    ChannelId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
