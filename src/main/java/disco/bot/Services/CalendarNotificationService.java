package disco.bot.Services;

import disco.bot.Discord.ChannelId;
import disco.bot.Model.Race;
import disco.bot.Model.Season;
import disco.bot.Model.StringAndDate;
import disco.bot.Utils.Discord;
import disco.bot.Utils.TextFormatter;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.MessageData;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CalendarNotificationService {
    private List<Season> seasonList = new ArrayList<>();
    private List<Race> raceList = new ArrayList<>();

    public List<Season> parseSeasonList(MessageCreateEvent event, GatewayDiscordClient client){
        List<Season> tempList = new ArrayList<>();
        //public static void getSeasonList(MessageCreateEvent event, GatewayDiscordClient client){
        List<Race> parsedRaces = new ArrayList<>();
        String calendarContent = "";
        String[] calendarLines = new String[0];
        int i=0,j=0, line=0, maxlines=0;

        List<MessageData> calendarmsg = client.getRestClient()
                .getChannelById(Snowflake.of(ChannelId.KALENDARZ.getId()))
                .getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();

        if(!calendarmsg.isEmpty()) {
            calendarContent = calendarmsg.get(calendarmsg.size() - 1).content();
            calendarLines = calendarContent.split("\n");
        }
        maxlines = countLines(calendarContent);
        //System.out.println(calendarContent);
        for(line=0;line<maxlines;line++) {
            //System.out.println(calendarLines[line]);
            if (calendarLines[line].contains("**")) {
                //System.out.println(calendarLines[line].replace("*", ""));
                i++;
                j = 0;
                parsedRaces.clear();
                Season tempSeason = new Season(i, calendarLines[line].replace("*", ""));
                tempList.add(tempSeason);
            }
            if((calendarLines[line].matches("^(0[1-9]|[12][0-9]|3[01])[.](0[1-9]|1[012])[.](19|20)[0-9]{2}[\\s\\S]*"))) {
                j++;
                Race singleRace = new Race(j,
                        calendarLines[line].substring(0, 10),
                        calendarLines[line].substring(13, 14),
                        calendarLines[line].substring(13)
                );
                //System.out.println(singleRace.getId() + " " + singleRace.getDate() + " " + singleRace.getType() + " " + singleRace.getName());
                parsedRaces.add(singleRace);
                //System.out.println(calendarLines[line]);)
                tempList.get(i - 1).setRaces(new ArrayList<>(parsedRaces));
            }

        }


        //System.out.println(seasonList.get(0).getName() + " " + seasonList.get(1).getName());
        //System.out.println(seasonList.get(0).getRaces().get(seasonList.get(0).getRaces().size()-1).getName() + " " + seasonList.get(0).getRaces().get(seasonList.get(1).getRaces().size()-1).getName());

        return tempList;
    }

    public List<Race> parseRacesList (List<Season> seasonList){
        List<Race> races = new ArrayList<>();
        int index=0;
        if(!seasonList.isEmpty()) {
            for (int i = 0; i < seasonList.size(); i++) {
                for(int j=0;j<seasonList.get(i).getRaces().size();j++){
                    races.add(seasonList.get(i).getRaces().get(j));
                    races.get(index).setName(seasonList.get(i).getName() + " - " + races.get(index).getName());
                    index++;
                }
            }
        }
        return races;
    }


    public String printSeasonList(List<Season> seasonList){
        String result = "";
        if(seasonList.isEmpty()) result = "Kalendarz pusty!";
        else {
            result = "```";
            for (int i = 0; i < seasonList.size(); i++) {
                result += seasonList.get(i).getId() + ". " + seasonList.get(i).getName() + "\n";
            }
            result = result.substring(0,result.length()-1) + "```";
        }
        return result;
    }

    public String printRacesList(List<Race> racesList){
        String result = "";
        if(racesList.isEmpty()) result = "Kalendarz pusty!";
        else {
            result = "```";
            for (int i = 0; i < racesList.size(); i++) {
                result += racesList.get(i).getDate() + " - " + racesList.get(i).getName() + "\n";
            }
            result = result.substring(0,result.length()-1) + "```";
            //System.out.println(result);
        }
        return result;
    }

    public String printSortedRacesList(List<Race> racesList, int count){
        String result = "";
        if(racesList.size() < count) count = racesList.size();
        if(racesList.isEmpty()) result = "Kalendarz pusty!";
        else {
            result = "```Nadchodzące wyścigi:\n";
            for (int i = 0; i < count; i++) {
                result += racesList.get(i).getDate() + " - " + racesList.get(i).getName() + "\n";
            }
            result = result.substring(0,result.length()-1) + "```";
            //System.out.println(result);
        }
        return result;
    }

    private static int countLines(String str){
        String[] lines = str.split("\n");
        return  lines.length;
    }

    public static String processCalendarMessage( org.javacord.api.event.message.MessageCreateEvent event ) {
        List<String> singleLines = Arrays.asList( Discord.getMsg(event).split("\n") );
        return singleLines.stream().map( s -> StringUtils.isNotBlank( s ) && new StringAndDate( s ).getDate() == null && !s.contains("**") ? TextFormatter.boldWrapper( s ) : s ).collect(Collectors.joining("\n"));
    }
}
