package disco.bot.Services;

import disco.bot.Discord.ChannelId;
import disco.bot.Discord.Reaction;
import disco.bot.Discord.UserId;
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
                .setToken( Files.readAllLines( Paths.get(System.getProperty("user.dir") + "/file.txt") ).get(0) )
                .login().join();
    }

    public static void createMessage( MessageCreateEvent event, String message ) {
        if ( StringUtils.isNotBlank( message ) )
            event.getChannel().sendMessage( message );
    }

    public static void createMessage( DiscordApi api, String channel, String message ) {
        if ( StringUtils.isNotBlank( message ) )
            api.getChannelById( channel ).get().asServerTextChannel().get().sendMessage( message );
    }

    public static void deleteeMessage( MessageCreateEvent event ) {
        try {
            event.getChannel().getMessageById( event.getMessageId() ).get().delete();
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
    }

    public static String modifyOrReplaceLastMessage( DiscordApi api, ChannelId channelId, MessageCreateEvent event, String output ) {
        String calendarmsg = Discord.getLatestMsgAsString( api, channelId );

        if ( StringUtils.isEmpty( calendarmsg) )
            return null;

        String userInput = Discord.getMsgWithoutCommand( event );
        String editedMessage = calendarmsg + "\n" + userInput;

        if ( Discord.getLatestMsgFromChannel( api, channelId ).getUserAuthor().get().isBot() )
            Discord.getLatestMsgFromChannel( api, channelId ).edit( editedMessage );
        else {
            Discord.getLatestMsgFromChannel( api, channelId ).removeContent();
            api.getChannelById( channelId.getId() ).get().asServerTextChannel().get().sendMessage( editedMessage );
        }
        return output;
    }

    public static void deleteMessageAndSetRound( MessageCreateEvent event,  int[] currentWEKRound) {
        currentWEKRound[0] = Integer.parseInt( Discord.getMsg( event ).replaceAll("[^0-9]", "").trim() );
        deleteeMessage( event );
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
