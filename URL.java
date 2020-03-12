/**
 * A class containing a hash table of the word and frequency of the word within a URL's web page. "Syntatic glue" words
 * such as "the", "and", "a", etc. are excluded from the hash table. These words are excluded because of their common
 * use and they do not provide evidence on the web page's context.
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class URL implements Serializable, Comparable<URL> {

    private static final String URL_COUNT_KEY = "URL_COUNT";    // Unique key, maps to the number of URLS
    private String url;                                         // The web page's URL
    private BTree bTree;                                        // Hash Table of the Word-Frequency pairs
    private Integer label;                                      // Used for clustering in DBSCAN




    /**
     * Constructor, given a URL, a custom BTree will be created
     * @param url A URL of a web page
     */
    public URL(String url) {
        this.url = url;
        String paragraphText = "";
        bTree = new BTree(url);


        // Retrieves all the paragraph elements from the url and stores them in the
        try {
            Document doc = Jsoup.connect(url).get();
            Elements paragraphs = doc.getElementsByTag("p");
            for (Element paragraph1 : paragraphs) { paragraphText = paragraphText.concat(paragraph1.text()); }
        } catch (IOException e) { e.printStackTrace(); }

        createTree(paragraphText);
        IDF.incrementIDF(URL_COUNT_KEY);
    }




    /**
     * Using the web page's text in String form, creates a HashMap of the words and frequency of appearance's, removing
     * word's that offer little contextual clues. The HashMap is than transposed into the custom BTree
     * @param str The paragraph element's from a web page, in string form.
     */
    public void createTree(String str){
        String [] strArr = str.split("\\P{L}+",2);   // Split the head word from the rest in a string
        String tempStr;                                          // Stores the lower case head word
        Map<String, Integer> map = new HashMap<String, Integer>();             // Key-value pair of a word and the word frequency
        Key tempKey;                                             // Key inserted in tree, and updates IDF HashMap

        // Create a temporary Hash Map of the words and the frequency of their occurrences on a web page
        while(strArr.length > 1){
            tempStr = strArr[0].toLowerCase();
            Integer freq = map.get(tempStr);
            map.put(tempStr, (freq == null) ? 1 : freq + 1);
            strArr = strArr[1].split("\\P{L}+",2);
        }
        tempStr = strArr[0].toLowerCase();
        Integer freq = map.get(tempStr);
        map.put(tempStr,(freq == null) ? 1 : freq + 1);

        Map<String, Integer> filteredMap = filterWords(map);

        // From the given HashMap add them to the custom B-Tree
        for(Map.Entry<String, Integer> e : filteredMap.entrySet()) {
            tempKey = new Key(e.getKey(),e.getValue());
            bTree.keyInsert(tempKey);
            IDF.incrementIDF(tempKey.getWord());
        }
    }




    /**
     * Retrieve tree associated with this URL
     * @return MyHashTable object for this URL
     */
    public BTree getTree(){ return bTree; }




    /**
     * retrieve url address associated with this URL object
     * @return The URL address
     */
    public String getUrl(){ return url; }




    /**
     * Retrieve label status
     * @return current label status
     */
    public Integer getLabel(){ return label; }




    /**
     * Set label status
     * @param label update's label status
     */
    public void setLabel(Integer label){ this.label = label; }




    /**
     * Removes 40 of the most common words that offer no contextual clues
     * @param hashMap the previous non-filtered hashMap
     * @return returns the filtered hashMap
     */
    public static HashMap<String, Integer> filterWords(Map<String, Integer> hashMap){
        hashMap.remove("the");
        hashMap.remove("");
        hashMap.remove("to");
        hashMap.remove("of");
        hashMap.remove("and");
        hashMap.remove("a");
        hashMap.remove("in");
        hashMap.remove("is");
        hashMap.remove("it");
        hashMap.remove("you");
        hashMap.remove("that");
        hashMap.remove("an");
        hashMap.remove("was");
        hashMap.remove("for");
        hashMap.remove("on");
        hashMap.remove("are");
        hashMap.remove("with");
        hashMap.remove("as");
        hashMap.remove("his");
        hashMap.remove("i");
        hashMap.remove("they");
        hashMap.remove("be");
        hashMap.remove("at");
        hashMap.remove("have");
        hashMap.remove("this");
        hashMap.remove("from");
        hashMap.remove("or");
        hashMap.remove("had");
        hashMap.remove("by");
        hashMap.remove("but");
        hashMap.remove("some");
        hashMap.remove("what");
        hashMap.remove("there");
        hashMap.remove("we");
        hashMap.remove("can");
        hashMap.remove("were");
        hashMap.remove("all");
        hashMap.remove("your");
        hashMap.remove("when");
        hashMap.remove("use");
        hashMap.remove("how");

        HashMap<String, Integer> copy = new HashMap<String, Integer>(hashMap);
        return copy;
    }




    /**
     * Generate a numerical representation of the relationship of two web pages, the larger the long the more related
     * two web pages are.
     * @param url1 Web page compared
     * @param url2 Web page compared
     * @return Numerical representation of web page's relation
     */
    public static double generateURLSimilarityVal(URL url1, URL url2){
        if(url1.bTree.getTotalKeyCount() < url2.bTree.getTotalKeyCount())
            return compareTree(url1.bTree.getRoot(), url1.bTree, url2.bTree);

        return compareTree(url2.bTree.getRoot(), url2.bTree, url1.bTree);
    }




    /**
     * Recursively determine tf-idf of two trees
     * @param position address of node
     * @param smallerTree BTree total number of key's is <= largerTree
     * @param largerTree BTree total number of key's is >= smallerTree
     * @return Numerical representation of BTree's relation
     */
    public static double compareTree(int position, BTree smallerTree, BTree largerTree){
        BTree_Node node = smallerTree.getNode(position);
        double urlCount = IDF.getURLCount().doubleValue();
        double tfSmall, tfLarge, idf;
        double returnVal = 0;
        int keyCount = node.getKeyCount();
        Key [] keyArray;

        // Base Case: returns the tf-idf of a leaf node
        if(node.getLeafStatus() == 1){
            keyArray = node.getKeys();
            for(int i0 = 0; i0 < keyCount; i0++){
                tfSmall = keyArray[i0].getFreq() / (double)smallerTree.getTotalWordCount();
                tfLarge = largerTree.search(keyArray[i0]) / (double)largerTree.getTotalWordCount();
                idf = urlCount / IDF.wordAppearances(keyArray[i0].getWord());
                idf = Math.log(idf);
                returnVal += tfSmall * tfLarge * idf;
            }
            return returnVal;
        }

        // Recursive Case: returns the summation of the tf-idf of all nodes
        for(int i1 = 0; i1 < keyCount; i1++){
            returnVal += compareTree(node.getChildren()[i1],smallerTree, largerTree);
        }
        //
        keyArray = node.getKeys();
        for(int i2 = 0; i2 < keyCount; i2++){
            tfSmall = keyArray[i2].getFreq() / (double)smallerTree.getTotalWordCount();
            tfLarge = largerTree.search(keyArray[i2]) / (double)largerTree.getTotalWordCount();
            idf = urlCount / IDF.wordAppearances(keyArray[i2].getWord());
            idf = Math.log(idf);
            returnVal += tfSmall * tfLarge * idf;
        }
        return returnVal;
    }




    /**
     * Override's the compareTo method
     * @param other URL to compare against
     * @return an integer relationship between both URL addresses
     */
    @Override
    public int compareTo(URL other){
        return url.compareTo(other.url);
    }
}