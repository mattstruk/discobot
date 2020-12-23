package disco.bot.Services;

import java.util.List;
import java.util.Random;


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
}
