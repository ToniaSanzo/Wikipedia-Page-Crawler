/**
 * Launches the GUI for the B-Tree web page categorization program
 *
 * @author Tonia Sanzo
 * @version 1.0
 * @since November 2019
 */

import javafx.application.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.IOException;

public class Main extends Application {
    // JavaFX Nodes
    Stage stage;
    Scene defScene, ansScene, clstr1Scene, clstr2Scene;
    Label defLbl, ansLbl, epsLbl, minPtsLbl, clstrRsltLbl;
    TextField defFld, epsFld, minsPtsFld;
    Button defBtn, ansBtn, clstrBtn, clstrCalcBtn;




    /**
     * launches GUI
     * @param args Not used
     */
    public static void main(String [] args) { launch(args); }




    /**
     * Create and display the root scene
     * @param primaryStage a Stage object
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException{
        stage = primaryStage;

        // Initialize scene specification
        defLbl = new Label("Enter a URL:");
        defFld = new TextField();
        defFld.setMinWidth(200);
        defFld.setMaxWidth(200);
        defBtn = new Button("compare");
        defBtn.setOnAction(e -> exeCompare());
        clstrBtn = new Button("clusters");
        clstrBtn.setOnAction(e -> exeCluster());
        VBox panel1 = new VBox(20);
        HBox panel2 = new HBox(20);
        panel2.getChildren().addAll(defBtn,clstrBtn);
        panel2.setAlignment(Pos.CENTER);
        panel1.getChildren().addAll(defLbl,defFld,panel2);
        panel1.setAlignment(Pos.CENTER);
        defScene = new Scene(panel1,750,500);

        primaryStage.setScene(defScene);
        primaryStage.setTitle("Web Page Comparer");
        primaryStage.show();
    }




    /**
     * Determine and display the most similar web page
     */
    public void exeCompare(){
        String URLAddr = defFld.getText();

        if(URLAddr.length() == 0)
            return;

        URL tmpURL = mostSimilarWebPage(URLAddr);

        // Initialize answer scene
        ansLbl = new Label("The most similar to " + URLAddr + " is " + tmpURL.getUrl());
        ansBtn = new Button("Home");
        ansBtn.setOnAction(e -> restart());
        VBox panel2 = new VBox(20);
        panel2.getChildren().addAll(ansLbl,ansBtn);
        panel2.setAlignment(Pos.CENTER);
        ansScene = new Scene(panel2, 750, 500);

        stage.setScene(ansScene);
        stage.setTitle("Success");
        stage.show();
    }




    /**
     * User specifies DBSCAN parameters
     */
    public void exeCluster(){
        // Initialize cluster scene
        epsLbl = new Label("Similarity Radius");
        epsFld = new TextField(".00077");
        minPtsLbl = new Label("Minimum Points");
        minsPtsFld = new TextField("4");
        ansBtn = new Button("Home");
        ansBtn.setOnAction(e -> restart());
        clstrCalcBtn = new Button("Generate Clusters");
        clstrCalcBtn.setOnAction(e -> clusterGeneration());
        HBox panel1 = new HBox(20);
        panel1.setAlignment(Pos.CENTER);
        panel1.getChildren().addAll(epsLbl, minPtsLbl);
        HBox panel2 = new HBox(20);
        panel2.setAlignment(Pos.CENTER);
        panel2.getChildren().addAll(epsFld, minsPtsFld);
        HBox panel3 = new HBox(20);
        panel3.setAlignment(Pos.CENTER);
        panel3.getChildren().addAll(clstrCalcBtn, ansBtn);
        VBox panel4 = new VBox(20);
        panel4.getChildren().addAll(panel1, panel2, panel3);
        panel4.setAlignment(Pos.CENTER);
        clstr1Scene = new Scene(panel4, 750, 500);

        stage.setScene(clstr1Scene);
        stage.setTitle("Cluster");
        stage.show();
    }




