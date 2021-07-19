/**
 *
 * Main.java
 * Author: RM
 *
 */
import java.io.*;
import java.io.PrintWriter;
import java.util.BitSet;

/**
 *
 */
public class Main {

    public static void main(String[] args) {

        DataInputStream input = null;
        String inFileName = "WarAndPeace.txt";
        //Sample txt(replace WarAndPeace.txt):
        //String inFileName = "MobyDick.txt"
        String outFileName = "compressed.bin";
        String outFileName1 = "codes.txt";

        try{
            // Contribution: https://www.mkyong.com/java/how-to-assign-file-content-into-a-variable-in-java/
            input = new DataInputStream (
                            new FileInputStream (inFileName));
            byte[] datainBytes = new byte[input.available()];
            input.readFully(datainBytes);
            String content = new String(datainBytes, 0, datainBytes.length);
            // End Contribution

            // Open Files
            OutputStream outputStream = new FileOutputStream(outFileName);
            PrintWriter printWriter = new PrintWriter(outFileName1);

            // Start time count.
            long startTime = System.currentTimeMillis();

            // Create an instance of a CodingTree, passing a String of Text
            CodingTree huffmanCoding = new CodingTree(content);

            // Write to 2 files: compressed.bin and codes.txt respectively.
            outputStream.write(huffmanCoding.getByte());
            printWriter.write(huffmanCoding.codesToString());

            // End time count.
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            float uncompSize = new File(inFileName).length();
            float compedSize = new File(outFileName).length();
            System.out.println("File Size: " + uncompSize/1024 + " KB");
            System.out.println("Compressed File Size: " + compedSize/1024 + " KB");
            System.out.println("Compression Ratio: " + (compedSize/uncompSize)*100 + "%");

            System.out.println("Time(No Decode): " + totalTime + " ms");



            printWriter.close();
            outputStream.close();

            // Open the compressed File and run decodeFile() with it.
            String bitSequence = decodeFile(new FileInputStream(outFileName));
            // Decode the bitSequence(0's & 1's) into the message.
            String decodedString = huffmanCoding.decode(bitSequence, huffmanCoding.getCodes());

            long decodeTotalTime = System.currentTimeMillis() - startTime;
            System.out.println("Time(with Decode) " + decodeTotalTime + " ms");

            //System.out.println(decodedString);

            //Decomment below to test CodingTree class.
            //testCodingTree();

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Takes a compressed File and decompresses it into a String of 0's and 1's
     *
     * Uses Java 9 method bitSet.readAllBytes()
     * @param content - Input that has the compressed binary file
     * @return Return a String of bits i.e. 0's and 1's
     */
    public static String decodeFile(FileInputStream content) {

        // Declare variables
        BitSet bitSet = null;
        byte[] fileContent = null;
        StringBuilder theString = new StringBuilder();

        //Try to open and read the file.
        try {

            fileContent = content.readAllBytes();
            bitSet = BitSet.valueOf(fileContent);

        }catch(Exception e) {

            System.out.println(e);

        }
        /**
         *  Contribution:
         * https://stackoverflow.com/questions/39680749/string-of-0s-and-1s-to-file-as-bits
         * Iterate through each bit in the bitSet. If the i'th bit is a 1,
         * append a '1' to the StringBuilder, if not then append '0'.
         */
        for(int i = 0; i <= bitSet.length(); i++) {
            if(bitSet.get(i)) {
                theString.append('1');
            } else {
                theString.append('0');
            }
        }

        return theString.toString();
    }


    public static void testCodingTree() {

        CodingTree test = new CodingTree("ANNA HAS A BANANA IN A BANDANA");

        System.out.println("Message: " + "ANNA HAS A BANANA IN A BANDANA");
        System.out.println("bits: " + test.getBits());
        System.out.println("codes: " + test.getCodes().toString());
        System.out.println("Decoded: " + test.decode(test.getBits(), test.getCodes()));


    }
}
