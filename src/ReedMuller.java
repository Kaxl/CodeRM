import java.math.BigInteger;
import java.util.Random;

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
     * Default constructor
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
     * @param word  The word to encode.
     * @return      The encoded word.
     */
    public BigInteger encode(BigInteger word) {

        int length = (int)Math.pow(2, r);
        BigInteger code = new BigInteger("0");

        for (int i = 0; i < length; i++) {
            // Conversion of i in binary.
            String iBinary = Integer.toBinaryString(i);
            // Fill in with 0 if missing (0 at the end).
            while (iBinary.length() < r) {
                iBinary = "0" + iBinary;
            }
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
     * Decode an encoded word.
     *
     * We check the first bit (at the left) of the coded word.
     * If this is a "1", we need to inverse it before running the algorithm.
     *
     * Then, for each power of two with the power smaller than r, we get the value of the position,
     * and affect if to the word.
     *
     * @param code  The encoded word.
     * @return      The decoded word.
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

    public static void main(String[] args) {
        int r = 3;
        ReedMuller rm = new ReedMuller(r);

        // Encoding
        BigInteger word = new BigInteger("13");
        BigInteger code = rm.encode(word);

        System.out.println("Word : " + word.toString(2) + " Code : " + code.toString(2));

        // Decoding
        //code = new BigInteger("11");
        word = rm.decode(code);

        System.out.println("Code : " + code.toString(2) + " Word : " + word.toString(2));
    }

}
