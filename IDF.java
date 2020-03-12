/**
 * Inverse Document Frequency, develops word weights depending on the number of unique pages the word appeared in
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class IDF implements Serializable {
    private static final String URL_COUNT_KEY = "URL_COUNT";
    static Map<String, Integer> classMap = new HashMap<>();




    /**
     * The class HashMap becomes a copy of the parameter
     */
    public static void loadIDF() {
        classMap = new HashMap<>(SaveLoad.loadIDF());
    }




    /**
     * Increments the IDF(Inverse Document Frequency) of the IDF class HashMap
     * @param word The word that needs to increment
     */
    public static void incrementIDF(String word){
        Integer freq = classMap.get(word);
        classMap.put(word, (freq == null)?1: freq + 1);
    }




    /**
     * Get the current class's HashMap
     * @return Class's HashMap
     */
    public static Map<String, Integer> getMap(){
        return classMap;
    }




    /**
     * Returns the number of times this word appeared throughout the corpus of web pages
     * @param word Word different web page appearances
     * @return The number of different times the word appeared in the corpus of web pages, or null if the word never
     *         appeared.
     */
    public static Integer wordAppearances(String word) {
        return classMap.get(word);
    }




    /**
     * Get the total number of URL objects
     * @return Number of URL objects so far
     */
    public static Integer getURLCount() { return classMap.get(URL_COUNT_KEY); }
}