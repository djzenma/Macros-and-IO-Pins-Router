
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
    private Node[][][] grids;
    private Hashtable<String,Macro> macrosTable ;
    private Set<Macro> macrosSet;
    private int xSize, ySize, zSize;
    private int xStart, yStart;
    private int cellWidth, cellHeight;

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

        this.xSize = xMax[0];
        this.ySize = yMax[0];
        this.zSize = tracks.size();


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
            cellVectors.add(new Vector(x,y, (double) rect.getZ()));
            Integer zKey = rect.getZ();
            //if(tracks.get(zKey).direction)
        }

        return cellVectors;
    }

    
}
