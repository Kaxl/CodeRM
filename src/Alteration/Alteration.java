package Alteration;

import Utilities.ParsePGM;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created by rudy on 19.05.15.
 */
public class Alteration {

    /**
     * Alter an encoded word.
     *
     * We generate a random number for each bit of the word and if the
     * generated number is lower than the line, we flip the bit at the position i.
     *
     * @param mot  The encoded word.
     * @param line  The line that we want our word altered.
     * @return      The altered word.
     */
    public static BigInteger alter (BigInteger mot, double line) {
        Random random = new Random();

        for (int i = 0; i < mot.bitCount(); i++) {
            if (line > random.nextFloat()) {
                mot = mot.flipBit(i);
            }
        }

        return mot;
    }

    /**
     * Alter an encoded word.
     *
     * We generate a random number for each bit of the word and if the
     * generated number is lower than the line, we flip the bit at the position i.
     *
     * @param buffer  The image.
     * @param line  The line that we want our word altered.
     * @return      The altered word.
     */
    public static String alter(String buffer, double line) {
        String header = ParsePGM.readHeader(buffer);
        String data = ParsePGM.readData(buffer);
        String output = "";

        for (String s : data.split(" ")) {
            // Exclude whitespaces
            if (s.trim().length() > 0) {
                BigInteger word = new BigInteger(s.trim());
                output += Alteration.alter(word, line).toString();
                output += " ";
            }
        }
        return header + output;
    }
}
