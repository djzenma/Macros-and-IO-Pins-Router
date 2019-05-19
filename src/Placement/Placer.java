
package Placement;
import Algorithm.Node;
import GUI.Controller;
import Parser.*;

import java.util.Hashtable;

import static Algorithm.Node.*;
import java.util.ArrayList;
import java.util.List;

public class Placer {
    private final Hashtable<String, Layer> layersTable;
    private static Hashtable <Integer , Track> tracks;
    private Rect dieArea ;
    private Vector coreSite ;
    private Hashtable<String,Macro> placedMacros;
    private Hashtable<String,Macro> definedMacros;

    public static Node[][][] grids;
    public static int xSize, ySize, zSize;    // The number of cells per grid layer
    public static int xStart, yStart;         // Coordinates of the start of the grid
    public static int cellWidth, cellHeight;
    private static Hashtable<Integer, Integer> layersRatios;   // The ratio of every metal layer relative to the maximum one

    public Placer (Hashtable <Integer , Track> tracks, Rect dieArea ,
                   Vector coreSite , Hashtable<String,Macro> placedMacros, Hashtable<String, Macro> definedMacros, Hashtable<String, Layer> layersTable)
    {
        this.tracks = tracks ;
        this.dieArea = dieArea ;
        this.coreSite = coreSite ;
        this.placedMacros = placedMacros;
        this.definedMacros = definedMacros;
        this.layersTable = layersTable;
        this.layersRatios = new Hashtable<>();
        constructGrids();
    }
    
    private void constructGrids() {
        final int[] xMax = {0};
        final int[] yMax = {0};

        // Getting the maximum number of cells to create our grids
        tracks.forEach((key, track) -> {
            if(track.direction == Track.X) {
                if(track.number > xMax[0]) {
                    xMax[0] = track.number;
                    cellWidth = track.step;
                    xStart = track.start;
                }
            }
            else {
                if (track.number > yMax[0]) {
                    yMax[0] = track.number;
                    cellHeight = track.step;
                    yStart = track.start;
                }
            }
        });

        // The maximum number of cells per layer
        xSize = xMax[0];
        ySize = yMax[0];
        zSize = tracks.size() + 1;     // Because we don't start from the 0 index TODO:: verify


        // Calculating the ratio of every metal layer relative to the maximum one
        tracks.forEach((key, track) -> {
            if(track.direction == Track.X) {
                this.layersRatios.put( key,xMax[0] / track.number);
            }
            else {
                this.layersRatios.put( key, yMax[0] / track.number);
            }
        });


        // Initialization
        grids = new Node [xSize][ySize][zSize];
        for (int k=0 ;k<zSize ;k++) {
            for (int j = 0; j < ySize; j++) {
                for (int i = 0; i < xSize; i++) {
                    grids[i][j][k] = new Node(i, j, k);
                }
            }
        }
    }


    public List<Vector> addObsInGrid () {
        List<Vector> obsLocations = new ArrayList<>();
        
        placedMacros.forEach((key, macro)-> {
            Macro macroDefinition =  definedMacros.get(macro.name);
            Vector baseLocation = macro.location;

            macroDefinition.obsList.forEach(rect -> {
                if(rect.getZ() != 0) {
                    Rect convertedRect = convertUnitToCell(rect, baseLocation);
                    int zKey = convertedRect.getZ();
                    for (int i = Math.min( (int) convertedRect.point2.x, (int) convertedRect.point1.x); i <= Math.max( (int) convertedRect.point2.x, (int) convertedRect.point1.x) ; i++) {
                        for (int j = Math.min( (int) convertedRect.point2.y, (int) convertedRect.point1.y); j <= Math.max( (int) convertedRect.point2.y, (int) convertedRect.point1.y); j++) {
                            Node node = new Node(i, j, zKey);
                            node.nodeType= NodeType.Obstacle ;
                            grids[i][j][zKey] = node;
                            obsLocations.add(new Vector(i,j,zKey));
                        }
                    }
                }
            });
        });
        
        return obsLocations;
    }


