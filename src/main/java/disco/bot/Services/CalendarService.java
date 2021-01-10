package disco.bot.Services;

import disco.bot.Discord.ChannelId;
import disco.bot.Model.StringAndDate;
import disco.bot.Persistance.FileStorage;
import disco.bot.Utils.Discord;
import disco.bot.Utils.TextFormatter;
import org.apache.commons.lang3.StringUtils;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.*;
import java.util.stream.Collectors;

public class CalendarService {

    Map<String, String> events = new HashMap<>();
    List<String> listOfEvents = new ArrayList<>();

    public CalendarService() {
        List<String> calendar = Arrays.asList( Discord.getLatestMsgAsString( ChannelId.KALENDARZ.getId() ).split("\n") );
        calendar.forEach(c -> {
            if ( c.contains("**") ) {
                events.put(c, calendar.get(calendar.indexOf(c) + 1));
                listOfEvents.add( c );
            }
        });
    }

    public static String addNewCalendarEventToExistingList( MessageCreateEvent event ) {
        List<String> singleLines = Arrays.asList( Discord.getMsgWithoutCommand( event ).split("\n") );
        return Discord.getLatestMsgFromChannel( ChannelId.KALENDARZ.getId() ).getContent() + "\n\n" + singleLines.stream().map( s -> StringUtils.isNotBlank( s ) && new StringAndDate( s ).getDate() == null && !s.contains("**") ? TextFormatter.boldWrapper( s ) : s ).collect(Collectors.joining("\n"));
    }

    public String listEventsIds() {
        return listOfEvents.stream().map( s -> (listOfEvents.indexOf( s ) ) + StringUtils.SPACE + s).collect(Collectors.joining("\n"));
    }

    public String getCurrentStatus( MessageCreateEvent event ) {
        List<String> users = FileStorage.getAllUsers();
        String currentUserId = Discord.getAuthorId( event );
        String currentEvents = getUserCurrentEvents( currentUserId );

        return StringUtils.isNotBlank( currentEvents ) ? currentEvents : "Nie jesteś zapisany do żadnego eventu.";
    }

    private String getUserCurrentEvents( String userId ) {
        List<String> userData = getRecordsForUserId( userId );
        if ( !userData.isEmpty() )
            return eventResolver( userData.get(0) );
        return null;
    }

    private List<String> getRecordsForUserId(String userId) {
        List<String> users = FileStorage.getAllUsers();
        return users.stream().filter( s -> s.contains( userId ) ).collect(Collectors.toList());
    }

    private String eventResolver( String events ) {
        return Arrays.stream( standarizeEventFormat (StringUtils.substringAfter( events, ":") ).split(";"))
                .filter( StringUtils::isNotBlank )
                .map( Integer::valueOf )
                .filter( i -> i < listOfEvents.size() -1 )
                .map( i -> listOfEvents.get( i ) )
                .collect(Collectors.joining("\n"));
    }

    private String standarizeEventFormat( String s ) {
        return s.replaceAll("(\\D)", " ")
                .replaceAll("( +)", ";");
    }

    public String updateEventsRecord( String userId, String input ) {
        input = standarizeEventFormat( input );
        if ( getRecordsForUserId( userId ).isEmpty() )
            FileStorage.addNewUserRecord( userId, input );
        else
            FileStorage.editExistingRecord( userId, input );
        return "Dodano nowe wydarzenia do sledzenia.";
    }
}
