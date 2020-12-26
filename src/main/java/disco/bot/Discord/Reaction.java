package disco.bot.Discord;

public enum Reaction {
    MIDDLE_FINGER        ("\uD83D\uDD95"),
    MIDDLE_FINGER_TONE_1 ("\uD83D\uDD95\uD83C\uDFFB"),
    MIDDLE_FINGER_TONE_2 ("\uD83D\uDD95\uD83C\uDFFC"),
    MIDDLE_FINGER_TONE_3 ("\uD83D\uDD95\uD83C\uDFFD"),
    MIDDLE_FINGER_TONE_4 ("\uD83D\uDD95\uD83C\uDFFE"),
    MIDDLE_FINGER_TONE_5 ("\uD83D\uDD95\uD83C\uDFFF"),
    POOP                 ("\uD83D\uDCA9"),
    TRACTOR              ("\uD83D\uDE9C"),
    TOILET               ("\uD83D\uDEBD"),
    THUMBS_UP            ("\uD83D\uDC4D"),
    THUMBS_DOWN          ("\uD83D\uDC4E"),
    WHATEVER             ("\uD83E\uDD37\u200D♂️"),
    FACEPALM             ("\uD83E\uDD26\u200D♂️"),
    LOCKED               ("\uD83D\uDD10"),
    EXCLAMATION          ("❗");

    private final String id;

    Reaction(String id) {
        this.id = id;
    }

    public String getValue() {
        return this.id;
    }
}