    /**
     * Display Web page clusters
     */
    public void clusterGeneration(){
        // Load URL objects
        URL [] urlArray = SaveLoad.getURLS(104);
        IDF.loadIDF();

        // Parse DBSCAN parameters specified in previous web page
        String epsStr = epsFld.getText();
        String minPtsStr = minsPtsFld.getText();
        double eps = Double.parseDouble(epsStr);
        int minPts = Integer.parseInt(minPtsStr);

        // execute DBSCAN algorithm
        DBSCAN.dbScan(urlArray, eps, minPts);
        String clusterData = "Cluster Data\n\n\n";
        int j = 0, i = 0;
        // Organize URL cluster's
        while(i < 104){
            for(int i0 = 0; i0 < 104; i0++) {
                if (j == 0 && urlArray[i0].getLabel() == j) {
                    clusterData = clusterData.concat("Noise: " + urlArray[i0].getUrl() + "\n");
                    i++;
                } else if (urlArray[i0].getLabel() == j){
                    clusterData = clusterData.concat("Cluster[" + j + "]: " + urlArray[i0].getUrl() + "\n");
                    i++;
                }
            }
            clusterData = clusterData.concat("\n\n");
            j++;
        }

        // Initialize cluster result scene
        clstrRsltLbl = new Label(clusterData);
        ansBtn = new Button("Home");
        ansBtn.setOnAction(e -> restart());
        clstrBtn = new Button("Back");
        clstrBtn.setOnAction(e -> exeCluster());
        HBox panel1 = new HBox(20);
        panel1.getChildren().addAll(clstrBtn, ansBtn);
        panel1.setAlignment(Pos.CENTER);
        VBox panel2 = new VBox(20);
        panel2.getChildren().addAll(clstrRsltLbl);
        panel2.setAlignment(Pos.CENTER);
        ScrollPane panel3 = new ScrollPane();
        panel3.setPrefSize(750,750);
        panel3.setContent(panel2);
        VBox panel4 = new VBox(20);
        panel4.getChildren().addAll(panel3 ,panel1);
        panel4.setAlignment(Pos.CENTER);
        clstr2Scene = new Scene(panel4, 750, 500);

        stage.setScene(clstr2Scene);
        stage.setTitle("Cluster Data");
        stage.show();
    }




    /**
     * Set scene to root scene
     */
    public void restart(){
        stage.setScene(defScene);
        stage.setTitle("Web Page Comparer");
        stage.show();
    }




    /**
     * Determines the most similar web page
     * @param url A string that corresponds to a valid URL address
     * @return returns the url that is most similar to the parameter url address
     */
    public URL mostSimilarWebPage(String url){
        double similarityVal, biggestVal = 0;
        URL mostSimilarURL = null;

        URL [] urlArray = SaveLoad.getURLS(104);
        IDF.loadIDF();

        URL url1 = new URL(url);

        for(int i = 0; i < 104; i++){
            similarityVal = URL.generateURLSimilarityVal(urlArray[i], url1);
            System.out.println("Similarity Value between " + url + " and " + urlArray[i].getUrl() + "is " + similarityVal);

            if(similarityVal >= biggestVal) {
                mostSimilarURL = urlArray[i];
                biggestVal = similarityVal;
            }
        }

        System.out.println("The most similar to " + url + " is : " + mostSimilarURL.getUrl());
        return mostSimilarURL;
    }




