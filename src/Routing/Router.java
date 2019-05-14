package Routing;

import Algorithm.Node;
import Parser.*;
import Placement.Placer;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Router {
    private int gboxSize = 5;
    public static int xGridSize;
    public static int yGridSize;
    int[] targetCoords = null;


    private GBox[][][] grids;

    private HashSet<Net> nets;       // Set of all the nets blocks, each block contains the pins that need to be connected
    private Hashtable<String, Macro> placedMacros;
    private Hashtable<String, Macro> definedMacros;
    private Hashtable<Net.Item, Vector> pinLocations;
    public static Hashtable <Integer , Track> tracks;

    public Router(HashSet<Net> nets, Hashtable<String, Macro> placedMacros, Hashtable<String, Macro> definedMacros, Hashtable<Net.Item, Vector> pinLocations, Hashtable <Integer , Track> tracks) {
        this.nets = nets;
        this.placedMacros = placedMacros;
        this.definedMacros = definedMacros;
        this.pinLocations = pinLocations;
        this.tracks = tracks;

        xGridSize = Placer.xSize / gboxSize;
        yGridSize = Placer.ySize / gboxSize;
        
        // Initialization
        grids = new GBox[xGridSize][yGridSize][Placer.zSize];
        for (int i = 0; i < xGridSize; i++) {
            for (int j = 0; j < yGridSize; j++) {
                for (int k = 1; k < Placer.zSize; k++) {
                    grids[i][j][k] = new GBox(new Vector(i,j,k), false, false, false);
                }
            }
        }


        this.nets.forEach((net) -> {
            final boolean[] firstPin = {true};
            targetCoords = null ;

            net.getNet().forEach((item)-> {
                // Get the macro's base location from the placed Macros Table
                Macro macro = this.placedMacros.get(item.compName);

                // Look up the pin item in its corresponding Macro from the defined Macros table
                Iterator<Pin> iterator = this.definedMacros.get(macro.name).pins.iterator();
                iterator.forEachRemaining(pinIter -> {
                    if (pinIter.name.equals(item.pinName)) {
                        Vector offset = this.pinLocations.get(item);
                        placeInGbox( offset, firstPin[0]);     // Get location of the pin in the placed grids
                    }
                });

                firstPin[0] = false;
            });
        });
    }

    private void placeInGbox( Vector offset, boolean firstPin) {
        Vector legalizedOffset = new Vector((int)Math.floor(offset.x/gboxSize) , (int)Math.floor(offset.y/gboxSize) , offset.z);
        if(firstPin) {
            this.grids[(int) (legalizedOffset.x)  ][(int) ( legalizedOffset.y)  ][(int) offset.z].isTarget = true;
            targetCoords = new int[]{(int) legalizedOffset.x, (int) legalizedOffset.y, (int) offset.z};
        }
        else {
            this.grids[(int) (legalizedOffset.x) ][(int) (legalizedOffset.y)][(int) offset.z].isSource = true;
            int[] dimensions = new int[]{this.xGridSize, this.yGridSize, Placer.zSize};
            int[] sourceCoords = new int[]{ (int) legalizedOffset.x, (int) legalizedOffset.y, (int) offset.z};
            List <Node> path = Algorithm.Main.main(dimensions, sourceCoords, targetCoords);
            setPath (path);
        }
    }

    
    
    
    
    
    
    
    
    public void printGbox ()
    {
        for (int z= 1 ;z <= Placer.zSize ;z++)
        {
            System.out.println("Metal " + (z));
            
            for (int j=0 ;j < yGridSize ;j++)
            {
                for (int i=0 ;i< xGridSize ;i++ )
                        {
                            if (grids[i][j][z].isSource == true)
                                System.out.print("S ");
                            else 
                                if (grids[i][j][z].isTarget == true)
                                    System.out.print("T ");
                            else
                               if (grids[i][j][z].isPath == true)
                                    System.out.print("P ");     
                            else
                                System.out.print("- ");
                        }
                System.out.print('\n');
            }
            
       }

    }
    
    public void setPath (List <Node> path)
    {
        for (Node n : path)
        {
            grids[n.getX()][n.getY()][n.getZ()].isPath = true ;
        }
        
        
    }
}
