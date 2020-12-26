package disco.bot.Utils;

import disco.bot.Discord.ChannelId;
import disco.bot.Discord.UserId;
import disco.bot.JavacordBot;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.Message;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.event.message.reaction.SingleReactionEvent;

import java.util.concurrent.ExecutionException;

public class Discord {

    public static boolean equals( MessageCreateEvent event, String str ) {
        return event.getMessageContent().equalsIgnoreCase( str );
    }

    public static boolean contains( MessageCreateEvent event, String str ) {
        return event.getMessageContent().contains( str );
    }

    public static String getMsgAuthor( MessageCreateEvent event ) {
        return event.getMessageAuthor().getDisplayName();
    }

    public static String getMsg( MessageCreateEvent event ) {
        return event.getMessageContent();
    }

    public static String getMsgWithoutCommand( MessageCreateEvent event ) {
        return StringUtils.substringAfter( event.getMessageContent(), StringUtils.SPACE );
    }

    public static Message getMsg( SingleReactionEvent event ) {
        try {
            return event.getChannel().getMessageById( event.getMessageId() ).get();
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean isSameAuthor( ReactionAddEvent event, UserId user ) {
        return String.valueOf( Discord.getMsg( event ).getUserAuthor().get().getId() ).equals( user.getId() );
    }

    public static boolean isSameAuthor( MessageCreateEvent event, UserId user ) {
        return String.valueOf( event.getMessageAuthor().getId() ).equals( user.getId() );
    }

    public static String getLatestMsgAsString( ChannelId channelId ) {
        return getLatestMsgFromChannel( channelId ).getContent();
    }

    public static Message getLatestMsgFromChannel( ChannelId channelId ) {
        try {
            return JavacordBot.api.getChannelById( channelId.getId() ).get().asTextChannel().get().getMessages( 1 ).get().getNewestMessage().get();
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean compareChannels( String channelId, SingleReactionEvent event ) {
        return channelId.equalsIgnoreCase( String.valueOf( event.getChannel().getId() ) );
    }

    public static boolean compareChannels( String channelId, MessageCreateEvent event ) {
        return channelId.equalsIgnoreCase( String.valueOf( event.getChannel().getId() ) );
    }
}