    /**
     * Generate and save 104 URL objects
     */
    public static void reloadLocalFiles(){
        URL [] url = new URL[130];

        url[0] = new URL("https://en.wikipedia.org/wiki/Yellowstone_National_Park");
        url[1] = new URL("https://en.wikipedia.org/wiki/Adirondack_Mountains");
        url[2] = new URL("https://en.wikipedia.org/wiki/Everglades_National_Park");
        url[3] = new URL("https://en.wikipedia.org/wiki/Yosemite_National_Park");
        url[4] = new URL("https://en.wikipedia.org/wiki/Grand_Canyon");
        url[5] = new URL("https://en.wikipedia.org/wiki/Rocky_Mountains");
        url[6] = new URL("https://en.wikipedia.org/wiki/Serengeti_National_Park");
        url[7] = new URL("https://en.wikipedia.org/wiki/Sequoia_National_Park");
        url[8] = new URL("https://en.wikipedia.org/wiki/Amazon_rainforest");
        url[9] = new URL("https://en.wikipedia.org/wiki/Gal%C3%A1pagos_National_Park");
        url[10] = new URL("https://en.wikipedia.org/wiki/Kruger_National_Park");
        url[11] = new URL("https://en.wikipedia.org/wiki/Zion_National_Park");
        url[12] = new URL("https://en.wikipedia.org/wiki/Glacier_National_Park_(U.S.)");
        url[13] = new URL("https://en.wikipedia.org/wiki/Glacier_National_Park_(Canada)");
        url[14] = new URL("https://en.wikipedia.org/wiki/Spaghetti");
        url[15] = new URL("https://en.wikipedia.org/wiki/Pizza");
        url[16] = new URL("https://en.wikipedia.org/wiki/Cheeseburger");
        url[17] = new URL("https://en.wikipedia.org/wiki/Pineapple");
        url[18] = new URL("https://en.wikipedia.org/wiki/Kiwifruit");
        url[19] = new URL("https://en.wikipedia.org/wiki/Burrito");
        url[20] = new URL("https://en.wikipedia.org/wiki/Ramen");
        url[21] = new URL("https://en.wikipedia.org/wiki/Chinese_cuisine");
        url[22] = new URL("https://en.wikipedia.org/wiki/American_Chinese_cuisine");
        url[23] = new URL("https://en.wikipedia.org/wiki/Rice");
        url[24] = new URL("https://en.wikipedia.org/wiki/Raspberries");
        url[25] = new URL("https://en.wikipedia.org/wiki/Lebanese_cuisine");
        url[26] = new URL("https://en.wikipedia.org/wiki/Deviled_egg");
        url[27] = new URL("https://en.wikipedia.org/wiki/Pickled_cucumber");
        url[28] = new URL("https://en.wikipedia.org/wiki/Cannoli");
        url[29] = new URL("https://en.wikipedia.org/wiki/Cars");
        url[30] = new URL("https://en.wikipedia.org/wiki/Porsche");
        url[31] = new URL("https://en.wikipedia.org/wiki/Nissan");
        url[32] = new URL("https://en.wikipedia.org/wiki/NASCAR");
        url[33] = new URL("https://en.wikipedia.org/wiki/Lamborghini");
        url[34] = new URL("https://en.wikipedia.org/wiki/Chevrolet_Impala");
        url[35] = new URL("https://en.wikipedia.org/wiki/Pontiac_Firebird");
        url[36] = new URL("https://en.wikipedia.org/wiki/Rallying");
        url[37] = new URL("https://en.wikipedia.org/wiki/Skateboarding");
        url[38] = new URL("https://en.wikipedia.org/wiki/Rodney_Mullen");
        url[39] = new URL("https://en.wikipedia.org/wiki/Left_4_Dead_2");
        url[40] = new URL("https://en.wikipedia.org/wiki/The_Berrics");
        url[41] = new URL("https://en.wikipedia.org/wiki/Let%C3%ADcia_Bufoni");
        url[42] = new URL("https://en.wikipedia.org/wiki/Kickflip");
        url[43] = new URL("https://en.wikipedia.org/wiki/360_Kickflip");
        url[44] = new URL("https://en.wikipedia.org/wiki/Chris_Cole_(skateboarder)");
        url[45] = new URL("https://en.wikipedia.org/wiki/Heelflip");
        url[46] = new URL("https://en.wikipedia.org/wiki/Woodward_Camp");
        url[47] = new URL("https://en.wikipedia.org/wiki/Half-pipe");
        url[48] = new URL("https://en.wikipedia.org/wiki/Mega_Ramp");
        url[49] = new URL("https://en.wikipedia.org/wiki/FDR_Skatepark");
        url[50] = new URL("https://en.wikipedia.org/wiki/Burnside_Skatepark");
        url[51] = new URL("https://en.wikipedia.org/wiki/Coleman_Playground#Coleman_Playground_Skatepark");
        url[52] = new URL("https://en.wikipedia.org/wiki/Primitive_Skateboarding");
        url[53] = new URL("https://en.wikipedia.org/wiki/Dwindle_Distribution");
        url[54] = new URL("https://en.wikipedia.org/wiki/Vans");
        url[55] = new URL("https://en.wikipedia.org/wiki/Fox");
        url[56] = new URL("https://en.wikipedia.org/wiki/Squirrel");
        url[57] = new URL("https://en.wikipedia.org/wiki/Otter");
        url[58] = new URL("https://en.wikipedia.org/wiki/Bluebird");
        url[59] = new URL("https://en.wikipedia.org/wiki/House_sparrow");
        url[60] = new URL("https://en.wikipedia.org/wiki/Deer");
        url[61] = new URL("https://en.wikipedia.org/wiki/Bear");
        url[62] = new URL("https://en.wikipedia.org/wiki/Wolf");
        url[63] = new URL("https://en.wikipedia.org/wiki/Sheep");
        url[64] = new URL("https://en.wikipedia.org/wiki/Dog");
        url[65] = new URL("https://en.wikipedia.org/wiki/Cat");
        url[66] = new URL("https://en.wikipedia.org/wiki/Wildebeest");
        url[67] = new URL("https://en.wikipedia.org/wiki/Wolverine");
        url[68] = new URL("https://en.wikipedia.org/wiki/Cattle");
        url[69] = new URL("https://en.wikipedia.org/wiki/Human");
        url[70] = new URL("https://en.wikipedia.org/wiki/Polar_bear");
        url[71] = new URL("https://en.wikipedia.org/wiki/Bumblebee");
        url[72] = new URL("https://en.wikipedia.org/wiki/Killer_whale");
        url[73] = new URL("https://en.wikipedia.org/wiki/Dolphin");
        url[74] = new URL("https://en.wikipedia.org/wiki/League_of_Legends");
        url[75] = new URL("https://en.wikipedia.org/wiki/Skate_3");
        url[76] = new URL("https://en.wikipedia.org/wiki/Grand_Theft_Auto_V");
        url[77] = new URL("https://en.wikipedia.org/wiki/Civilization_VI");
        url[78] = new URL("https://en.wikipedia.org/wiki/Reader_Rabbit");
        url[79] = new URL("https://en.wikipedia.org/wiki/The_Legend_of_Zelda");
        url[80] = new URL("https://en.wikipedia.org/wiki/Mario_Party");
        url[81] = new URL("https://en.wikipedia.org/wiki/JumpStart");
        url[82] = new URL("https://en.wikipedia.org/wiki/Warcraft_III:_Reign_of_Chaos");
        url[83] = new URL("https://en.wikipedia.org/wiki/StarCraft_II:_Wings_of_Liberty");
        url[84] = new URL("https://en.wikipedia.org/wiki/Fuzion_Frenzy");
        url[85] = new URL("https://en.wikipedia.org/wiki/Medieval:_Total_War");
        url[86] = new URL("https://en.wikipedia.org/wiki/The_Sims");
        url[87] = new URL("https://en.wikipedia.org/wiki/Harry_Potter_and_the_Philosopher%27s_Stone");
        url[88] = new URL("https://en.wikipedia.org/wiki/The_Lion_King");
        url[89] = new URL("https://en.wikipedia.org/wiki/Chappie_(film)");
        url[90] = new URL("https://en.wikipedia.org/wiki/A_Clockwork_Orange_(film)");
        url[91] = new URL("https://en.wikipedia.org/wiki/Brown_bear");
        url[92] = new URL("https://en.wikipedia.org/wiki/American_black_bear");
        url[93] = new URL("https://en.wikipedia.org/wiki/Giant_panda");
        url[94] = new URL("https://en.wikipedia.org/wiki/Grizzly_bear");
        url[95] = new URL("https://en.wikipedia.org/wiki/Kodiak_bear");
        url[96] = new URL("https://en.wikipedia.org/wiki/Sloth_bear");
        url[97] = new URL("https://en.wikipedia.org/wiki/Asian_black_bear");
        url[98] = new URL("https://en.wikipedia.org/wiki/Sun_bear");
        url[99] = new URL("https://en.wikipedia.org/wiki/Spectacled_bear");
        url[100] = new URL("https://en.wikipedia.org/wiki/The_Little_Mermaid_(1989_film)");
        url[101] = new URL("https://en.wikipedia.org/wiki/WALL-E");
        url[102] = new URL("https://en.wikipedia.org/wiki/Toy_Story");
        url[103] = new URL("https://en.wikipedia.org/wiki/Requiem_for_a_Dream");

        for(int i = 0; i < 104; i++){
            SaveLoad.saveURL(url[i], i);
        }
        SaveLoad.saveIDF();
    }
}
