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
       
        Parser parser = new Parser();
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
        int[][][] maze = new int[Router.xGridSize][Router.yGridSize][placer.getzSize()];    // TODO:: Make it placer.size when detailed routing
        for (int i = 0; i < Router.xGridSize; i++) {
            for (int j = 0; j < Router.yGridSize; j++) {
                for (int k = 1; k < placer.getzSize(); k++) {
                    maze[i][j][k] = 0;
                }
            }
        }
        
        Router router = new Router(netsSet, placedMacros, definedMacros, pinLocationsInGrid, tracks, obsLocations);

        dimensions = new int[]{placer.getxSize(), placer.getySize(), placer.getzSize()};
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

        /*for (int k = 1; k < placer.getzSize(); k++) {
            for (int j = 0; j < placer.getySize(); j++) {
                for (int i = 0; i < placer.getxSize(); i++) {
                    System.out.print(grids[i][j][k]);
                }
                System.out.println("");
            }
            System.out.println("Metal " + (k));
        }*/


        //gridContainer = updateUI();

        primaryStage.setScene(new Scene(gridContainer, 2048, 1024));
        primaryStage.show();
    }

    private void processNewCells() {
        this.start(this.stage);
    }


    private GridPane updateUI() {
        GridPane gridContainer = new GridPane();

        int[][][] maze = controller.maze;

        Label[][] metal1 = new Label[dimensions[0]][];
        //Label[][] metal2 = new Label[dimensions[0]][];
        //Label[][] metal3 = new Label[dimensions[0]][];
        String nodeStyle = "-fx-border-color: black; -fx-font-size: 2;";
        String labelStyle = "-fx-font-size: 2;";

        // Initialize the metals grids
        for(int i=0; i<dimensions[0]; i++) {
            metal1[i] = new Label[dimensions[1]];
            //metal2[i] = new Label[dimensions[1]];
            //metal3[i] = new Label[dimensions[1]];
        }

        // Fill the grids
        for (int i = 0; i < dimensions[0]; i++) {
            for (int j = 0; j < dimensions[1]; j++) {
                metal1[i][j] = new Label(Integer.toString(maze[i][j][0]));
                //metal2[i][j] = new Label(Integer.toString(maze[i][j][1]));
                //metal3[i][j] = new Label(Integer.toString(maze[i][j][2]));

                metal1[i][j].setStyle(nodeStyle);
                //metal2[i][j].setStyle(nodeStyle);
                //metal3[i][j].setStyle(nodeStyle);

                controller.setInMetal1Grid(metal1[i][j], j,i);
                //controller.setInMetal2Grid(metal2[i][j], j,i);
                //controller.setInMetal3Grid(metal3[i][j], j,i);
            }
        }
        // Labels for each grid
        Label m1 = new Label("Metal 1");
        //Label m2 = new Label("Metal 2");
        //Label m3 = new Label("Metal 3");
        //Label m4 = new Label("Metal 4");
        m1.setStyle(labelStyle);
        //m2.setStyle(labelStyle);
        //m3.setStyle(labelStyle);
        //m4.setStyle(labelStyle);

        gridContainer.add(controller.metal1, 0, 0);
        gridContainer.add(m1, 0, 1);

        /*
        gridContainer.add(controller.metal2, 1, 0);
        gridContainer.add(m2, 1, 1);

        gridContainer.add(controller.metal3, 2, 0);
        gridContainer.add(m3, 2, 1);
        */

        gridContainer.setHgap(20.0);
        gridContainer.setAlignment(Pos.CENTER);

        Controller.currentColor = Controller.currentColor + 1 % Controller.hexColors.length;
        return gridContainer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
