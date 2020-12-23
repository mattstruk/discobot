package disco.bot.Services;

import disco.bot.Discord.ChannelId;
import disco.bot.Utils.Discord;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.event.message.MessageCreateEvent;

public class ChannelIdResolver {
    public static String getChannel( MessageCreateEvent event, String divider  ) {
        String identifier = StringUtils.substringBefore(Discord.getMsg( event ), divider ).toLowerCase();
        switch ( identifier ) {
            case "pogadanki":
                return ChannelId.POGADANKI.getId();
            case "simgrid":
                return ChannelId.SIM_GRID.getId();
            case "acl-wek":
                return ChannelId.ACL_WEK.getId();
            default:
                return null;
        }
    }
}
