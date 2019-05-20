 package GUI;

import Algorithm.Maze;

import Parser.Macro;
import Parser.Parser;
import Parser.Layer;
import Parser.Rect;
import Parser.Vector;
import Parser.Net;
import Parser.Track;
import Placement.Placer;

import Routing.Router;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

import java.util.*;


 /**
 *  The Main GUI Class, it waits until the Algorihtm's Main finishes execution and the proceeds with its own execution
 */
public class Main extends Application {
    public static Controller controller;
    private static GridPane gridContainer;
    public static Scanner scanner;
    private Stage stage;

    public static int[] dimensions;
    public static boolean firstTimeInDetailedRouting = true;
    public static boolean globalRouting = true;
    
    public static boolean exit = false;

    public static Maze maze;
    private static Placer placer;

    @Override
    public void init() throws Exception {
       
        Parser parser = new Parser(); //Create Parser of DEF & LEF
        Hashtable<String, Layer> layersTable = parser.getLayersTable();
        Hashtable<String, Macro> placedMacros = parser.getPlacedMacros();
        Hashtable<String, Macro> definedMacros = parser.getMacrosDefinitions(layersTable);
        Rect dieArea = parser.getDieArea(); 
        Vector coreSite = parser.getCoreSite();
        HashSet<Net> netsSet = parser.getNets() ;
        HashSet<Net> specialnetsSet = parser.getSpecialNets () ;
        Hashtable <Integer , Track> tracks =  parser.getTracks();

        placer = new Placer(tracks, dieArea, coreSite , placedMacros,  definedMacros, layersTable);
        List<Vector> obsLocations = placer.addObsInGrid();
        Hashtable<Net.Item, Vector> pinLocationsInGrid = placer.addPinsInGrid();

        // Initialization
        int[][][] maze = new int[Router.xGridSize][Router.yGridSize][placer.getzSize()];   //130 * 65 * 5 
        for (int i = 0; i < Router.xGridSize; i++) {
            for (int j = 0; j < Router.yGridSize; j++) {
                for (int k = 1; k < placer.getzSize(); k++) { // 1--> 4
                    maze[i][j][k] = 0;
                }
            }
        }
        
        Router router = new Router(netsSet, placedMacros, definedMacros, pinLocationsInGrid, tracks, obsLocations);

        dimensions = new int[]{placer.getxSize(), placer.getySize(), placer.getzSize()}; //130 * 65 * 5
        controller = new Controller();
        controller.setMaze(maze, dimensions[0], dimensions[1], dimensions[2]);

        gridContainer = new GridPane();
        firstTimeInDetailedRouting = true;

        router.printGbox();
        super.init();
    }

    @Override
    public void start(Stage primaryStage){
        //Parent root = FXMLLoader.load(getClass().getResource("GUI/sample.fxml"));
        this.stage = primaryStage;
        primaryStage.setTitle("A* Routing");
        //Algorithm.Main.main(controller);   // Take inputs and Run the A* Algorithm
        int[][][] grids = placer.draw();
        primaryStage.setScene(new Scene(gridContainer, 2048, 1024));
        primaryStage.show();
    }

   


    public static void main(String[] args) {
        launch(args);
    }
}
