/**
 * DBSCAN clustering algorithm
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since October 2019
 * @citation DBSCAN data clustering algoritm developed by Martin Ester, Hans-Peter Kriegel, Jörg Sander and Xiaowei Xu
 */

import java.util.ArrayList;
import java.util.TreeSet;

public class DBSCAN {
    private static final int NOISE = 0; // Value used to represent noise




    /**
     * Using a URL database generates the cluster using DBSCAN
     * @param urlArray URL database
     * @param eps Value compared against similarity metric
     * @param minPts URLS within radius necessary to join cluster
     */
    public static void dbScan(URL [] urlArray, double eps, int minPts){
        int clusterCounter = 0;
        TreeSet<URL> tempSet = new TreeSet<URL>();
        ArrayList<URL> tempList;
        ArrayList<URL> neighbors;

        // Goes through and label's each element
        for(URL url: urlArray){
            if(url.getLabel() != null) continue;
            neighbors = rangeQuery(urlArray, url, eps);
            if(neighbors.size() < minPts){
                url.setLabel(NOISE);
                continue;
            }
            clusterCounter++;
            url.setLabel(clusterCounter);
            tempSet.addAll(neighbors);
            tempList = new ArrayList<URL>(tempSet);
            for(int i0 = 0; i0 < tempList.size(); i0++){
                if(tempList.get(i0).getLabel() != null && tempList.get(i0).getLabel() == NOISE) {
                    tempList.get(i0).setLabel(clusterCounter);
                }
                if(tempList.get(i0).getLabel() != null) continue;
                tempList.get(i0).setLabel(clusterCounter);
                neighbors = rangeQuery(urlArray,tempList.get(i0),eps);
                if(neighbors.size() >= minPts){
                    neighbors = filterList(tempList,neighbors);
                    tempList.addAll(neighbors);
                }
            }
        }
    }




    /**
     * Generate list of URL's in eps range
     * @param urlArray URL database
     * @param url URL compared to URL database
     * @param eps Value deciding if a similarity metric is in range of url
     * @return Return list of URL's in eps range
     */
    public static ArrayList<URL> rangeQuery(URL [] urlArray, URL url, double eps){
        ArrayList<URL> neighbors = new ArrayList<URL>();

        // URL's in the range of eps, are put in the neighbors
        for(URL tempURL: urlArray){
            if(URL.generateURLSimilarityVal(tempURL,url) >= eps)
                neighbors.add(tempURL);
        }
        return neighbors;
    }




    /**
     * Removes elements in list2, that are in list1
     * @param list1  list used to know which elements to filter
     * @param list2 list that is filtered
     * @return a filtered list
     */
    public static ArrayList<URL> filterList(ArrayList<URL> list1, ArrayList<URL> list2){
        for(int i1 = 0; i1 < list1.size(); i1++){
            for(int i2 = 0; i2 < list2.size(); i2++){
                if(list1.get(i1).getUrl().equals(list2.get(i2).getUrl()))
                    list2.remove(i2);
            }
        }
        return list2;
    }
}