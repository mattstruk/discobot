package disco.bot;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.MessageData;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;

public class Bot {


    private static GatewayDiscordClient client;

    static {
        try {
            client = DiscordClientBuilder
                        .create(Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/file.txt")).get(0))
                        .build()
                        .login()
                        .block();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows(IOException.class)
    public static void main(String[] args) throws InterruptedException  {

        Random random = new Random();
        final boolean[] luka = {false};

        client.getEventDispatcher()
                .on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(event -> {
                    String msg = event.getMessage().getContent().toLowerCase();

                    if ( msg.equals("!dubson") ) createMessage( event, MiddleFingerAlphabet.printFuckerText( "dubson" ) );

                    if ( msg.equals("!luka") )createMessage( event, MiddleFingerAlphabet.printFuckerText( "luka" ) );

                    if ( !msg.equalsIgnoreCase("!maser") &&
                            authorId(event).equals(UserId.LUKA.id) &&
                            Stream.of("maser", "maserati", "itaresam", "masser").anyMatch( msg::contains ) ) {
                        reactAtMaserByLuka( event );
                    }

                    if ( msg.equals("!maser")) getRandomDriver( event, random );

                    if ( msg.contains("!losuj kierowcy:") && msg.contains("zespoly:") ) losuj(event);

                    if ( msg.equals("!lukafuck") ) switchLukaFuck( event, luka );

                    if ( msg.contains("!fuckertext") ) convertTextToFuckers( event );

                    if ( luka[0] && authorId(event).equals(Bot.UserId.LUKA.id) ) reactUnicode( event, Reactions.MIDDLE_FINGER );


                    if ( event.getMessage().getChannelId().equals(Snowflake.of(ChannelsId.ANKIETY.getId())) ) initPool( event );

                    if (event.getMessage().getContent().toLowerCase().contains("!ping")) pingMsg( event, random);

                    try {

                        if (msg.equals("!preqwek")) createMessage(event, codeBlockWrapper(ACLParser.getACLWEKPreq(1)));

                        if (msg.contains("!preqwekr0"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLWEKPreq(0)));

                        if (msg.equals("!preqgt4")) createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(5)));

                        if (msg.contains("!preqgt4r0"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(0)));

                        if (msg.contains("!preqgt4r1"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(1)));

                        if (msg.contains("!preqgt4r2"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(2)));

                        if (msg.contains("!preqgt4r3"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(3)));

                        if (msg.contains("!preqgt4r4"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(4)));

                        if (msg.contains("!preqgt4r5"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(5)));

                        if (msg.contains("!preqgt4r6"))
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4Preq(6)));

                        if (msg.contains("!generalkagt4")) {
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4DriverStandings()));
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4TeamStandings()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    client.getRestClient()
//                            .getChannelById(Snowflake.of( toDoFIETId ))
//                            .getMessagesBefore(Snowflake.of( Instant.now() ))
//                            .toStream().forEach(msg -> {
//                                msg.
//                    });


                });

        List<String> timeAndMessage = FIETParser.parseFIETServer();
        String savedTime = timeAndMessage.isEmpty() ? null : timeAndMessage.get(0);

