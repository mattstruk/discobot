package disco.bot;

import disco.bot.Services.MiddleFingerAlphabet;
import disco.bot.Services.ToDoNotificatons;
import disco.bot.Services.Web.ACLParser;
import disco.bot.Services.Web.FIETParser;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.Reaction;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.MessageData;
import discord4j.rest.http.client.ClientException;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.StrictMath.abs;

public class Bot {


    private static GatewayDiscordClient client;
    private static String BOT_ID;
    private static final String CHANNEL_DIVIDER = ":";

    static {
        try {
            client = DiscordClientBuilder
                        .create(Files.readAllLines(Paths.get(System.getProperty("user.dir") + "/file.txt")).get(0))
                        .build()
                        .login()
                        .block();
             BOT_ID = client.getSelfId().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException  {

        Random random = new Random();
        AtomicInteger round = new AtomicInteger(2);
        final boolean[] luka = {false};
        List<Bot.Reactions> ankietyAllowedReactions = Arrays.asList(Reactions.THUMBS_UP , Reactions.THUMBS_DOWN, Reactions.WHATEVER, Reactions.FACEPALM );
        List<String> commands = Arrays.asList("!dubson", "!luka", "!maser", "!losuj", "!ping", "!roll");

        client.getEventDispatcher()
                .on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    System.out.println(String.format("Logged in as %s#%s.", self.getUsername(), self.getDiscriminator()));
                    System.out.println( "Wersja z 12 grudnia 2020 13:00." );
                });

        /** Reactions event listener
         */
        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(event -> {
            int countOfReactions = event.getMessage().block().getReactions().stream().mapToInt(Reaction::getCount).sum();
            if ( isNotABotAndProperChannel( event ) )
                try {
                    if ( isReactionAllowed( event, ankietyAllowedReactions )  || hasReactedBefore( event, ankietyAllowedReactions, countOfReactions ) ) {
                        client.getMessageById(Snowflake.of(event.getChannelId().asLong()), Snowflake.of(event.getMessageId().asLong())).subscribe(e ->
                                e.removeReaction(event.getEmoji(), event.getUserId()).subscribe());
                    } else if ( event.getMessage().block().getReactions().stream().filter( reaction -> reaction.getEmoji().asUnicodeEmoji().isPresent() && reaction.getEmoji().asUnicodeEmoji().get().equals( ReactionEmoji.unicode( Reactions.LOCKED.getValue() ) )  ).count() != 1 ) {
                        makeAnnouncementIfEveryoneVoted( event, ankietyAllowedReactions, countOfReactions );
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        });

        /** Messages event listener
         */
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(event -> {

                    if ( event.getMessage().getChannelId().equals( Snowflake.of( ChannelsId.PRIV_DIRECT.getId()) ) &&
                            event.getMessage().getAuthor().get().getId().asString().equals( UserId.LUCJAN.getId() ) ) {
                        String channelId = extractChannelFromMessage( event.getMessage().getContent() );
                        if (channelId != null)
                            client.getRestClient()
                                    .getChannelById(Snowflake.of( channelId ))
                                    .createMessage( StringUtils.substringAfter( event.getMessage().getContent(), CHANNEL_DIVIDER) ).subscribe();
                    }

                    if ( event.getMessage().getChannelId().equals(Snowflake.of( ChannelsId.ANKIETY.getId() )) ) initPoll( event, ankietyAllowedReactions );

                    String msg = event.getMessage().getContent().toLowerCase();

                    msg = msg.contains("!") ? "!" + StringUtils.substringAfter( msg, "!" ) : msg;

                    if ( StringUtils.isBlank( msg )) return;

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

                    if ( msg.contains("!fuckertext") ) convertTextToFuckers( event, msg );

                    if ( luka[0] && authorId(event).equals(Bot.UserId.LUKA.id) ) reactUnicode( event, Reactions.MIDDLE_FINGER );

                    if ( event.getMessage().getChannelId().equals(Snowflake.of(ChannelsId.ANKIETY.getId())) ) initPoll( event, ankietyAllowedReactions );

                    if ( msg.contains("!ping") ) pingMsg( event, random);

                    try {

                        if (msg.equals("!preqwek")) createMessage(event, codeBlockWrapper(ACLParser.getACLWEKPreq(round.intValue())));

                        if (msg.contains("!generalkagt4")) {
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4DriverStandings()));
                            createMessage(event, codeBlockWrapper(ACLParser.getACLGT4TeamStandings()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if ( msg.equals("!roll") ) rollTheDice( event, random );

                    if (msg.contains("!dodajevent")) addToCalendar(event, "event");

                    if( msg.contains("!dodajwyscig")) addToCalendar(event, "race");

                    if( msg.contains("!dodajzadanie")) addTask(event);

                    if( msg.contains("!setround")) round.set(setRound(event));

                });

        try {
            List<String> timeAndMessage = FIETParser.parseFIETServer();
            String savedTime = timeAndMessage != null && timeAndMessage.isEmpty() ? null : timeAndMessage.get(0);

            while (true) {
                int seconds = 120;
                Thread.sleep(1000 * seconds);
                List<String> freshTimeAndMessage = FIETParser.parseFIETServer();
                String freshTime = freshTimeAndMessage.isEmpty() ? null : freshTimeAndMessage.get(0);
                if (freshTime != null && savedTime != null && !freshTime.equals(savedTime)) {
                    client.getRestClient().getChannelById(Snowflake.of(ChannelsId.ACL_WEK.getId())).createMessage(freshTimeAndMessage.get(1)).subscribe();
                    savedTime = freshTime;
                }

                String[] currentTime = new SimpleDateFormat("HH:mm:ss:dd:MM:yyyy").format(Calendar.getInstance().getTime()).split(":");

                if (Stream.of(9, 12, 15, 18, 21).anyMatch( i -> isTimeMatching( currentTime, i ))) {
                    List<MessageData> listMono = client.getRestClient()
                            .getChannelById(Snowflake.of(ChannelsId.TODO.getId()))
                            .getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();

                    if (listMono != null) {
                        try {
                            String getMessage = ToDoNotificatons.notifications(listMono.get(0).content(), currentTime[0]);
                            if (StringUtils.isNotBlank( getMessage ))
                                client.getRestClient()
                                        .getChannelById(Snowflake.of(Bot.ChannelsId.POGADANKI.getId()))
                                        .createMessage("Przypomnienie o rzeczach do zrobienia z kanalu #\uD83D\uDCDDto-do : \n" + getMessage).subscribe();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    Thread.sleep(1000 * seconds);
                }

                reminderPLP(currentTime);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void addToCalendar(MessageCreateEvent event, String mode){

        List<MessageData> calendarmsg = client.getRestClient()
                .getChannelById(Snowflake.of(ChannelsId.KALENDARZ.getId()))
                .getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();

        String content = "";
        if(mode == "event") content = "\n**" + event.getMessage().getContent().substring(12) + "**";
        else if(mode == "race") content = event.getMessage().getContent().substring(13);

        client.getRestClient()
                .getChannelById(Snowflake.of(ChannelsId.KALENDARZ.getId()))
                .createMessage(calendarmsg.get(calendarmsg.size()-1).content() + "\n" + content).subscribe();

        Mono.just(event.getMessage())
                .flatMap(e -> client.getMessageById(Snowflake.of(ChannelsId.KALENDARZ.getId()), Snowflake.of(calendarmsg.get(calendarmsg.size()-1).id()))
                        .onErrorResume(ClientException.isStatusCode(HttpResponseStatus.FORBIDDEN.code()), err -> Mono.empty())
                        .flatMap(Message::delete))
                .subscribe();

        createMessage(event, "Dodano zawartość do kalendarza");
    }

    private static void addTask(MessageCreateEvent event){

        List<MessageData> todomsg = client.getRestClient()
                .getChannelById(Snowflake.of(ChannelsId.TODO.getId()))
                .getMessagesBefore(Snowflake.of(Instant.now())).collectList().block();

        String content = event.getMessage().getContent().substring(14);

        client.getRestClient()
                .getChannelById(Snowflake.of(ChannelsId.TODO.getId()))
                .createMessage(todomsg.get(todomsg.size()-1).content() + "\n• " + content).subscribe();

        Mono.just(event.getMessage())
                .flatMap(e -> client.getMessageById(Snowflake.of(ChannelsId.TODO.getId()), Snowflake.of(todomsg.get(todomsg.size()-1).id()))
                        .onErrorResume(ClientException.isStatusCode(HttpResponseStatus.FORBIDDEN.code()), err -> Mono.empty())
                        .flatMap(Message::delete))
                .subscribe();

        createMessage(event, "Dodano zadanie");
    }

    private static int setRound(MessageCreateEvent event){
        Mono.just(event.getMessage())
                .flatMap(e -> client.getMessageById(event.getMessage().getChannelId(), event.getMessage().getId())
                        .onErrorResume(ClientException.isStatusCode(HttpResponseStatus.FORBIDDEN.code()), err -> Mono.empty())
                        .flatMap(Message::delete))
                .subscribe();
        return Integer.parseInt(event.getMessage().getContent().substring(10));

    }

    private static String extractChannelFromMessage( String content ) {
        String identificator = StringUtils.substringBefore(content, CHANNEL_DIVIDER);
        switch (identificator) {
            case "pogadanki":
                return ChannelsId.POGADANKI.getId();
            case "simgrid":
                return ChannelsId.SIM_GRID.getId();
            case "acl-wek":
                return ChannelsId.ACL_WEK.getId();
            default:
                return null;
        }
    }

    private static void reminderPLP(String[] currentTime) throws InterruptedException {
        if (  Stream.of(12, 15, 18).anyMatch( i -> isTimeMatching( currentTime, i )) &&
                 Stream.of("03;12", "17;12", "07;01", "21;01", "04;02", "18;02", "04;03")
                         .map(s -> s.split(";"))
                         .anyMatch( dayMonth -> isDateMatching(currentTime, dayMonth[0], dayMonth[1] ) ) )  {
            client.getRestClient()
                    .getChannelById(Snowflake.of(Bot.ChannelsId.POGADANKI.getId()))
                    .createMessage("@everyone Przypomnienie o aktualizacji PLP przed dzisiejsza runda. Link do pobrania: https://tinyurl.com/y6zjrdv6").subscribe();

            Thread.sleep(60000);
        }
    }

    private static boolean isDateMatching(String[] currentTime, String s, String s1) {
        return currentTime[3].equals(s) && currentTime[4].equals(s1);
    }

    private static void makeAnnouncementIfEveryoneVoted(ReactionAddEvent event, List<Bot.Reactions> ankietyAllowedReactions, int countOfReactions ) throws InterruptedException {
        Set<String> usersThatReacted = new HashSet<>();
        HashMap<String, List<String>> reactionsAndUsers = new LinkedHashMap<>();
        AtomicInteger counter = new AtomicInteger();


        client.getMessageById( Snowflake.of( event.getChannelId().asLong() ), Snowflake.of( event.getMessageId().asLong() ) )
                .subscribe( e ->
                    ankietyAllowedReactions.forEach( r -> {
                        counter.getAndDecrement();
                        List<String> temporary = new ArrayList<>();
                        e.getReactors( ReactionEmoji.unicode( r.getValue() ) ).toStream().forEach(user -> {
                            if ( !BOT_ID.equals( user.getId().asString() ) ) {
                                temporary.add(user.getUsername());
                                usersThatReacted.add(user.getId().asString());
                            }
                            counter.getAndIncrement();
                        });
                        reactionsAndUsers.put(r.getValue(), temporary);
                        counter.getAndIncrement();
                    })
                );

        int time = 200;
        while (counter.get() < countOfReactions && time < 10000) {
            int sleep = 200;
            time += sleep;
            Thread.sleep( sleep );
        }
        counter.set(0);

        String pollResults = StringUtils.replaceEach( reactionsAndUsers.toString(),
                                                   new String[] {"], ", "{", "}", "[", "]", "="},
                                                   new String[] {"\n" , "" , "" , "" , "" , ""});

        if ( hasEveryoneVoted( usersThatReacted ) ) {
            client.getRestClient()
                    .getChannelById(Snowflake.of( ChannelsId.OGLOSZENIA.getId() ))
                    .createMessage(String.format("Wyniki ankiety pt. **%s** : \n", event.getMessage().block().getContent()) + pollResults).subscribe();
            event.getMessage().block().addReaction( ReactionEmoji.unicode( Reactions.LOCKED.getValue() ) ).subscribe();
        }
    }

    private static boolean hasEveryoneVoted( Set<String> usersThatReacted ) {
        return usersThatReacted.containsAll( Stream.of( UserId.values() ).map( UserId::getId ).collect(Collectors.toSet()) );
    }

    private static boolean isNotABotAndProperChannel(ReactionAddEvent event) {
        return !event.getUserId().asString().equals( BOT_ID ) && event.getChannelId().equals( Snowflake.of( ChannelsId.ANKIETY.getId() ) );
    }

    private static boolean isReactionAllowed( ReactionAddEvent event, List<Reactions> ankietyAllowedReactions ) {
        return !(event.getEmoji().asUnicodeEmoji().isPresent() && ankietyAllowedReactions.stream().anyMatch(r -> ReactionEmoji.unicode( r.getValue() ).equals( ReactionEmoji.unicode( event.getEmoji().asUnicodeEmoji().get().getRaw() ) ) ) );
    }

    private static void rollTheDice(MessageCreateEvent event, Random random ) {
        String message = String.format("%s rolls %d (0-100)", event.getMessage().getUserData().username(), random.nextInt(101) );
        createMessage( event, message );
    }

    private static boolean isTimeMatching(String[] currentTime, int hour ) {
        return ( currentTime[0].equals( String.format("%02d", hour) ) && ( currentTime[1].equals("00") || currentTime[1].equals("01") || currentTime[1].equals("02")  ) );
    }

    private static boolean hasReactedBefore( ReactionAddEvent event, List<Bot.Reactions> ankietyAllowedReactions, int countOfReactions ) throws InterruptedException {
        List<Snowflake> usersThatReacted = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger();

        client.getMessageById( Snowflake.of( event.getChannelId().asLong() ), Snowflake.of( event.getMessageId().asLong() ) )
              .subscribe( e ->
                      ankietyAllowedReactions.forEach( r ->
                              e.getReactors( ReactionEmoji.unicode( r.getValue() ) )
                               .subscribe( user -> {
                                    usersThatReacted.add(user.getId());
                                    counter.getAndIncrement();
        })));

        int time = 200;
        while (counter.get() < countOfReactions && time < 3000) {
            int sleep = 200;
            time += sleep;
            Thread.sleep( sleep );
        }
        counter.set(0);

        return Collections.frequency(usersThatReacted, event.getUserId()) >= 2;
    }


    private static void pingMsg(MessageCreateEvent event, Random random) {
        List<String> ping = Arrays.asList("Lucjana", "Luki", "JarzomBa", "DubSona", "mknbla", "Maniaka");
        Duration between = Duration.between(event.getMessage().getTimestamp(), Instant.now());
        if(abs(between.toMillis()) < 1500){
            event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Pong! Opóźnienie wynosi " + abs(between.toMillis()) + "ms. To na pewno mniej niż opóźnienie " + ping.get(random.nextInt(ping.size())) + "! " + disco.bot.Bot.Reactions.MIDDLE_FINGER.getValue())).subscribe();
        }
        else event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Pong! Opóźnienie wynosi " + abs(between.toMillis()) + "ms. Totalna kompromitacja. Sugeruję nastawić zegarek lub dorzucić węgla do RaspberryPi" + disco.bot.Bot.Reactions.MIDDLE_FINGER.getValue())).subscribe();
    }

    private static void initPoll(MessageCreateEvent event, List<Bot.Reactions> ankietyAllowedReactions) {
        ankietyAllowedReactions.forEach( reaction -> reactUnicode( event, reaction ));
    }

    private static void convertTextToFuckers( MessageCreateEvent event, String message ) {
        String fuckerText = MiddleFingerAlphabet.printFuckerText( message );
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
        ACL_WEK     ("778000167516373012"),
        OGLOSZENIA  ("700670367131500624"),
        KALENDARZ   ("767789225458270227"),
        PRIV_DIRECT ("784106290523537438"),
        TEST_CHNL   ("787522026348478484"),
        TEST_TODO   ("787553581208961044"),
        SIM_GRID    ("767804272057909258");

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
        MANIAK      ("658052410559823882"),
        MATEUSZ     ("471116345610862595");

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
        FACEPALM             ("\uD83E\uDD26\u200D♂️"),
        LOCKED               ("\uD83D\uDD10");



        private final String id;

        Reactions(String id) {
            this.id = id;
        }

        public String getValue() {
            return this.id;
        }
    }
}
