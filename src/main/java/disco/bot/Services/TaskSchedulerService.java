package disco.bot.Services;

import disco.bot.Bot;
import disco.bot.Discord.ChannelId;
import disco.bot.JavacordBot;
import disco.bot.Services.Web.FIETParser;
import disco.bot.Utils.Discord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

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

    @Scheduled(fixedRate = 60000)
    public void PLPReminder() {
        String[] currentTime = new SimpleDateFormat("HH:mm:ss:dd:MM:yyyy").format(Calendar.getInstance().getTime()).split(":");
        if (  Stream.of(12, 15, 18).anyMatch(i -> isTimeMatching( currentTime, i )) &&
                Stream.of("03;12", "17;12", "07;01", "21;01", "04;02", "18;02", "04;03")
                        .map(s -> s.split(";"))
                        .anyMatch( dayMonth -> isDateMatching(currentTime, dayMonth[0], dayMonth[1] ) ) )  {
            DiscordMessageService.createMessage( JavacordBot.api, Bot.ChannelsId.POGADANKI.getId(),"@everyone Przypomnienie o aktualizacji PLP przed dzisiejsza runda. Link do pobrania: https://tinyurl.com/y6zjrdv6");
        }
    }


    @Scheduled(fixedRate = 60000)
    public void toDoReminder() {
        String[] currentTime = new SimpleDateFormat("HH:mm:ss:dd:MM:yyyy").format(Calendar.getInstance().getTime()).split(":");
        if (Stream.of(9, 12, 15, 18, 21).anyMatch( i -> isTimeMatching( currentTime, i ))) {
            String latestMessage = Discord.getLatestMsgAsString( JavacordBot.api, ChannelId.TODO );

            if ( StringUtils.isEmpty( latestMessage ) )
                return;

            try {
                String getMessage = ToDoNotificationService.notifications( latestMessage, currentTime[0]);
                if ( StringUtils.isNotBlank( getMessage ) )
                    DiscordMessageService.createMessage( JavacordBot.api, Bot.ChannelsId.POGADANKI.getId(),"Przypomnienie o rzeczach do zrobienia z kanalu #\uD83D\uDCDDto-do : \n" + getMessage);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    //todo create dedicated time class
    private static boolean isDateMatching(String[] currentTime, String s, String s1) {
        return currentTime[3].equals(s) && currentTime[4].equals(s1);
    }

    private static boolean isTimeMatching(String[] currentTime, int hour ) {
        return ( currentTime[0].equals( String.valueOf( hour ) ) && ( currentTime[1].equals("00") || currentTime[1].equals("01") || currentTime[1].equals("02")  ) );
    }
}
