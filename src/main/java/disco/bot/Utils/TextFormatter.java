package disco.bot.Utils;

public class TextFormatter {

    private static final String BOLD = "**";
    private static final String CODE_BLOCK = "```";
    private static final String UNDERLINE = "__";

    public static String boldWrapper( String str ) {
        return wrapper( BOLD, str );
    }

    public static String codeBlockWrapper( String str ) {
        return wrapper( CODE_BLOCK, str );
    }

    public static String underlineWrapper( String str ) {
        return wrapper( UNDERLINE, str );
    }

    private static String wrapper( String wrapper, String string ) {
        return wrapper + string + wrapper;
    }
}
