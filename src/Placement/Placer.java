
package Placement;
import Algorithm.Node;
import Parser.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class Placer {

    private final Hashtable<String, Layer> layersTable;
    private Hashtable <Integer , Track> tracks;
    private Rect dieArea ;
    private Vector coreSite ;
    private Hashtable<String,Macro> macrosTable ;
    private Set<Macro> macrosSet;

    private Node[][][] grids;
    private int xSize, ySize, zSize;    // The number of cells per grid layer
    private int xStart, yStart;         // Coordinates of the start of the grid
    private int cellWidth, cellHeight;
    private Hashtable<Integer, Integer> layersRatios;   // The ratio of every metal layer relative to the maximum one

    public Placer (Hashtable <Integer , Track> tracks, Rect dieArea ,
                   Vector coreSite , Hashtable<String,Macro> macrosTable, Set<Macro> macrosSet, Hashtable<String, Layer> layersTable)
    {
        this.tracks = tracks ;
        this.dieArea = dieArea ;
        this.coreSite = coreSite ;
        this.macrosTable = macrosTable ;
        this.macrosSet = macrosSet ;
        this.layersTable = layersTable;
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
                    this.cellWidth = track.step;
                    this.xStart = track.start;
                }
            }
            else {
                if (track.number > yMax[0]) {
                    yMax[0] = track.number;
                    this.cellHeight = track.step;
                    this.yStart = track.start;
                }
            }
        });

        // The maximum number of cells per layer
        this.xSize = xMax[0];
        this.ySize = yMax[0];
        this.zSize = tracks.size();


        // Calculating the ratio of every metal layer relative to the maximum one
        tracks.forEach((key, track) -> {
            if(track.direction == Track.X) {
                this.layersRatios.put( key, track.number / xMax[0]);
            }
            else {
                this.layersRatios.put( key, track.number / yMax[0]);
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


    public void addObsInGrid () {
        macrosSet.forEach((macro)-> {
            macro.obsList.forEach(rect -> {

            });
        });
    }

    public List<Vector> convertUnitToCell(Rect rect) {
        List<Vector> cellVectors = new ArrayList<>();
        for (Vector vector: rect.getVectors()) {
            int x = (int) Math.floor((vector.x - xStart)/cellWidth);
            int y = (int) Math.floor((vector.y - yStart)/cellHeight);
            legalizeIndexes(x,y, rect.getZ());
            cellVectors.add(new Vector(x,y, (double) rect.getZ()));
            Integer zKey = rect.getZ();
            //if(tracks.get(zKey).direction)
        }

        return cellVectors;
    }

    private List<Vector> legalizeIndexes(int x, int y, int z) {
        List<Vector> indexes = new ArrayList<>();


        return indexes;
    }

    
}
