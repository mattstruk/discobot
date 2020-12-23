package disco.bot;

import disco.bot.Discord.ChannelId;
import disco.bot.Discord.Reaction;
import disco.bot.Discord.UserId;
import disco.bot.Services.*;
import disco.bot.Services.Web.ACLParser;
import disco.bot.Utils.Discord;
import disco.bot.Utils.TextFormatter;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class JavacordBot {

    public static final String BOT_ID = "768738709805465621";
    public static DiscordApi api;

    static {
        try { api = DiscordMessageService.init(); } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) throws IOException {
        SpringApplication.run(JavacordBot.class, args);

        List<String> users = Arrays.asList("405052679182745620", "320695302841434112");
        List<Reaction> defaultReactions = Arrays.asList( Reaction.THUMBS_UP , Reaction.THUMBS_DOWN, Reaction.WHATEVER, Reaction.FACEPALM );
        List<Reaction> lukaReactions = Arrays.asList( Reaction.TRACTOR, Reaction.MIDDLE_FINGER, Reaction.MIDDLE_FINGER_TONE_1, Reaction.MIDDLE_FINGER_TONE_2, Reaction.MIDDLE_FINGER_TONE_3, Reaction.MIDDLE_FINGER_TONE_4, Reaction.MIDDLE_FINGER_TONE_5, Reaction.POOP, Reaction.TOILET );
        List<String> maser = Arrays.asList("Lucjan Próchnica", "Adam Dubiel", "Patryk Pyrchla", "Paweł Jarzębowski", "Mateusz Książek");
        final boolean[] lukaFuckLever = {false};
        final int[] currentWEKRound = {3};
        String CHANNEL_DIVIDER = ":";


        api.addMessageCreateListener(event -> {
            // alphabetical order
            if ( Discord.contains( event, "!dodajwyscig" ) )    DiscordMessageService.createMessage( event, DiscordMessageService.modifyOrReplaceLastMessage( api, ChannelId.POGADANKI, event, "Dodano zawartość do kalendarza" ) );
            if ( Discord.contains( event, "dodajzadanie" ) )    DiscordMessageService.createMessage( event, DiscordMessageService.modifyOrReplaceLastMessage( api, ChannelId.POGADANKI, event, "Dodano zadanie") );
            if ( Discord.equals(   event, "!dubson" ) )         DiscordMessageService.createMessage( event, MiddleFingerAlphabetService.printFuckerText( "dubson" ) );
            if ( Discord.contains( event, "!fuckertext" ) )     DiscordMessageService.createMessage( event, FuckerTextResolver.convertTextToFuckers( Discord.getMsg( event ) ) );
            if ( Discord.equals(   event, "!luka" ) )           DiscordMessageService.createMessage( event, MiddleFingerAlphabetService.printFuckerText( "luka") );
            if ( Discord.equals(   event, "!lukafuck" ) )       DiscordMessageService.createMessage( event, FuckerTextResolver.resolveLukaFuckAndGetMessage( lukaFuckLever, event ) );
            if ( Discord.equals(   event, "!maser" ) )          DiscordMessageService.createMessage( event, RandomizerService.getRandomDriver( maser ) );
            if ( Discord.equals(   event, "!roll" ) )           DiscordMessageService.createMessage( event, RandomizerService.rollTheDice( Discord.getMsgAuthor( event ) ) );
            if ( Discord.contains( event, "!setround" ) )       DiscordMessageService.deleteMessageAndSetRound( event, currentWEKRound );

            try {
                if ( Discord.equals( event, "!generalkagt4" ) ) DiscordMessageService.createMessage( event, TextFormatter.codeBlockWrapper( ACLParser.getACLGT4TeamStandings() + ACLParser.getACLGT4DriverStandings() ) );
                if ( Discord.equals( event, "!preqwek" ) )      DiscordMessageService.createMessage( event, TextFormatter.codeBlockWrapper( ACLParser.getACLWEKPreq(currentWEKRound[0]) ) );
            } catch (IOException e) { e.printStackTrace(); }

            if ( Discord.compareChannels( ChannelId.PRIV_DIRECT.getId(), event ) ) DiscordMessageService.createMessage( api, ChannelIdResolver.getChannel( event, CHANNEL_DIVIDER ), StringUtils.substringAfter( Discord.getMsg( event ), CHANNEL_DIVIDER) );

            DiscordReactionService.addDefaultVotingReactions( event, ChannelId.ANKIETY.getId(), defaultReactions );
        });

        api.addReactionAddListener(event -> {

            if ( lukaFuckLever[0] ) DiscordReactionService.addFuckersForUserMessages( event, UserId.LUKA, lukaReactions );
            DiscordReactionService.removeForbiddenReactions( defaultReactions, event, ChannelId.ANKIETY.getId() );
            DiscordReactionService.removeRedundantReactions( event,  ChannelId.ANKIETY.getId() );
            DiscordReactionService.makeAnnouncementWhenAllVoted( event, ChannelId.ANKIETY.getId(), ChannelId.OGLOSZENIA.getId(), users, "Wyniki ankiety pt. ", api );

        });
    }
}
