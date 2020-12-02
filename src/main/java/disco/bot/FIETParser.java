package disco.bot;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

class FIETParser {

    private static Map<String, String> cookies;
    static {
        cookies = new HashMap<>();
        cookies.put("_ga", "GA1.2.635290763.1582986418");
        cookies.put("testcookie", "ano");
        cookies.put("_gid", "GA1.2.328053332.1587993330");
        cookies.put("bkod", "Z032AJG3AX");
        cookies.put("bid", "34700159");
    }


    static List<String> parseFIETServer() throws IOException {
        String link = "http://managerdc5.rackservice.org:50925/lapstat";
        Document doc = Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36")
                .cookies(cookies)
                .execute()
                .bufferUp()
                .parse();

        Elements elements = doc.select("tbody > tr");

        List<String> returnList = new ArrayList<>();

        String message = "**Nowy bestlap**:\n";

        if (elements.size() >= 1) {
            Element firstElement = elements.get(0);
            returnList.add(StringUtils.substringBetween(firstElement.select("td:nth-child(4)").toString(), "bestLap\">", "</td>"));
            message += "Tor: " + replaceTrackOrCar(doc.select("select#trackname > option[selected]").first().absUrl("value").replaceAll("http://managerdc5.rackservice.org:50925/", ""), Track.class) + "\n";
            message += "Czas: " + StringUtils.substringBetween(firstElement.select("td:nth-child(4)").toString(), "bestLap\">", "</td>") + "\n";
            message += "Kierowca: " + StringUtils.substringBetween(firstElement.select("td:nth-child(2)").toString(), "bestLap\">", "</td>") + "\n";
            message += "Samochód: " + replaceTrackOrCar(StringUtils.substringBetween(firstElement.select("td:nth-child(3)").toString(), "bestLap\">", "</td>"), Car.class) + "\n";
            returnList.add(message);
        } else {
            return Collections.emptyList();
        }

        return returnList;
    }

    private static String replaceTrackOrCar(String str, Class<? extends Enum> en ) {
       Optional<? extends Enum> tc = Arrays.stream( en.getEnumConstants() )
                .filter( e -> str.contains( e.name()) )
                .findFirst();

       return tc.isPresent() && tc.get() instanceof Label ? ((Label) tc.get()).getLabel() : str;
    }

    enum Track implements Label {
        villeneuve("R1. Circuit Gilles Villeneuve (Montreal)"),
        america("R2. Road America"),
        daytona("R3. Daytona"),
        watkins("R4. Watkins Glen"),
        lemans("Circuit de la Sarthe (Le Mans)"),
        nurburgring("Nurburgring"),
        spa("Spa-Francorchamps");

        public final String label;
        Track(String label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }


    enum Car implements Label {
        audi("Audi R8 LMS 2016"),
        bmw("BMW Z4 GT3"),
        ferrari("Ferrari 488 GT3"),
        lambo("Lamborghini Huracan GT3"),
        mclaren("McLaren 650 GT3"),
        mercedes("Mercedes AMG GT3"),
        nissan("Nissan GT-R GT3"),
        porsche("Porsche 911 GT3 R 2016");

        public final String label;
        Car(String label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return label;
        }
    }
}