package disco.bot;

import disco.bot.Discord.ChannelId;
import disco.bot.Discord.Reaction;
import disco.bot.Discord.UserId;
import disco.bot.Services.*;
import disco.bot.Services.Web.ACLParser;
import disco.bot.Utils.Discord;
import disco.bot.Utils.TextFormatter;
import disco.bot.Utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class JavacordBot {

    public static final String BOT_ID = "768738709805465621";
    public static DiscordApi api;

    static { try { api = DiscordMessageService.init(); System.out.println(Utils.RED + "Wersja: Discobot Javacord " + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(Calendar.getInstance().getTime()) + Utils.RESET); } catch (IOException e) { e.printStackTrace(); } }

    public static void main(String[] args) {
        SpringApplication.run(JavacordBot.class, args);

        List<UserId> users = Arrays.asList( UserId.DUBSON, UserId.JARZOMB, UserId.LUKA, UserId.MANIAK, UserId.MATEUSZ, UserId.LUCJAN );
        List<Reaction> defaultReactions = Arrays.asList( Reaction.THUMBS_UP , Reaction.THUMBS_DOWN, Reaction.WHATEVER, Reaction.FACEPALM );
        List<Reaction> lukaReactions = Arrays.asList( Reaction.TRACTOR, Reaction.MIDDLE_FINGER, Reaction.MIDDLE_FINGER_TONE_1, Reaction.MIDDLE_FINGER_TONE_2, Reaction.MIDDLE_FINGER_TONE_3, Reaction.MIDDLE_FINGER_TONE_4, Reaction.MIDDLE_FINGER_TONE_5, Reaction.POOP, Reaction.TOILET );
        List<String> maser = Arrays.asList("Lucjan Próchnica", "Adam Dubiel", "Patryk Pyrchla", "Paweł Jarzębowski", "Mateusz Książek");
        final boolean[] lukaFuckLever = {false};
        String CHANNEL_DIVIDER = ":";


        api.addMessageCreateListener(event -> {
            // alphabetical order
            if ( Discord.compareChannels( ChannelId.KOMENDY.getId(), event ) )
                return;

            if ( Discord.contains( event, "!dodajwyscig" ) )    DiscordMessageService.createMessage( event, DiscordMessageService.modifyOrReplaceLastMessage( ChannelId.POGADANKI, event, "Dodano zawartość do kalendarza" ) );
            if ( Discord.contains( event, "dodajzadanie" ) )    DiscordMessageService.createMessage( event, DiscordMessageService.modifyOrReplaceLastMessage( ChannelId.POGADANKI, event, "Dodano zadanie") );
            if ( Discord.equals(   event, "!dubson" ) )         DiscordMessageService.createMessage( event, MiddleFingerAlphabetService.printFuckerText( "dubson" ) );
            if ( Discord.contains( event, "!fuckertext" ) )     DiscordMessageService.createMessage( event, FuckerTextResolver.convertTextToFuckers( Discord.getMsg( event ) ) );
            if ( Discord.contains( event,"!losuj kierowcy:" ) ) DiscordMessageService.createMessage( event, RandomizerService.pickRandomTeams( event ) );
            if ( Discord.equals(   event, "!luka" ) )           DiscordMessageService.createMessage( event, MiddleFingerAlphabetService.printFuckerText( "luka") );
            if ( Discord.equals(   event, "!lukafuck" ) )       DiscordMessageService.createMessage( event, FuckerTextResolver.resolveLukaFuckAndGetMessage( lukaFuckLever, event ) );
            if ( Discord.equals(   event, "!maser" ) )          DiscordMessageService.createMessage( event, RandomizerService.getRandomDriver( maser ) );
            if ( Discord.equals(   event, "!roll" ) )           DiscordMessageService.createMessage( event, RandomizerService.rollTheDice( Discord.getMsgAuthor( event ) ) );

            try {
                if ( Discord.equals( event, "!generalkagt4" ) ) DiscordMessageService.createMessage( event, TextFormatter.codeBlockWrapper( ACLParser.getACLGT4TeamStandings() + ACLParser.getACLGT4DriverStandings() ) );
                if ( Discord.equals( event, "!preqwek" ) )      DiscordMessageService.createMessage( event, TextFormatter.codeBlockWrapper( ACLParser.getACLWEKPreq() ) );
            } catch (IOException e) { e.printStackTrace(); }

            if ( Discord.compareChannels( ChannelId.PRIV_DIRECT.getId(), event ) ) DiscordMessageService.createMessage( ChannelIdResolver.getChannel( event, CHANNEL_DIVIDER ), StringUtils.substringAfter( Discord.getMsg( event ), CHANNEL_DIVIDER) );
            if ( lukaFuckLever[0] ) DiscordMessageService.addFuckersForUserMessages( event, UserId.LUKA );
            if (  Stream.of("maser", "maserati", "itaresam", "masser").anyMatch( event.getMessageContent()::contains )) DiscordMessageService.addReactionsForEachKeyword( event, UserId.LUKA, lukaReactions );

            DiscordReactionService.addDefaultVotingReactions( event, ChannelId.ANKIETY.getId(), defaultReactions );
        });

        api.addReactionAddListener(event -> {

            DiscordReactionService.removeForbiddenReactions( defaultReactions, event, ChannelId.ANKIETY.getId() );
            DiscordReactionService.removeRedundantReactions( event,  ChannelId.ANKIETY.getId() );
            DiscordReactionService.makeAnnouncementWhenAllVoted( event, ChannelId.ANKIETY.getId(), ChannelId.OGLOSZENIA.getId(), users, "Wyniki ankiety pt. " );

        });

        api.addReactionRemoveListener( DiscordReactionService::undoLockedReactionRemove );
    }
}
