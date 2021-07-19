/**
 * CodingTree.java
 * CodingTree.java
 * Author: RM
 */

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.BitSet;

/**
 * Performs the Huffman Coding:
 * 1. Count the frequency of each character in the message
 * 2. Create individual trees for each character and add them into a Priority
 * Queue, sorted with by their frequency. (least Frequent is prioritized)
 * 3. Until there is only 1 Tree in the PriorityQueue, poll() 2 trees and
 * assign them as children of new Tree then put that new Tree into the PQ.
 * 4. After getting 1 Tree, traverse it and produce a Map, left: '1' & right:
 * '0'.
 * 5. After obtaining the map, iterate through the message and replace each
 * character with a corresponding sequence of bits.
 * 6. The sequence of bits is then the compressed message.
 *
 * @version RM
 * @version 10 April 2017
 */
public class CodingTree {

    /**
     * Declares the root node the of full Huffman Tree once all mini-tress
     * have been all combined.
     */
    private bNode myTree;

    /**
     * A structure that maps a character to a string of 0's and 1's for encoding
     */
    private HashMap<Character, String> codes;

    /**
     * A structure that is exactly the opposite of codes.
     * Maps strings of 0's and 1's to a single character. i.e. 00 -> 'A'
     */
    private HashMap<String, Character> myDictionary;

    /**
     * String containing 0's and 1's that represent the encoded message that
     * was initially passed to the Coding Tree upon creation.
     */
    private String bits;

    /**
     * Constructor. G
     *
     * @param message - The String message that is going to be encoded.
     */
    CodingTree(String message) {

        myDictionary = new HashMap<>();

        //Create a hashmap of characters as keys and their frequencies as data.
        HashMap<Character, Integer> charFrequency = getCharFreq(message);

        //Create and insert bonsaiTrees using the hashmap of
        // <characters, frequencies>
        PriorityQueue<bNode> myQ = makeTrees(charFrequency);

        // Using the PriorityQueue, combine all trees and create a single tree.
        myTree = mergeTrees(myQ);

        // Recursively traverse the tree which also writes into the Dictionary,
        // which is <String, Character> Map that gives a character given a 0s/1s
        traverse(myTree, "");

        // Reverse the Dictionary and get <Character, String> to use for
        // encoding.
        codes = reverseMap(myDictionary);

        // Get the sequence of 0s and 1s using the code map and the message
        bits = encode(message, codes);


    }

    /**
     * binary Tree Node class to be used in Huffman Coding.
     *
     *
     */
    private class bNode {

        private Character myChar;
        private int myFreq;
        private bNode leftNode;
        private bNode rightNode;

        bNode (Character theChar, int theFreq) {

            this.myChar = theChar;
            this.myFreq = theFreq;
            this.leftNode = null;
            this.rightNode = null;
        }


        private int getFreq() {return this.myFreq;}

        public String toString() { return String.valueOf(myChar) + ":" + myFreq;}
    }

    // Takes a string and returns a hashmap of unique characters and their frequency.

    /**
     * Receives the string and counts the number each character appears in the
     * string, storing it in a map <Character, Frequency>
     *
     * @param theString - The string to count each character in it.
     * @return Map of Characters to their frequency in a specific string.
     */
    private HashMap<Character, Integer> getCharFreq(String theString) {

        HashMap<Character, Integer> theMap = new HashMap<Character, Integer>();

        Integer theFreq;

        // For each character in the String, check if the character as a key
        // is in the map.
        // If it is, increment its integer.
        // Else, put the character in the map with frequency 1.
        for (char theChar : theString.toCharArray()) {

            theFreq = theMap.get(theChar);
            if (theFreq == null) {

                theMap.put(theChar, 1);
            } else {
                theMap.put(theChar, theFreq+1);
            }

        }
        return theMap;
    }



    /**
     * Make mini-Trees(single Nodes) from a Map that stores each character of
     * a String along with their frequency.
     *
     * @param theMap - Map that stores each character of a String with their
     *               frequency.
     * @return Return a Priority Queue, sorted by least frequency.
     */
    private PriorityQueue<bNode> makeTrees(HashMap<Character, Integer> theMap) {

        PriorityQueue<bNode> theBonsaiTrees = new PriorityQueue<bNode>(100, Comparator.comparing(bNode::getFreq));

        // For each character in the Map, make a new Tree with the
        // character as data and their frequency as the bNode's "weight"
        for(Character ch : theMap.keySet()) {

            theBonsaiTrees.add(new bNode(ch, theMap.get(ch)));

        }
        return theBonsaiTrees;
    }

    /**
     * Merge the mini-Trees into a single Tree by assigning 2 mini-trees as a
     * children of a new Node with empty characters.
     *
     * @param theBonsaiTrees - Queue of mini-trees that are queued by least
     *                       character frequency.
     * @return Return a single Tree- Huffman Tree
     */
    private bNode mergeTrees(PriorityQueue<bNode> theBonsaiTrees) {

        bNode firstNode;
        bNode secondNode;
        bNode newNode;
        int combinedFreq;

        // While there is more than 1 Tree in the Queue, keep assigning children
        while (theBonsaiTrees.size() > 1) {

            firstNode = theBonsaiTrees.poll();
            secondNode = theBonsaiTrees.poll();

            combinedFreq = firstNode.myFreq + secondNode.myFreq;

            newNode = new bNode(null, combinedFreq);
            newNode.leftNode = firstNode;
            newNode.rightNode = secondNode;

            theBonsaiTrees.add(newNode);

        }


        return theBonsaiTrees.poll();
    }

