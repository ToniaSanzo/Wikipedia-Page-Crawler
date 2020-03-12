/**
 * Save/Load a serialized URL object in a local folder
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 */

import java.io.*;
import java.util.Map;

public class SaveLoad {
    final static String IDF_PATH = System.getProperty("user.dir").concat("/src/IDF/hashmap");  // Path to the IDF folder
    final static String URL_PATH = System.getProperty("user.dir").concat("/src/URLS/"); // Path to the URLS folder



    /**
     * Serializes and saves the URL object in the URLS directory
     * @param urlObject the URL object you'd like to store in a local file
     * @param index Used as the URL object's file name, each URL should be saved with a unique index
     */
    public static void saveURL(Object urlObject, Integer index) {
        String fileName = URL_PATH.concat(index.toString());

        // Serialization
        try {
            //Saving of object in a file
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(urlObject);

            out.close();
            file.close();

            System.out.println("Object has been serialized");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    /**
     * Reads a specified number of URLS stored in memory
     * @param numbURL How many URLs are saved in the URL folder
     * @return returns an array of URL objects
     */
    public static URL[] getURLS(int numbURL){
        URL [] urlArr = new URL[numbURL];

        // Deserialize a URL object, and add it to the URL array
        for(Integer i = 0; i < numbURL; i++) {
            try {
                FileInputStream file = new FileInputStream(URL_PATH.concat(i.toString()));
                ObjectInputStream in = new ObjectInputStream(file);

                urlArr[i] = (URL)in.readObject();

                in.close();
                file.close();

            } catch(IOException ex) {
                ex.printStackTrace();
            } catch(ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        return urlArr;
    }




    /**
     * Save the current IDF's HashMap
     */
    public static void saveIDF(){
        Map<String, Integer> map = IDF.getMap();

        try{
            FileOutputStream file = new FileOutputStream(IDF_PATH);
            ObjectOutputStream out = new ObjectOutputStream(file);

            out.writeObject(map);

            out.close();
            file.close();

        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }




    /**
     * Loads the last IDF HashMap that was saved
     * @return The HashMap that is loaded from the IDF folder
     */
    public static Map<String, Integer> loadIDF(){
        Map<String, Integer> tempMap = null;

        try{
            FileInputStream file = new FileInputStream(IDF_PATH);
            ObjectInputStream in = new ObjectInputStream(file);

            tempMap = (Map<String, Integer>)in.readObject();

            in.close();
            file.close();

        } catch(IOException ex) {
            ex.printStackTrace();
        } catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return tempMap;
    }
}