package CodeRM;

import Utilities.ParsePGM;
import Alteration.Alteration;

import java.util.ArrayList;
import java.util.Collections;
import java.math.BigInteger;

/**
 * Class to implement the Reed-Muller code (RM(1, r) -> First order, length r).
 *
 * Reed-Muller code is a error-correcting code.
 *
 * Features :
 *
 *  - Encode
 *
 *  - Decode
 *
 *  - Correction of errors
 *
 * All operations above can be performed on a word or an image (.pgm).
 *
 * A word is represented by a Integer (Class 'BigInteger').
 * Methods of class 'BigInteger' can be used to access one specific bit.
 *
 * Minimal distance is 2^(r-1) and the code can correct up to 2^(r-2) - 1
 *
 * Size of word before encoding :   r + 1
 * Size of word after encoding :    2^r
 *
 * Warning :
 *  - In the theory bit 0 is in the left. (x0, x1, x2, x3)
 *  - In practice, bit 0 is in the right. (x3, x2, x1, x0)
 *
 * Warning :
 *  - Encoding and decoding of image is done on a buffer.
 *    - To load an image, use 'ParsePGM.read(filename)'
 *    - To save an image (buffer), use 'ParsePGM.writeString(filename, buffer)'
 *
 *  It has been done that way because of the 'Main.java', which was imposed and already done that way.
 *
 * @author  Axel Fahy
 * @author  Rudolf Höhn
 *
 * @version 01.05.2015
 */
public class ReedMuller {

    // Length of the code.
    // Words will be of length r + 1.
    // Encoded words will be of length 2^r.
    private int r;

    /**
     * Constructor with the rang.
     */
    public ReedMuller(int rang) {
        this.r = rang;
    }

    /**
     * Encode a word.
     *
     * The encoding matrix is not stored.
     *
     * We can calculate it on the fly.
     *
     * The matrix is composed of r + 1 lines and r^2 columns.
     * The last line is fill with 1 values.
     * The others values are composed as below :
     * For each columns, its binary value is written in the the first r lines.
     * The bits 0 of the values is at the first line.
     *
     * Example : r = 3
     *
     *  0 1 0 1 0 1 0 1
     *  0 0 1 1 0 0 1 1
     *  0 0 0 0 1 1 1 1
     *  1 1 1 1 1 1 1 1
     *
     * @param word The word to encode.
     * @return The encoded word.
     */
    public BigInteger encode(BigInteger word) {

        int length = (int)Math.pow(2, r);
        BigInteger code = new BigInteger("0");

        for (int i = 0; i < length; i++) {
            // Conversion of i in binary.
            String iBinary = Integer.toBinaryString(i);
            // Fill in with 0 if missing (0 at the left).
            iBinary = fillZeroLeft(iBinary, r);
            // Add "1" at the beginning.
            iBinary = "1" + iBinary;
            // Multiplication of the word by the column of the matrix.
            int sum = 0;
            for (int j = 0; j < r + 1; j++) {
                int bit = word.testBit(j) ? 1 : 0;
                sum += bit * Character.getNumericValue((iBinary.charAt(r - j)));
            }
            sum %= 2;
            // Set the correct value for the bit.
            if (sum == 1) {
                code = code.setBit(i);
            }
        }
        return code;
    }

    /**
     * Encode a file.
     * The content of the file is already in a String.
     *
     * Run overs each value of the PGM image.
     * Each value is separated by a space.
     *
     * @param buffer The content of the image to encode.
     * @return The encoded image in a String.
     */
    public String encode(String buffer) {
        String header = ParsePGM.readHeader(buffer);
        String data = ParsePGM.readData(buffer);
        String output = "";

        for (String s : data.split("\\s+")) {
            // Exclude whitespaces
            if (s.trim().length() > 0) {
                BigInteger word = new BigInteger(s.trim());
                output += this.encode(word).toString();
                output += " ";
            }
        }
        return header + output;
    }

    /**
     * Fill the rest of the string with '0'.
     *
     * For example, if we have the value 1 on three bits :
     * Before   : "1"
     * After    : "003"
     *
     * @param sBinary The string to fill with missing '0'
     * @param length  The length of the binary value.
     * @return The string with the '0' added at the left.
     */
    public String fillZeroLeft(String sBinary, int length) {
        while (sBinary.length() < length) {
            sBinary = "0" + sBinary;
        }
        return sBinary;
    }

    /**
     * Get a BigInteger from a String.
     *
     * If the value is negative, adapt the BigInteger.
     *
     * @param value     Value in a string.
     * @param length    Number of bits.
     * @return          The BigInteger from the string.
     */
    public BigInteger getBigInteger(String value, int length) {
        BigInteger word = new BigInteger(value);
        if (word.compareTo(BigInteger.ZERO) < 0) {
            word = word.add(BigInteger.ONE.shiftLeft(length));
        }
        return word;
    }

    /**
     * Decode an encoded word.
     *
     * We check the first bit (at the left) of the coded word.
     * If this is a "1", we need to inverse it before running the algorithm.
     *
     * Then, for each power of two with the power smaller than r, we get the value of the position,
     * and affect if to the word.
     *
     * @param code The encoded word.
     * @return The decoded word.
     */
    public BigInteger decode(BigInteger code) {
        BigInteger word = new BigInteger("0");

        // Check for the first bit.
        // If 1, inverse all bits and first bit from left is 1 (last bit).
        if (code.testBit(0)) {
            code = code.not();
            word = word.setBit(r);
        }

        // Get values of power 2 positions.
        for (int i = 0; i < r; i++) {
            word = code.testBit((int)Math.pow(2, i)) ? word.setBit(i) : word.clearBit(i);
        }
        return word;
    }

