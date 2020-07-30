import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.Paths;

public class SpellChecker {

    public static Hashtable<Integer, String> dictionary = new Hashtable<Integer, String>();

    public static void main(final String args[]) throws FileNotFoundException, IOException {
        // this reads the dictionary in to a hashtable so we can use
        Scanner sc = new Scanner(new FileReader("dictionary.txt"));
        final ArrayList<String> myList = new ArrayList<String>();
        ArrayList<String> incorrectWords = new ArrayList<String>();
        ArrayList<String> correctWords = new ArrayList<String>();
        sc.nextLine();
        int num = 1;
        while (sc.hasNextLine()) {
            final String value = sc.nextLine();
            dictionary.put(num, value);
            num++;
        }
        // this takes in the sample file we need to check the words
        String path = "";
        try{
            path = args[0];
            sc = new Scanner(new FileReader(path));
        }
        catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Please pass in a single sample file as an argumemt when running this application");
            System.exit(0);
        }
        catch(Exception e){
            System.out.println("Something went wrong reading the file :" + e);
            System.exit(0);
        }

        while (sc.hasNextLine()) {
            final String line = sc.nextLine();
            final String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
            for (int i = 0; i < words.length; i++) {
                myList.add(words[i]);
            }
        }
        sc.close();
        // check if the words are are in the dictionary
        File checked = new File("spellChecked.txt");
        for (int i = 0; i < myList.size(); i++) {
            if (!dictionary.contains(myList.get(i))) {
                incorrectWords.add(myList.get(i));
                verifyAnswer(myList.get(i), checked, correctWords);
            }

        }//merge the new words in to the dictionary
        File sample = new File(path);
        File finalFile = new File("FinalFile.txt");
        Files.copy(sample.toPath(), finalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        String placeholder = fileToString("FinalFile.txt");
        for (int i = 0; i < correctWords.size(); i++) {
            placeholder = placeholder.replaceFirst("(?i)\\b" + incorrectWords.get(i) + "\\b", correctWords.get(i));
        }


        FileWriter niccoloMachiavelli = new FileWriter(finalFile, false);
        niccoloMachiavelli.write(placeholder);
        niccoloMachiavelli.close();

        BufferedWriter func = new BufferedWriter(new FileWriter(new File("dictionary.txt"), true));
        for (String string : correctWords) {
            if (!dictionary.contains(string)) {
                func.write(string);
                func.newLine();
            }

        }
        System.out.println("::: Appended 'new' corrected words to dictionary file :::");
        func.close();

    }
    // the soundex implementation
    public static String soundex(String s) {
        char[] x = s.toUpperCase().toCharArray();
        char firstLetter = x[0];

        // convert letters to numeric code
        for (int i = 0; i < x.length; i++) {
            switch (x[i]) {

                case 'B':
                case 'F':
                case 'P':
                case 'V':
                    x[i] = '1';
                    break;

                case 'C':
                case 'G':
                case 'J':
                case 'K':
                case 'Q':
                case 'S':
                case 'X':
                case 'Z':
                    x[i] = '2';
                    break;

                case 'D':
                case 'T':
                    x[i] = '3';
                    break;

                case 'L':
                    x[i] = '4';
                    break;

                case 'M':
                case 'N':
                    x[i] = '5';
                    break;

                case 'R':
                    x[i] = '6';
                    break;

                default:
                    x[i] = '0';
                    break;
            }
        }

        // remove duplicates
        String output = "" + firstLetter;
        for (int i = 1; i < x.length; i++)
            if (x[i] != x[i - 1] && x[i] != '0')
                output += x[i];

        // pad with 0's or truncate
        output = output + "0000";
        return output.substring(0, 4);
    }
    // theis is the function checks in the words are spelt correctly
    public static void verifyAnswer(String content, File checked, ArrayList<String> corList) throws IOException {
        String ref = soundex(content);
        System.out.println("NOT FOUND : " + content);
        System.out.println("Is this word spelt correctly? y/n");
        Scanner scanner = new Scanner(System.in);
        String inputString = scanner.nextLine();
        if (inputString.toLowerCase().equals("y")) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(checked, true));
            writer.newLine();
            writer.write(content);
            corList.add(content);
            writer.close();
        } else if (inputString.toLowerCase().equals("n")) {
            dictionary.forEach((k, v) -> {
                if (ref.equals(soundex(v))) {
                    System.out.println("Suggested word : " + k + " " + v);
                }
            });

            System.out.println("type the number for the suggested word or type 'n' to write you own");
            newS(scanner, checked, corList);
            // System.out.println("Please type in the correct alternative");

        }
    }
    // this method allows you to entre a replacement word or pick from the suggested words, and then writes in to the check words file
    public static void newS(Scanner scanner, File checked, ArrayList<String> corList) {
        String newString = scanner.nextLine();
        try {
            if (newString.toLowerCase().equals("n")) {
                System.out.println("Now type in you replacement word");
                String newString1 = scanner.nextLine();
                BufferedWriter writer = new BufferedWriter(new FileWriter(checked, true));
                writer.newLine();
                writer.write(newString1);
                corList.add(newString1);
                writer.close();
            } else {
                BufferedWriter writer = new BufferedWriter(new FileWriter(checked, true));
                writer.write(dictionary.get(Integer.parseInt(newString)));
                corList.add(dictionary.get(Integer.parseInt(newString)));
                writer.close();
            }

        } catch (Exception e) {
            System.out.println("You did not enter a correct number, try again or type 'n' to write you own");
            newS(scanner, checked, corList);

        }
    }
    // converts the file in to a string, so it can be rebuilt.
    static String fileToString(String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }
}