    public Hashtable<Net.Item, Vector> addPinsInGrid () {
        Hashtable<Net.Item, Vector> pinLocations = new Hashtable<>();

        placedMacros.forEach((key, macro)-> {
            Macro macroDefinition =  definedMacros.get(macro.name);
            Vector baseLocation = macro.location;

            macroDefinition.pins.forEach(pin -> {

                pin.rectList.forEach((rect) -> {
                    Rect convertedRect = convertUnitToCell(rect, baseLocation);
                    int zKey = convertedRect.getZ();

                    for (int i = Math.min( (int) convertedRect.point2.x, (int) convertedRect.point1.x); i <= Math.max( (int) convertedRect.point2.x, (int) convertedRect.point1.x) ; i++) {
                        for (int j = Math.min( (int) convertedRect.point2.y, (int) convertedRect.point1.y); j <= Math.max( (int) convertedRect.point2.y, (int) convertedRect.point1.y); j++) {
                            if(grids[i][j][zKey].nodeType == Node.NodeType.Pin){
                                pin.location = new Vector(i, j, zKey);
                                grids[i][j][zKey].pin.add(pin);
                            }
                            else {
                                Node node = new Node(i, j, zKey);
                                node.nodeType = NodeType.Pin;
                                pin.location = new Vector(i, j, zKey);
                                node.pin.add(pin);
                                grids[i][j][zKey] = node;
                            }
                            pinLocations.put(new Net.Item(key, pin.name), pin.location);
                        }
                    }

                });

            });
        });

        return pinLocations;
             
    }

    private Rect convertUnitToCell(Rect rect, Vector baseLocation) {
        Vector one = new Vector((int) Math.floor((rect.point1.x + baseLocation.x - xStart)/cellWidth), (int) Math.floor((rect.point1.y + baseLocation.y - yStart)/cellHeight));
        Vector two = new Vector((int) Math.floor((rect.point2.x + baseLocation.x - xStart)/cellWidth), (int) Math.floor((rect.point2.y + baseLocation.y - yStart)/cellHeight));

        Integer zKey = rect.getZ();

        return legalizeIndexes(new Rect(one, two), zKey);

    }

    public static Vector convertUnitToCellFromVector(Vector baseLocation , int gboxSize) {
        Vector vector = new Vector((int) Math.floor((baseLocation.x - xStart)/cellWidth), (int) Math.floor((baseLocation.y - yStart)/cellHeight));
        Integer z = (int) baseLocation.z;

        return legalizeIndexesFromVector(vector, z , gboxSize);

    }

    private static Vector legalizeIndexesFromVector(Vector vector, Integer z, int gboxSize ) {        
        int x= (int)vector.x/gboxSize;
        int y= (int)vector.y/gboxSize;
            
       
        return new Vector(x,y);
    }


    private static Rect legalizeIndexes(Rect rect, Integer zKey) {

        int xStart = (int)rect.point1.x;
        int yStart = (int)rect.point1.y;

        int xEnd = (int)rect.point2.x;
        int yEnd = (int) rect.point2.y;

        if(tracks.get(zKey).direction == Track.X){
            yStart = yStart - (yStart % layersRatios.get(zKey));
            yEnd = yEnd - (yEnd % layersRatios.get(zKey)) + layersRatios.get(zKey) - 1;
            
        }else{
            xStart = xStart - (xStart % layersRatios.get(zKey));
            xEnd = xEnd - (xEnd % layersRatios.get(zKey)) + layersRatios.get(zKey) - 1;
        }


        return new Rect(new Vector(xStart, yStart, zKey), new Vector(xEnd, yEnd, zKey));
    }

    public int[][][] draw(){
        int[][][] maze = new int[xSize][ySize][zSize];

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                for (int k = 0; k < zSize; k++) {
                    switch (grids[i][j][k].nodeType) {
                        case Empty:
                            maze[i][j][k] = 0;
                            break;
                        case Pin:
                            maze[i][j][k] = 2;
                            break;
                        case Obstacle:
                            maze[i][j][k] = 1;
                            break;
                    }
                }
            }
        }

        return maze;
    }


    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }

    // The z size assumes you start from 1-index
    public int getzSize() {
        return zSize;
    }


    public Node[][][] getGrids() {
        return grids;
    }

}