    /**
     * Decode a file.
     * The content of the file is already in a String.
     *
     * Run overs each values of the encoded file
     * and decodes them.
     *
     * @param buffer The content of the file to decode.
     * @return The decoded file in a string.
     */
    public String decode(String buffer) {
        String header = ParsePGM.readHeader(buffer);
        String data = ParsePGM.readData(buffer);
        String output = "";

        for (String s : data.split("\\s+")) {
            // Exclude whitespaces
            if (s.trim().length() > 0) {
                //BigInteger word = new BigInteger(s.trim());
                BigInteger word = getBigInteger(s.trim(), (int)Math.pow(2, r));
                output += this.decode(word).toString();
                output += " ";
            }
        }
        return header + output;
    }

    /**
     * Unaltered an encoded image.
     *
     * This method uses the Fourier algorithm.
     *
     * @param buffer  The altered image.
     * @return      The unaltered word.
     */
    public String unalter (String buffer) {
        String header = ParsePGM.readHeader(buffer);
        String data = ParsePGM.readData(buffer);
        String output = "";

        for (String s : data.split("\\s+")) {
            // Exclude whitespaces
            if (s.trim().length() > 0) {
                //BigInteger word = new BigInteger(s.trim());
                BigInteger word = getBigInteger(s.trim(), (int)Math.pow(2, r));
                output += this.unalter(word).toString();
                output += " ";
            }
        }
        return header + output;
    }

    /**
     * Unaltered an encoded word.
     *
     * This method uses the Fourier algorithm.
     *
     * @param mot  The altered word.
     * @return      The unaltered word.
     */
    public BigInteger unalter (BigInteger mot) {

        // Transformer le mot en string pour avoir tous les 0 qu'il faut.
        String motBinary = mot.toString(2);
        motBinary = fillZeroLeft(motBinary, (int)Math.pow(2, r));

        // Stocker le mot dans un ArrayList avec des -1 à la place des 1 et des 1 à la places des 0.
        ArrayList<Integer> F = new ArrayList<Integer>();
        for (int i = 0; i < motBinary.length(); i++) {
            if (motBinary.charAt(i) == '1') {
                F.add(-1);
            }
            else {
                F.add(1);
            }
        }

        String iBinary;

        // Boucle de n à 1 pour k
        for (int n = r - 1; n >= 0; n--) { // K
            ArrayList<Integer> Ftmp = new ArrayList<Integer>();
            for (int i = 0; i < Math.pow(2, r); i++) { // Position (colonne) dans K
                // Transfrome  i en binaire.
                iBinary = Integer.toBinaryString(i);
                iBinary = fillZeroLeft(iBinary, r);

                // Récupère suivant le K, le nième bit de i.
                int posK = Integer.parseInt(Character.toString(iBinary.charAt(r - 1 - n)));

                // les tests et additions
                if (posK == 1) {
                    Ftmp.add(F.get(i) - F.get(i - (int)Math.pow(2, n)));
                }
                else {
                    Ftmp.add(F.get(i) + F.get(i + (int)Math.pow(2, n)));
                }
            }
            F = Ftmp;   // F est mis à jour. C'est ce F qui sera utilisé pour le K suivant.
        }

        // On trouve le maximum en valeur absolue.
        ArrayList<Integer> Fpositif = new ArrayList<Integer>();
        for (Integer i : F) {
            Fpositif.add(Math.abs(i));
        }

        // On trouve la maximum de Fpositif.
        int maxF = Collections.max(Fpositif);

        // On trouve la valeur décodé et débruitée.
        int motDebruiteEtDecode;
        int posMaxF = Fpositif.indexOf(maxF);

        if (F.get(posMaxF) < 0) {
            motDebruiteEtDecode = posMaxF + (int)Math.pow(2, r);
        }
        else {
            motDebruiteEtDecode = posMaxF;
        }

        // On retourne le mot encodé.
        return this.encode(new BigInteger(String.valueOf(motDebruiteEtDecode)));
    }

    public static void main(String[] args) {
        int r = 5;
        ReedMuller rm = new ReedMuller(r);

        // Encoding
        BigInteger word = new BigInteger("13");
        BigInteger code = rm.encode(word);
        BigInteger codeB = Alteration.alter(code, 0.3);
        BigInteger codeUB = rm.unalter(codeB);
        BigInteger wordEnd = rm.decode(codeUB);

        System.out.println("Word    : " + word +    " Word    : " + word.toString(2));
        System.out.println("Code    : " + code +    " Code    : " + code.toString(2));
        System.out.println("CodeB   : " + codeB +   " CodeB   : " + codeB.toString(2));
        System.out.println("CodeUB  : " + codeUB +   " CodeUB  : " + codeUB.toString(2));
        System.out.println("wordEnd : " + wordEnd + " wordEnd : " + wordEnd.toString(2));


        System.out.println("Encode and decode an image :");
        r = 5;
        String buffer = ParsePGM.read("lena_128x128_64.pgm");
        String sEncoded = rm.encode(buffer);
        String sDecoded = rm.decode(sEncoded);
        ParsePGM.writeString("lena_encoded_decoded.pgm", sDecoded);


        r = 5;
        //String sMarsAlter = ParsePGM.read("mars_crat.enc.alt");
        String sMarsAlter = ParsePGM.read("mars-crat.enc.alt_0.07");
        //String sMarsAlter = ParsePGM.read("mars-crat.enc.alt_0.10");
        //String sMarsAlter = ParsePGM.read("mars_decoded.pgm");
        String sMarsUnalter = rm.unalter(sMarsAlter);
        String sMarsDecoded = rm.decode(sMarsUnalter);
        ParsePGM.writeString("mars_decoded.pgm", sMarsDecoded);
        System.out.println("Done");

    }

}
