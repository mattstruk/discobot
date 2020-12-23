package disco.bot.Utils;

public class TextFormatter {

    private static final String BOLD = "**";
    private static final String CODE_BLOCK = "```";

    public static String boldWrapper( String str ) {
        return wrapper( BOLD, str );
    }

    public static String codeBlockWrapper( String str ) {
        return wrapper( CODE_BLOCK, str );
    }

    private static String wrapper( String wrapper, String string ) {
        return wrapper + string + wrapper;
    }
}
