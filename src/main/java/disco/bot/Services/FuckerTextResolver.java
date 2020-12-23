package disco.bot.Services;

import disco.bot.Discord.Reaction;
import disco.bot.Utils.Discord;
import org.javacord.api.event.message.MessageCreateEvent;

public class FuckerTextResolver {

    public static String resolveLukaFuckAndGetMessage( boolean[] lukaFuckLever, MessageCreateEvent event ) {
        lukaFuckLever[0] = !lukaFuckLever[0];
        String paramStatus = lukaFuckLever[0] ? "włączona" : "wyłączona";
        String statusText = String.format("Opcja !lukafuck została %s.", paramStatus);
        String lukaDisclaimer = "Luka, wszyscy inni mogą tym sterować, prócz Ciebie. Bądź miły dla cumpli!";
        return Discord.getMsgAuthor( event ).contains("Luka") ? lukaDisclaimer : statusText;
    }

    public static String convertTextToFuckers( String message ) {
        String fuckerText = MiddleFingerAlphabetService.printFuckerText( message );
        return fuckerText.length() >= 2000
                ? "Wyszło ponad 2000 znaków :( " + Reaction.MIDDLE_FINGER.getValue()
                : fuckerText;
    }
}
