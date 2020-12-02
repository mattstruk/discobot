package disco.bot;

import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MiddleFingerAlphabet {
    private static String firstLine = "";
    private static String secondLine = "";
    private static String thirdLine = "";
    private static String fourthLine = "";
    private static String fifthLine = "";

    private static final Map<String, FiveStringHolder> map = Stream.of(
            new AbstractMap.SimpleEntry<>("a", FiveStringHolder.builder()
                    .first("_ _ _ _  %1$s%1$s    ")
                    .second("   %1$s    %1$s  ")
                    .third("  %1$s%1$s%1$s ")
                    .fourth(" %1$s        %1$s")
                    .fifth("%1$s          %1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("b", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s  ")
                    .second("%1$s        %1$s")
                    .third("%1$s%1$s%1$s  ")
                    .fourth("%1$s        %1$s")
                    .fifth("%1$s%1$s%1$s ")
                    .build()),
            new AbstractMap.SimpleEntry<>("c", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s ")
                    .second("%1$s             ")
                    .third("%1$s             ")
                    .fourth("%1$s             ")
                    .fifth("%1$s%1$s%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("d", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s    ")
                    .second("%1$s          %1$s")
                    .third("%1$s          %1$s")
                    .fourth("%1$s        %1$s  ")
                    .fifth("%1$s%1$s%1$s   ")
                    .build()),
            new AbstractMap.SimpleEntry<>("e", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s ")
                    .second("%1$s             ")
                    .third("%1$s%1$s%1$s ")
                    .fourth("%1$s             ")
                    .fifth("%1$s%1$s%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("f", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s")
                    .second( "%1$s            ")
                    .third( "%1$s%1$s%1$s")
                    .fourth("%1$s            ")
                    .fifth( "%1$s            ")
                    .build()),
            new AbstractMap.SimpleEntry<>("g", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s   ")
                    .second("%1$s                ")
                    .third("%1$s   %1$s%1$s")
                    .fourth("%1$s        %1$s  ")
                    .fifth("%1$s%1$s%1$s   ")
                    .build()),
            new AbstractMap.SimpleEntry<>("h", FiveStringHolder.builder()
                    .first("%1$s        %1$s")
                    .second("%1$s        %1$s")
                    .third("%1$s %1$s %1$s")
                    .fourth("%1$s        %1$s")
                    .fifth("%1$s        %1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("i", FiveStringHolder.builder()
                    .first("%1$s")
                    .second("%1$s")
                    .third("%1$s")
                    .fourth("%1$s")
                    .fifth("%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("j", FiveStringHolder.builder()
                    .first("%1$s %1$s %1$s")
                    .second("              %1$s")
                    .third("             %1$s ")
                    .fourth("            %1$s  ")
                    .fifth("%1$s %1$s       ")
                    .build()),
            new AbstractMap.SimpleEntry<>("k", FiveStringHolder.builder()
                    .first("%1$s        %1$s")
                    .second("%1$s    %1$s    ")
                    .third("%1$s%1$s        ")
                    .fourth("%1$s    %1$s    ")
                    .fifth("%1$s        %1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("l", FiveStringHolder.builder()
                    .first("%1$s            ")
                    .second("%1$s            ")
                    .third("%1$s            ")
                    .fourth("%1$s            ")
                    .fifth("%1$s%1$s%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("m", FiveStringHolder.builder()
                    .first("%1$s            %1$s")
                    .second("%1$s%1$s%1$s%1$s")
                    .third("%1$s   %1$s   %1$s")
                    .fourth("%1$s            %1$s")
                    .fifth("%1$s            %1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("n", FiveStringHolder.builder()
                    .first("%1$s            %1$s")
                    .second("%1$s%1$s      %1$s")
                    .third("%1$s   %1$s   %1$s")
                    .fourth("%1$s      %1$s%1$s")
                    .fifth("%1$s            %1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("o", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s")
                    .second("%1$s      %1$s")
                    .third("%1$s      %1$s")
                    .fourth("%1$s      %1$s")
                    .fifth("%1$s%1$s%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("p", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s")
                    .second("%1$s      %1$s")
                    .third("%1$s%1$s%1$s")
                    .fourth("%1$s            ")
                    .fifth("%1$s            ")
                    .build()),
            new AbstractMap.SimpleEntry<>("r", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s    ")
                    .second("%1$s         %1$s ")
                    .third("%1$s%1$s%1$s    ")
                    .fourth("%1$s        %1$s  ")
                    .fifth("%1$s          %1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("s", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s ")
                    .second("%1$s             ")
                    .third("%1$s%1$s%1$s ")
                    .fourth("             %1$s")
                    .fifth("%1$s%1$s%1$s ")
                    .build()),
            new AbstractMap.SimpleEntry<>("t", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s%1$s%1$s")
                    .second("            %1$s            ")
                    .third("            %1$s            ")
                    .fourth("            %1$s            ")
                    .fifth("            %1$s            ")
                    .build()),
            new AbstractMap.SimpleEntry<>("u", FiveStringHolder.builder()
                    .first("%1$s      %1$s")
                    .second("%1$s      %1$s")
                    .third("%1$s      %1$s")
                    .fourth("%1$s      %1$s")
                    .fifth("%1$s%1$s%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>("w", FiveStringHolder.builder()
                    .first("%1$s                   %1$s")
                    .second(" %1$s                 %1$s ")
                    .third("  %1$s    %1$s     %1$s  ")
                    .fourth("   %1$s%1$s%1$s %1$s   ")
                    .fifth("    %1$s           %1$s     ")
                    .build()),
            new AbstractMap.SimpleEntry<>("y", FiveStringHolder.builder()
                    .first("%1$s      %1$s")
                    .second("  %1$s %1$s   ")
                    .third("     %1$s       ")
                    .fourth("     %1$s       ")
                    .fifth("     %1$s        ")
                    .build()),
            new AbstractMap.SimpleEntry<>("z", FiveStringHolder.builder()
                    .first("%1$s%1$s%1$s ")
                    .second("         %1$s    ")
                    .third("      %1$s       ")
                    .fourth("   %1$s          ")
                    .fifth("%1$s%1$s%1$s")
                    .build()),
            new AbstractMap.SimpleEntry<>(" ", FiveStringHolder.builder()
                    .first("    ")
                    .second("    ")
                    .third("    ")
                    .fourth("    ")
                    .fifth("    ")
                    .build())
    ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    static String printFuckerText(String str) {

        resetLines();
        List<String> fiverLiner = new ArrayList<>();

        str = StringUtils.replaceEach(str,
                new String[] {"lucjan", "!fuckertext", "x" , "q", "[^a-zA-Z ]", "lucek", "ą", "ć", "ę", "ł", "ń", "ó", "ś", "ż", "ź"},
                new String[] {"luka"  , ""           , "ks", "ku" , ""          , "luka",  "a", "c" , "e", "l", "n", "o", "s", "z", "z" })
                .toLowerCase().trim();

        for (int i = 0; i < str.length(); i++) {
            String c = String.valueOf(str.charAt(i));
            String space = "  ";
            if (i != 0 && c.equalsIgnoreCase("a")){
                firstLine += String.format(map.get(c).getFirst().replaceAll("[.]", " ") + space, Bot.Reactions.MIDDLE_FINGER.getValue());
            } else {
                firstLine += String.format(map.get(c).getFirst() + space, Bot.Reactions.MIDDLE_FINGER.getValue());
            }
            secondLine += String.format(map.get(c).getSecond() + space, Bot.Reactions.MIDDLE_FINGER.getValue());
            thirdLine += String.format(map.get(c).getThird() + space, Bot.Reactions.MIDDLE_FINGER.getValue());
            fourthLine += String.format(map.get(c).getFourth() + space, Bot.Reactions.MIDDLE_FINGER.getValue());
            fifthLine += String.format(map.get(c).getFifth() + space, Bot.Reactions.MIDDLE_FINGER.getValue());
        }

        fiverLiner.add(firstLine);
        fiverLiner.add(secondLine);
        fiverLiner.add(thirdLine);
        fiverLiner.add(fourthLine);
        fiverLiner.add(fifthLine);

        return String.join("\n", fiverLiner);
    }

    private static void resetLines() {
        firstLine = "";
        secondLine = "";
        thirdLine = "";
        fourthLine = "";
        fifthLine = "";
    }

}