        while (true) {
            int seconds = 120;
            Thread.sleep(1000 * seconds);
            List<String> freshTimeAndMessage = FIETParser.parseFIETServer();
            String freshTime =  freshTimeAndMessage.isEmpty() ? null : freshTimeAndMessage.get(0);
            if (freshTime != null && savedTime != null && !freshTime.equals(savedTime)) {
                client.getRestClient().getChannelById(Snowflake.of(ChannelsId.ACL_WEK.getId())).createMessage(freshTimeAndMessage.get(1)).subscribe();
                savedTime = freshTime;
            }

            String[] currentTime = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()).split(":");
            if ( ( currentTime[0].equals("18") && ( currentTime[1].equals("00") || currentTime[1].equals("01") || currentTime[1].equals("02")  ) ) ) {

                List<MessageData> listMono = client.getRestClient()
                        .getChannelById(Snowflake.of( ChannelsId.TODO.getId() ))
                        .getMessagesBefore( Snowflake.of( Instant.now() ) ).collectList().block();

                if ( listMono != null && listMono.size() == 1 ) {
                    client.getRestClient()
                            .getChannelById(Snowflake.of(Bot.ChannelsId.POGADANKI.getId()))
                            .createMessage( "Przypomnienie o rzeczach do zrobienia z kanalu #\uD83D\uDCDDto-do : \n" + listMono.get(0).content() ).subscribe();
                }
                Thread.sleep(1000 * seconds);
            }

        }

    }

    private static void pingMsg(MessageCreateEvent event, Random random) {
        List<String> ping = Arrays.asList("Lucjana", "Luki", "JarzomBa", "DubSona", "mknbla", "Maniaka");
        Duration between = Duration.between(event.getMessage().getTimestamp(), Instant.now());
        if(abs(between.toMillis()) < 1500){
            event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Pong! Opóźnienie wynosi " + abs(between.toMillis()) + "ms. To na pewno mniej niż opóźnienie " + ping.get(random.nextInt(ping.size())) + "! " + disco.bot.Bot.Reactions.MIDDLE_FINGER.getValue())).subscribe();
        }
        else event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Pong! Opóźnienie wynosi " + abs(between.toMillis()) + "ms. Totalna kompromitacja. Sugeruję nastawić zegarek lub dorzucić węgla do RaspberryPi" + disco.bot.Bot.Reactions.MIDDLE_FINGER.getValue())).subscribe();
    }

    private static void initPool(MessageCreateEvent event) {
        reactUnicode( event, Reactions.THUMBS_UP );
        reactUnicode( event, Reactions.THUMBS_DOWN );
        reactUnicode( event, Reactions.WHATEVER );
        reactUnicode( event, Reactions.FACEPALM );
    }

    private static void convertTextToFuckers( MessageCreateEvent event ) {
        String fuckerText = MiddleFingerAlphabet.printFuckerText(event.getMessage().getContent());
        if (fuckerText.length() >= 2000) {
            createMessage( event, "Wyszło ponad 2000 znaków :( " + Reactions.MIDDLE_FINGER.getValue() );
        } else {
            createMessage( event, fuckerText );
        }
    }

    private static void switchLukaFuck(MessageCreateEvent event, boolean[] luka) {
        if ( !authorId(event).equals(Bot.UserId.LUKA.id) ) {
            luka[0] = !luka[0];
            String readParam = luka[0] ? "włączona" : "wyłączona";
            createMessage( event, "Opcja !lukafuck została " + readParam + ".");
        } else {
            createMessage( event, "Luka, wszyscy inni mogą tym sterować, prócz Ciebie. Bądź miły dla cumpli!" );
        }
    }

    private static void reactAtMaserByLuka( MessageCreateEvent event ) {
        reactUnicode( event, Reactions.TRACTOR );
        reactUnicode( event, Reactions.MIDDLE_FINGER_TONE_2 );
        reactUnicode( event, Reactions.MIDDLE_FINGER );
        reactUnicode( event, Reactions.MIDDLE_FINGER_TONE_5 );
        reactUnicode( event, Reactions.MIDDLE_FINGER_TONE_1 );
        reactUnicode( event, Reactions.MIDDLE_FINGER_TONE_4 );
        reactUnicode( event, Reactions.MIDDLE_FINGER_TONE_3 );
        reactUnicode( event, Reactions.POOP );
        reactUnicode( event, Reactions.TOILET );
    }

    private static void getRandomDriver( MessageCreateEvent event, Random random ) {
        List<String> maser = Arrays.asList("Lucjan Próchnica", "Adam Dubiel", "Patryk Pyrchla", "Paweł Jarzębowski", "Mateusz Książek");
        String randomDriver = maser.get(random.nextInt(5));
        if (randomDriver.toLowerCase().contains("lucjan")) {
            createMessage( event, "Nieszczęśliwym kierowcą Maserati na niby został... " + randomDriver + ". Do chuja wafla! Zalecane jest ponowne wykonanie losowania." );
        } else {
            createMessage( event, "Szczęśliwym kierowcą Maserati został... " + randomDriver + ". Gratuluję serdecznie! " );
        }
    }

    private static String codeBlockWrapper( String str ) {
        String wrapper = "```";
        return wrapper + str + wrapper;
    }

    private static void createMessage(MessageCreateEvent event, String message) {
        if ( StringUtils.isNotBlank( message ) )
            event.getMessage().getChannel().flatMap(channel -> channel.createMessage( message )).subscribe();
    }

    private static void reactUnicode(MessageCreateEvent event, Bot.Reactions reaction) {
        event.getMessage().addReaction( ReactionEmoji.unicode( reaction.getValue() ) ).subscribe();
    }

    private static String authorId(MessageCreateEvent event) {
        return event.getMessage().getAuthor().get().getId().asString();
    }

    private static void losuj(MessageCreateEvent event) {
        String userInput = event.getMessage().getContent();
        List<String> listOfDrivers = Arrays.asList(StringUtils.substringBetween(userInput, "!losuj kierowcy:", "zespoly:").trim().split(";"));

        listOfDrivers = listOfDrivers.stream().map(WordUtils::capitalizeFully).collect(Collectors.toList());
        int size = Integer.parseInt(StringUtils.substringAfter(userInput,"zespoly:").replaceAll("dwa", "2").replaceAll("trzy", "3").replaceAll("[^0-9.,]", ""));
        if (size < 2 || size > 3) {
            createMessage( event, "Podano inny rozmiar od dopuszczalnego (2 lub 3): " + size + "\n" + Reactions.MIDDLE_FINGER);
            return;
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

        createMessage( event, finalMessage );
    }

    enum ChannelsId {
        ANKIETY     ("767774988354453515"),
        TODO        ("767780095846776833"),
        LINKS       ("735196193964949606"),
        POGADANKI   ("700694946503720992"),
        ACL_WEK     ("778000167516373012");

        private final String id;

        ChannelsId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    enum UserId {
        LUKA        ("474322029856686080"),
        JARZOMB     ("408729434229833729"),
        DUBSON      ("633416304954441758"),
        LUCJAN      ("320695302841434112"),
        MANIAK      ("0"),
        MATEUSZ     ("0");

        private final String id;

        UserId(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }
    }

    enum Reactions {
        MIDDLE_FINGER        ("\uD83D\uDD95"),
        MIDDLE_FINGER_TONE_1 ("\uD83D\uDD95\uD83C\uDFFB"),
        MIDDLE_FINGER_TONE_2 ("\uD83D\uDD95\uD83C\uDFFC"),
        MIDDLE_FINGER_TONE_3 ("\uD83D\uDD95\uD83C\uDFFD"),
        MIDDLE_FINGER_TONE_4 ("\uD83D\uDD95\uD83C\uDFFE"),
        MIDDLE_FINGER_TONE_5 ("\uD83D\uDD95\uD83C\uDFFF"),
        POOP                 ("\uD83D\uDCA9"),
        TRACTOR              ("\uD83D\uDE9C"),
        TOILET               ("\uD83D\uDEBD"),
        THUMBS_UP            ("\uD83D\uDC4D"),
        THUMBS_DOWN          ("\uD83D\uDC4E"),
        WHATEVER             ("\uD83E\uDD37\u200D♂️"),
        FACEPALM             ("\uD83E\uDD26\u200D♂️");



        private final String id;

        Reactions(String id) {
            this.id = id;
        }

        public String getValue() {
            return this.id;
        }
    }
}
