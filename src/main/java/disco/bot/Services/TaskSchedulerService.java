package disco.bot.Services;

import disco.bot.Discord.ChannelId;
import disco.bot.Discord.Reaction;
import disco.bot.Model.DateAndTime;
import disco.bot.Model.StringAndDate;
import disco.bot.Persistance.FileStorage;
import disco.bot.Services.Web.FIETParser;
import disco.bot.Utils.Discord;
import disco.bot.Utils.TextFormatter;
import disco.bot.Utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.entity.message.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
                DiscordMessageService.createMessage( ChannelId.ACL_WEK.getId(), freshTimeAndMessage.get(1) );
                savedTime = freshTime;
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Scheduled(fixedRate = 59999)
    public void PLPReminder() {
        DateAndTime currentTime = new DateAndTime();
        List<Integer> hours = Arrays.asList(12, 15, 18);
        List<String> dayAndMonth = Arrays.asList("03.12", "17.12", "07.01", "21.01", "04.2", "18.2", "04.03");
        if ( currentTime.isCurrentHourInList( hours ) && currentTime.isCurrentDayAndMonthInList( dayAndMonth ) )
            DiscordMessageService.createMessage( ChannelId.POGADANKI.getId(),"@everyone Przypomnienie o aktualizacji PLP przed dzisiejsza runda. Link do pobrania: https://tinyurl.com/y6zjrdv6");

    }


    @Scheduled(fixedRate = 59999)
    public void toDoReminder() {
        DateAndTime currentTime = new DateAndTime();
        if ( !currentTime.isCurrentHourInList( Arrays.asList(9, 12, 15, 18, 21) ) )
            return;

        String latestMessage = Discord.getLatestMsgAsString( ChannelId.TODO );

        if ( StringUtils.isEmpty( latestMessage ) )
            return;

        String getMessage = ToDoNotificationService.notifications( latestMessage, currentTime.getHour() );
        if ( StringUtils.isNotBlank( getMessage ) )
            DiscordMessageService.createMessage( ChannelId.POGADANKI.getId(),"Przypomnienie o rzeczach do zrobienia z kanalu #\uD83D\uDCDDto-do : \n" + getMessage);
    }

    @Scheduled(fixedRate = 43200000)
    public void distinguishNearestDatesAndRemovePast() {
        Message latestMessage = Discord.getLatestMsgFromChannel( ChannelId.KALENDARZ );
        List<StringAndDate> singleLine = Arrays
                .stream( latestMessage.getContent().split("\n") )
                .map( StringAndDate::new )
                .filter( StringAndDate::isBeforeDate )
                .collect( Collectors.toList() );

        List<Integer> copiedList = singleLine
                .stream()
                .filter( stringAndDate -> stringAndDate.getDate() != null )
                .sorted( Comparator.comparingLong(s -> s.getDate().getTime() ) )
                .collect( Collectors.toList() ).subList(0, 3)
                .stream().map( singleLine::indexOf )
                .collect( Collectors.toList() );

        for (Integer i : copiedList) {
            singleLine.get( i ).setString( TextFormatter.underlineWrapper( singleLine.get( i ).getString() ) + Reaction.EXCLAMATION.getValue() );
        }

        DiscordMessageService.modifyOrReplaceLastMessage( ChannelId.KALENDARZ, singleLine.stream().map( StringAndDate::getString ).collect(Collectors.joining("\n")), null );
    }

    @Scheduled(fixedRate = 59999)
    public void notifyUsersAboutIncomingRace() {
        DateAndTime currentTime = new DateAndTime();
        CalendarService calendarService = new CalendarService();
        List<String> users = FileStorage.getAllUsers();
        StringBuilder finalMessage = new StringBuilder();

        if ( !currentTime.isCurrentHourInList( Collections.singletonList(16) ) )
            return;

        for (Map.Entry<String, String> entry : calendarService.events.entrySet()) {
            Date eventDate = Utils.extractDateFromString( entry.getValue() );
            String singleRace = entry.getValue();
            String eventName = entry.getKey();
            String indexOfCurrentEvent = String.valueOf( calendarService.listOfEvents.indexOf( eventName ) );
            long dayDiff = Utils.getDayDifference( eventDate );

            if ( Arrays.asList(7, 5, 2, 1).contains ( dayDiff ) )
                continue;

            List<String> listOfSubscribers = users.stream()
                    .filter( s -> Arrays.asList( StringUtils.substringAfter(s, ":").split(";") ).contains( indexOfCurrentEvent ) )
                    .map( s -> calendarService.userIdResolver( StringUtils.substringBefore(s, ":") ) )
                    .collect( Collectors.toList() );

            if ( listOfSubscribers.isEmpty() )
                continue;

            finalMessage.append( String.format( "Wyscig %s odbędzie się za %d. %s", singleRace, dayDiff, String.join(" ", listOfSubscribers) ) );
        }

        if ( StringUtils.isNotBlank(finalMessage.toString()) )
            DiscordMessageService.createMessage( ChannelId.POGADANKI.getId(), finalMessage.toString());
    }

}
