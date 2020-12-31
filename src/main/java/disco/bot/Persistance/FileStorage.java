package disco.bot.Persistance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class FileStorage {
    private static final String filename = "data.txt";

    public static void main(String[] args) {
        editExistingRecord("User2", "1;3;5");
        System.out.println( getAllUsers() );
        for (String str : getAllUsers()) {
            System.out.println(str);
        }
    }

    public static void editExistingRecord( String userId, String str ) {
        List<String> allUsers = getAllUsers();
        allUsers.set( getAllUsers().stream()
                .filter( user -> user.contains( userId ) )
                .map( allUsers::indexOf )
                .findFirst().get(), String.format("%s:%s", userId, str) );
        overwriteAllUsers( String.join("\n", allUsers) );
    }

    public static void overwriteAllUsers( String str ) {
        try {
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.write( str );
            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void addNewUserRecord( String userId, String str ) {
        try {
            List<String> users = getAllUsers();
            FileWriter fileWriter = new FileWriter(filename);
            fileWriter.write( String.join("\n", users) + String.format("\n%s:%s", userId, str) );
            fileWriter.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<String> getAllUsers() {
        try {
            List<String> lines = new ArrayList<>();
            File file = new File(filename);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                lines.add( myReader.nextLine() );
            }
            myReader.close();
            return lines;
        } catch (FileNotFoundException e) { e.printStackTrace(); }
        return Collections.emptyList();
    }
}
