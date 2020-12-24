package disco.bot.Services;

import disco.bot.Discord.Reaction;
import disco.bot.Discord.UserId;
import disco.bot.JavacordBot;
import disco.bot.Utils.Discord;
import disco.bot.Utils.TextFormatter;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.message.reaction.ReactionAddEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DiscordReactionService {

    public static void removeForbiddenReactions( List<Reaction> ankietyAllowedReactions, ReactionAddEvent event, String channelId ) {
        String currentEmoji = event.getEmoji().asUnicodeEmoji().get();
        if ( Discord.compareChannels( channelId, event ) && isHuman( event ) && !ankietyAllowedReactions.stream().map( Reaction::getValue ).collect(Collectors.toList()).contains( currentEmoji ) ) {
            event.removeReaction();
        }
    }

    public static void removeRedundantReactions( ReactionAddEvent event, String channelId ) {
        if ( isHuman( event ) && Discord.compareChannels( channelId, event ) && isNotFirstReaction( event ) ) {
            event.removeReaction();
        }
    }

    public static void makeAnnouncementWhenAllVoted(ReactionAddEvent event, String channelForVoting, String channelForAnnoucement, List<UserId> users, String annoucement, DiscordApi api ) {
        if ( Discord.compareChannels( channelForVoting, event ) && isntLocked( event, api ) && hasEveryoneVoted( event, users ) ) {
            annoucement += TextFormatter.boldWrapper( getContent( event ) ) + ":\n" + listAllReactions( event );
            DiscordMessageService.createMessage(api, channelForAnnoucement, annoucement);
            event.addReactionsToMessage( Reaction.LOCKED.getValue() );
        }
    }

    public static void addDefaultVotingReactions( MessageCreateEvent event, String channelId, List<Reaction> defaultReactions ) {
        if ( Discord.compareChannels( channelId, event ) )
            defaultReactions.forEach( reaction -> event.getMessage().addReaction( reaction.getValue() ) );
    }

    private static boolean hasEveryoneVoted( ReactionAddEvent event, List<UserId> users ) {
        return getReactions( event )
                .stream()
                .flatMap( reaction -> {
                    try {
                        return reaction.getUsers().get().stream();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).map(user -> String.valueOf( user.getId() ) )
                .collect(Collectors.toSet()).stream().filter( r -> users.stream().map( UserId::getId ).collect(Collectors.toList()).contains( r ) ).count() == users.size();
    }

    private static boolean isNotFirstReaction( ReactionAddEvent event ) {
        return getReactions( event ).stream().filter(reaction -> {
                    try {
                        return reaction.getUsers().get().stream().filter(user -> user.getId() == event.getUserId()).count() >= 1;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).count() > 1;
    }

    private static boolean isHuman( ReactionAddEvent event ) {
        return !String.valueOf( event.getUserId() ).equals( JavacordBot.BOT_ID );
    }

    private static boolean isntLocked( ReactionAddEvent event, DiscordApi api ) {
        return getReactions( event ).stream().noneMatch( reaction -> reaction.getEmoji().equalsEmoji( Reaction.LOCKED.getValue() ) );
    }

    private static List<org.javacord.api.entity.message.Reaction> getReactions( ReactionAddEvent event ) {
        try {
            return event.getChannel()
                    .getMessageById(event.getMessageId())
                    .get()
                    .getReactions();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private static String listAllReactions( ReactionAddEvent event ) {
        List<String> r = new ArrayList<>();
        getReactions( event ).forEach(reaction -> {
            try {
                r.add( StringUtils.removeEnd( reaction.getEmoji().asUnicodeEmoji().get() + String.format(" (%d) ", reaction.getCount() -1 ) +  reaction.getUsers().get().stream().filter(user -> !user.getName().equals("Middle Finger")).map(user -> user.getName()).collect(Collectors.joining(", ")), ", "));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        Collections.sort(r, (s1, s2) -> {
            int i1 = Integer.parseInt( StringUtils.substringBetween(s1, "(", ")") );
            int i2 = Integer.parseInt( StringUtils.substringBetween(s2, "(", ")") );
            return Integer.compare(i2, i1);
        });
        return String.join("\n", r);
    }

    private static String getContent( ReactionAddEvent event ) {
        try {
            return event.getChannel().getMessageById( event.getMessageId() ).get().getContent();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
