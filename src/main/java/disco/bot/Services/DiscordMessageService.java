package disco.bot.Services;

import disco.bot.Discord.Reaction;
import disco.bot.Discord.UserId;
import disco.bot.JavacordBot;
import disco.bot.Utils.Discord;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DiscordMessageService {

    public static DiscordApi init() throws IOException {
        return new DiscordApiBuilder()
                .setToken( Files.readAllLines( Paths.get(System.getProperty("user.dir") + "/credentials.txt") ).get(0) )
                .login().join();
    }

    public static void createMessage( MessageCreateEvent event, String message ) {
        if ( StringUtils.isNotBlank( message ) )
            event.getChannel().sendMessage( message );
    }

    public static void createMessage( String channel, String message ) {
        if ( StringUtils.isNotBlank( message ) )
            JavacordBot.api.getChannelById( channel ).get().asServerTextChannel().get().sendMessage( message );
    }

    public static void deleteMessage(MessageCreateEvent event ) {
        try {
            event.getChannel().getMessageById( event.getMessageId() ).get().delete();
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
    }

    public static String modifyOrReplaceLastMessage( String channelId, MessageCreateEvent event, String output  ) {
        String finalMessage = Discord.getMsg( event ) + "\n" + event.getMessageContent();
        if ( Discord.getLatestMsgFromChannel( channelId ).getUserAuthor().get().isBot() )
            Discord.getLatestMsgFromChannel( channelId ).edit( finalMessage );
        else {
            Discord.getLatestMsgFromChannel( channelId ).removeContent();
            JavacordBot.api.getChannelById( channelId ).get().asServerTextChannel().get().sendMessage( finalMessage );
        }
        return output;
    }

    public static String modifyOrReplaceLastMessage( String channelId, String finalMessage, String output  ) {
        if ( Discord.getLatestMsgFromChannel( channelId ).getUserAuthor().get().isBot() ) {
            Discord.getLatestMsgFromChannel( channelId ).edit( finalMessage );
        } else {
            Discord.getLatestMsgFromChannel( channelId ).removeContent();
            JavacordBot.api.getChannelById( channelId ).get().asServerTextChannel().get().sendMessage( finalMessage );
        }
        return output;
    }

    public static void addFuckersForUserMessages( MessageCreateEvent event, UserId user  ) {
        if ( Discord.isSameAuthor( event, user ) )
            event.getMessage().addReaction( Reaction.MIDDLE_FINGER.getValue() );
    }

    public static void addReactionsForEachKeyword( MessageCreateEvent event, UserId user, List<Reaction> reactionList ) {
        if ( Discord.isSameAuthor( event, user )  && !reactionList.isEmpty() )
            reactionList.forEach( reaction -> event.getMessage().addReaction( reaction.getValue() ));
    }
}
