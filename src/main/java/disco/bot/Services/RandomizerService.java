package disco.bot.Services;

import disco.bot.Discord.Reaction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.javacord.api.event.message.MessageCreateEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class RandomizerService {
    private static Random random = new Random();

    public static String rollTheDice( String username) {
        return String.format("%s rolls %d (0-100)", username, random.nextInt(101) );
    }

    public static String getRandomDriver( List<String> maser ) {
        String randomDriver = maser.get(random.nextInt(5));
        return randomDriver.toLowerCase().contains("lucjan")
                ? String.format("Nieszczęśliwym kierowcą Maserati na niby został... %s. Do chuja wafla! Zalecane jest ponowne wykonanie losowania.", randomDriver)
                : String.format("Szczęśliwym kierowcą Maserati został... %s. Gratuluję serdecznie!", randomDriver);
    }

    public static String pickRandomTeams( MessageCreateEvent event ) {
        String userInput = event.getMessage().getContent();
        List<String> listOfDrivers = Arrays.asList(StringUtils.substringBetween(userInput, "!losuj kierowcy:", "zespoly:").trim().split(";"));
        listOfDrivers = listOfDrivers.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
        int size = Integer.parseInt(StringUtils.substringAfter(userInput,"zespoly:").replaceAll("dwa", "2").replaceAll("trzy", "3").replaceAll("[^0-9.,]", ""));

        if (size < 2 || size > 3) {
            DiscordMessageService.createMessage( event, "Podano inny rozmiar od dopuszczalnego (2 lub 3): " + size + "\n" + Reaction.MIDDLE_FINGER);
            return null;
        }
        Collections.shuffle(listOfDrivers);
        int sizeOfSingleTeam = listOfDrivers.size() / size;

        String finalMessage = "";

        for (int i = 0; i < size; i++){
            int finalI = i + 1;
            List<String> finalDriversList = listOfDrivers;
            String kierowcy = finalDriversList.stream().filter(element -> finalDriversList.indexOf(element) < sizeOfSingleTeam).collect(Collectors.joining(";"));
            finalMessage += "Zespół nr "+ finalI + ": " + kierowcy + "\n";
            listOfDrivers.removeAll(Arrays.asList(kierowcy.split(";")));
        }

        return finalMessage;
    }

    public static String pingMsg( MessageCreateEvent event ) {
        List<String> ping = Arrays.asList("Lucjana", "Luki", "JarzomBa", "DubSona", "mknbla", "Maniaka");
        Duration between = Duration.between( event.getMessage().getCreationTimestamp(), Instant.now());
        long duration = Math.abs( between.toMillis() );
        if ( duration < 1500 )
            return String.format( "Pong! Opóźnienie wynosi %d ms. To na pewno mniej niż opóźnienie %s %s", duration, ping.get(random.nextInt(ping.size())), Reaction.MIDDLE_FINGER.getValue() );
        else
            return String.format( " Pong! Opóźnienie wynosi %d ms. Totalna kompromitacja. Sugeruję nastawić zegarek lub dorzucić węgla do RaspberryPi %s", duration, Reaction.MIDDLE_FINGER.getValue() );
    }
}
