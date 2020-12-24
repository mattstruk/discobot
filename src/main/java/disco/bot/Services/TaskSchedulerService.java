package disco.bot.Services;

import disco.bot.Discord.ChannelId;
import disco.bot.JavacordBot;
import disco.bot.Model.DateAndTime;
import disco.bot.Services.Web.FIETParser;
import disco.bot.Utils.Discord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

@Controller
@EnableScheduling
public class TaskSchedulerService {

    private static List<String> timeAndMessage;

    static { try { timeAndMessage = FIETParser.parseFIETServer(); } catch (IOException e) { e.printStackTrace(); } }

    private static String savedTime = timeAndMessage != null && timeAndMessage.isEmpty() ? null : timeAndMessage.get(0);

    @Scheduled(fixedRate = 120000)
    public void bestLapChecker() {
        try {
            List<String> freshTimeAndMessage = FIETParser.parseFIETServer();
            String freshTime = freshTimeAndMessage.isEmpty() ? null : freshTimeAndMessage.get(0);
            if (freshTime != null && savedTime != null && !freshTime.equals(savedTime)) {
                DiscordMessageService.createMessage( JavacordBot.api, ChannelId.ACL_WEK.getId(), freshTimeAndMessage.get(1) );
                savedTime = freshTime;
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Scheduled(fixedRate = 59000)
    public void PLPReminder() {
        DateAndTime currentTime = new DateAndTime();
        List<Integer> hours = Arrays.asList(12, 15, 18);
        List<String> dayAndMonth = Arrays.asList("03.12", "17.12", "07.01", "21.01", "04.2", "18.2", "04.03");
        if (  currentTime.isCurrentHourInList( hours ) && currentTime.isCurrentDayAndMonthInList( dayAndMonth ) )
            DiscordMessageService.createMessage( JavacordBot.api, ChannelId.POGADANKI.getId(),"@everyone Przypomnienie o aktualizacji PLP przed dzisiejsza runda. Link do pobrania: https://tinyurl.com/y6zjrdv6");

    }


    @Scheduled(fixedRate = 59000)
    public void toDoReminder() {
        DateAndTime currentTime = new DateAndTime();
        if ( currentTime.isCurrentHourInList( Arrays.asList(9, 12, 15, 18, 21) ) )
            return;

        String latestMessage = Discord.getLatestMsgAsString( JavacordBot.api, ChannelId.TODO );

        if ( StringUtils.isEmpty( latestMessage ) )
            return;

        try {
            String getMessage = ToDoNotificationService.notifications( latestMessage, currentTime.getHour() );
            if ( StringUtils.isNotBlank( getMessage ) )
                DiscordMessageService.createMessage( JavacordBot.api, ChannelId.POGADANKI.getId(),"Przypomnienie o rzeczach do zrobienia z kanalu #\uD83D\uDCDDto-do : \n" + getMessage);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
