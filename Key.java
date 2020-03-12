/**
 * Word-frequency pairs purposed in constructing constant sized key's. Contains a custom char array of predetermined
 * size, and an int.
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 */

import java.nio.ByteBuffer;

public class Key {
    private static final int STRING_SIZE = 14;
    private static final int KEY_SIZE = 32;
    private static final char DELIMETER = '!';

    private char [] string;
    private String word;
    private int freq;




    /**
     * Construct a word-frequency pair object of constant size
     * @param word String word, word.length > 12 will be truncated to 12 characters
     * @param freq Number of word occurrences
     */
    public Key(String word, int freq){
        this.freq = freq;
        string = new char[STRING_SIZE];
        char [] cString = word.toCharArray();
        int i = 0;
        while( i < cString.length && i < STRING_SIZE - 1){
             string[i] = cString[i];
             i++;
        }

        while(i < STRING_SIZE) {
            string[i] = DELIMETER;
            i++;
        }



        if(word.length() > 13){
           this.word = word.substring(0,13);
        } else {
            this.word = word;
        }
    }




    /**
     * Byte array of a Key
     * @return Key represented as a 32-Byte array
     */
    public byte [] array(){

        ByteBuffer bb = ByteBuffer.allocate(KEY_SIZE);

        // Put string into the ByteBuffer
        int i = 0;
        while(string[i] != DELIMETER){
            bb.putChar(string[i]);
            i++;
        }
        bb.putChar(string[i]);

        // Put freq into the ByteBuffer
        bb.putInt(28,freq);

        return bb.array();
    }




    /**
     * Convert a byte array into a Key object
     * @param buffer A byte array representation of a Key
     * @return returns a Key object, if invalid byte array returns null
     */
    public static Key key(byte [] buffer){
        char [] tempString = new char[STRING_SIZE];
        String tempWord;
        int tempFreq, i = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put(buffer);

        // Determine byte array validity
        tempString[STRING_SIZE - 1] = byteBuffer.getChar(i);
        if(tempString[STRING_SIZE - 1] == '.' || tempString[STRING_SIZE - 1] == '!') return null;

        // Determine word
        do {
            tempString[i] = tempString[STRING_SIZE - 1];
            i++;
            tempString[STRING_SIZE - 1] = byteBuffer.getChar(i << 1);
        } while(tempString[STRING_SIZE - 1] != '!' && i < STRING_SIZE);
        tempWord = String.copyValueOf(tempString,0,i);

        // Determine frequency
        tempFreq = byteBuffer.getInt(28);

        byteBuffer.clear();
        return new Key(tempWord,tempFreq);
    }




    /**
     * Convert Key object into a String
     * @return String representation of Key object
     */
    public String toString(){
        return word + " " + freq;
    }




    /**
     * Get word associated with Key object
     * @return word associated with Key object
     */
    public String getWord(){ return word; }




    /**
     * Get freq associated with Key object
     * @return freq associated with Key object
     */
    public int getFreq(){ return freq; }
}