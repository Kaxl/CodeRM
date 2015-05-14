package Utilities;

import java.io.*;

/**
 * Class to handle the PGM files.
 *
 * Functionnalities :
 *
 *  Get the header of the PGM file.
 *  Get the data of the PGM file.
 *
 *  Write a string at the end of a file.
 *      - Could be used to write the header in a new file of the data in a file containing already the header.
 *
 *  The header is built in the current form :
 *      - The first line contains "P2".
 *      - Then we can have some comments (line starting with '#').
 *        We don't know the number of comments lines.
 *      - The line containing the size of the image.
 *      - The line containing the gray level.
 *
 * @author  Axel Fahy
 *
 * @version 12.05.2015
 */
public class ParsePGM {

    /**
     * Read a file and put it in a StringBuffer.
     * Since this is a static method, it can be used without a class instance.
     *
     * @param filename  The filename to load.
     * @return          The String.
     */
    public static String read(String filename) {
        // Load the file into a String.
        String text = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {
                text += line;
                text += "\n";
                line = br.readLine();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }

    /**
     * NOT USED
     * Method to get the header of a PGM file.
     *
     * @param filename The file to parse.
     * @return A string containing the header.
     */
    public static String readHeaderFile(String filename) {
        String header = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            // Get the headers
            header += line;     // Put the first line into the header.
            do {
                line = br.readLine();
                header += '\n';
                header += line;
            }
            while (line != null && line.charAt(0) == '#');

            // Get the two last lines of the header (size and gray level).
            header += '\n';
            header += br.readLine();
            header += '\n';
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return header;
    }

    /**
     * Method to get the header of a PGM file.
     * The content of the PGM file is already in a String.
     *
     * @param s The string to parse.
     * @return A string containing the header.
     */
    public static String readHeader(String s) {
        String header = "";
        try {
            BufferedReader br = new BufferedReader(new StringReader(s));
            String line = br.readLine();
            // Get the headers
            header += line;     // Put the first line into the header.
            do {
                line = br.readLine();
                header += '\n';
                header += line;
            }
            while (line != null && line.charAt(0) == '#');

            // Get the two last lines of the header (size and gray level).
            header += '\n';
            header += br.readLine();
            header += '\n';
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return header;
    }

    /**
     * NOT USED
     * Method to get the data of a PGM file.
     *
     * @param filename The file to parse.
     * @return A string containing the data.
     */
    public static String readDataFile(String filename) {
        String data = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            // Skip the headers
            do {
                line = br.readLine();
            } while (line != null && line.charAt(0) == '#');
            br.readLine();

            // Get the data
            line = br.readLine();
            while (line != null) {
                data += line;
                data += '\n';
                line = br.readLine();
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Method to get the data of a PGM file.
     * The content of the PGM file is already in a String.
     *
     * @param s The string to parse.
     * @return A string containing the data.
     */
    public static String readData(String s) {
        String data = "";
        try {
            BufferedReader br = new BufferedReader(new StringReader(s));
            String line = br.readLine();
            // Skip the headers
            do {
                line = br.readLine();
            } while (line != null && line.charAt(0) == '#');
            br.readLine();

            // Get the data
            line = br.readLine();
            while (line != null) {
                data += line;
                data += '\n';
                line = br.readLine();
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Write a string at the end of a file.
     *
     * @param filename The name of the file.
     */
    public static void writeString(String filename, String s) {
        try {
            BufferedWriter bw = null;
            bw = new BufferedWriter(new FileWriter(filename, true));
            bw.write(s);
            bw.newLine();
            bw.flush();
            bw.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


}