    /**
     * Recursive traversal that does performs an action on a leaf node.
     * Passes itself a '0' when visiting leftNode and '1' if rightNode.
     *
     *
     * @param root
     * @param stringSeq
     */
    public void traverse (bNode root, String stringSeq){

        if(root == null) {return;}
        if (root.leftNode == null && root.rightNode == null) {

            myDictionary.put(stringSeq, root.myChar);

        }

        traverse(root.leftNode, stringSeq+0);

        traverse(root.rightNode, stringSeq+1);
    }

    /**
     * Reverse a String-Character Map to a Character-String Map.
     * String-Character is used for decoding while
     * Character, String maps are used for encoding.
     *
     * @param theMap The map that is <String, Character>
     * @return A new Map that is the opposite of the inserted map.
     */
    public HashMap<Character, String> reverseMap(HashMap <String,Character> theMap) {

        HashMap<Character, String> reversedMap = new HashMap<Character, String>();

        for(String s: theMap.keySet()) {
            reversedMap.put(theMap.get(s), s);

        }
        return reversedMap;
    }

    /**
     * Takes the message to be encoded and the Map that has a sequence of bits
     * for each character then turns the message into a Strubg of 0's and 1's
     * Example: if  A = 0, B = 11 then message "ABB" is encoded as 01111
     *
     * @param theString Message to be encoded
     * @param theMap Map that has a string of bits for each character in the
     *               message.
     * @return A String of 0's and 1's. i.e. "0111"
     */
    public String encode(String theString, HashMap<Character, String> theMap) {

        StringBuilder bitSeq = new StringBuilder();

        // For each character in the String, use that character as a key and
        // get a sequence of bits. Then append that sequence to the String
        for (char theChar : theString.toCharArray()) {

            bitSeq.append(theMap.get(theChar));

        }

        //System.out.println(bitSeq.toString());
        return bitSeq.toString();
    }

    /**
     * Decodes a String of 0's and 1's along with the Map for the codes
     * and decodes the string.
     *
     * @param bits - String of 0's and 1's that is to be decoded.
     * @param codes - A map that has a character for a sequence of bits.
     *
     * @return String message in human readable format.
     */
    public String decode(String bits, HashMap<Character, String> codes) {

        StringBuilder theMessage = new StringBuilder();
        StringBuilder subBitSeq = new StringBuilder();

        HashMap<String, Character> reversedMap = new HashMap<>();

        // Reverse the Map from Character-String to String-Character
        for(Character c: codes.keySet()) {
            reversedMap.put(codes.get(c), c);
        }
        // For each character in the bits String, append it to a sub Sequence
        // If this subSequence is a key in the Map, then use it and get a
        // character. Reset
        for(char c: bits.toCharArray()) {
            subBitSeq.append(c);

            // If the sub sequence is one of the keys, perform this.
            if (reversedMap.containsKey(subBitSeq.toString())) {

                // Use the key and grab a character then append that character
                // to the StringBuilder
                theMessage.append(reversedMap.get(subBitSeq.toString()));

                // Reset the sub sequence
                subBitSeq = new StringBuilder();
            }

        }
        // Return the Message
        return theMessage.toString();
    }

    /**
     * Contribution:
     * https://stackoverflow.com/questions/39680749/string-of-0s-and-1s-to-file-as-bits
     *
     * Turns the bits, a String of 0's and 1's into a byte Array that is needed
     * to write onto a file.
     *
     * @return Byte Array of 0's and 1's
     *
     *
     */
    public byte[] getByte() {

        BitSet bitSet = new BitSet(bits.length());
        int nthBit = 0;

        /* Iterate through each "character" in the bits. If the character is '1'
         * set the n'th bit of the bitSet to 1.
         * Either way, increment nthBit
         *
         * The resulting set is a sequence of bits.
         * Contribution:
         * https://stackoverflow.com/questions/39680749/string-of-0s-and-1s-to-file-as-bits
         */
        for(Character c : bits.toCharArray()) {
            if(c.equals('1')) {
                bitSet.set(nthBit);
            }
            nthBit++;
        }

        // Return a byte
        return bitSet.toByteArray();
    }

    /**
     * toString method to print the Dictionary/Map
     *
     * @return String representation of the Map
     */
    public String codesToString() {return codes.toString();}

    /**
     * Get method for the codes to be used for decoding.
     *
     * @return
     */
    public HashMap<Character, String> getCodes() {return codes;}

    /**
     * Get method for the codes to be used for decoding.
     * @return
     */
    public HashMap<String, Character> getDictionary() {return myDictionary;}

    /**
     * Get method for the String of bits.
     */
    public String getBits() {return bits;}
}


