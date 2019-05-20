package Routing;

import Algorithm.Node;
import Algorithm.Node.NodeType;
import GUI.Main;
import Parser.*;
import Placement.Placer;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

public class Router {
    private int gboxSize = 5;
    public static int xGridSize;
    public static int yGridSize;


    private GBox[][][] grids;
    private Node [][][] Matrix ;

    private HashSet<Net> nets;       // Set of all the nets blocks, each block contains the pins that need to be connected
    private Hashtable<String, Macro> placedMacros;
    private Hashtable<String, Macro> definedMacros;
    private Hashtable<Net.Item, Vector> pinLocations;
    public static Hashtable <Integer , Track> tracks;
    private List<Vector> legalizedObsLocations;
    private List <Vector> detailedPathsList;


    // helper global variables
    private int[] targetCoords = null;
    private int [] detailedFirst = null;
    private List <Node> globalPath = null ;
    private List <Node> pathDetailed = null ;
    private List <Node> globalNetPaths;     // list of all the recent global paths in the same net block to choose a near target from it
    private List <Node> detailedNetPaths;   // list of all the recent detailed paths in the same net block to choose a near target from it

    public Router(HashSet<Net> nets, Hashtable<String, Macro> placedMacros,
            Hashtable<String, Macro> definedMacros, Hashtable<Net.Item, Vector> pinLocations, 
            Hashtable <Integer , Track> tracks, List<Vector> obsLocations) {
        this.nets = nets;
        this.placedMacros = placedMacros;
        this.definedMacros = definedMacros;
        this.pinLocations = pinLocations;
        Router.tracks = tracks;
        this.legalizedObsLocations = new ArrayList<>();
        for( Vector v: obsLocations) {
            this.legalizedObsLocations.add(legalizeVector(v));
        }

        xGridSize = Placer.xSize / gboxSize;
        yGridSize = Placer.ySize / gboxSize;
        
        // Initialization
        grids = new GBox[xGridSize][yGridSize][Placer.zSize];
        for (int i = 0; i < xGridSize; i++) {
            for (int j = 0; j < yGridSize; j++) {
                for (int k = 1; k < Placer.zSize; k++) { // 1 --> 4
                    grids[i][j][k] = new GBox(new Vector(i,j,k), false, false, false, false);
                }
            }
        }
        
        Matrix = new Node[Placer.xSize ][Placer.ySize ][Placer.zSize];;
        for (int i = 0; i < Placer.xSize; i++) {
            for (int j = 0; j < Placer.ySize; j++) {
                for (int k = 1; k < Placer.zSize; k++) { // 1 --> 4
                    Matrix[i][j][k] = new Node(i,j,k);
                }
            }
        }

        detailedPathsList = new ArrayList<>();
        placeObs();        
        route();
        
    }
    
    private void placeObs() {
        this.legalizedObsLocations.forEach((obsVector) -> {
            this.grids[(int) obsVector.x][(int) obsVector.y][(int) obsVector.z].isObs = true;
        });
    }
    
    
    private void route() {
         this.nets.forEach((net) -> { //pass by every net
            final boolean[] firstPin = {true};
            targetCoords = null ;
            globalPath = null;
            pathDetailed = null;
            net.getNet().forEach((item)-> { // pass by ever item in net 
                // Get the macro's base location from the placed Macros Table
                Macro macro = this.placedMacros.get(item.compName);
                // Look up the pin item in its corresponding Macro from the defined Macros table
                Iterator<Pin> iterator = this.definedMacros.get(macro.name).pins.iterator();
                iterator.forEachRemaining(pinIter -> {
                    if (pinIter.name.equals(item.pinName)) {
                        Vector pinLocation = this.pinLocations.get(item);
                        globallyRoute(pinLocation, firstPin[0]); //set global globalPath if not first 
                        detailedRoute(pinLocation, firstPin[0]);
                    }
                });

                firstPin[0] = false;
            });
        });
    }
    
    private void detailedRoute(Vector pinLocation, boolean firstPin) {
        if (!firstPin) {
            int[] dimensions = new int[] {Placer.xSize, Placer.ySize, Placer.zSize };
            int[] sourceCoords = new int[] {(int) pinLocation.x, (int) pinLocation.y, (int) pinLocation.z};
            
            List <Vector> detailedObs = constructObsLocationsFromGlobalPathToDetailed();
            
            if (pathDetailed != null  && pathDetailed.size() != 0 ) // if not second
            {
                addPathToHistory(pathDetailed);
                List <Node> pathDetailed_Temp;
                List <Node> tested = new ArrayList<>();
                do
                {
                    Node target = getNearest (detailedNetPaths, sourceCoords , tested ); // pass a list of the recent paths in the same net block
                    tested.add(target);
                    detailedFirst = new int[] {target.x, target.y, target.z} ;
                    pathDetailed_Temp = Algorithm.Main.main(dimensions, sourceCoords , detailedFirst, detailedObs);
                } while(pathDetailed_Temp.size()== 0 && tested.size() != pathDetailed.size());
                if (pathDetailed_Temp.size() != 0)
                pathDetailed = pathDetailed_Temp ;
                if (pathDetailed.size() != 0 && pathDetailed != null)
                    draw (detailedFirst , sourceCoords , detailedObs ,dimensions ,pathDetailed );
            }
            else {    // is second time
                GUI.Main.firstTimeInDetailedRouting = true;
                pathDetailed = Algorithm.Main.main(dimensions, sourceCoords, detailedFirst, detailedObs);
                if (pathDetailed.size() != 0 && pathDetailed != null)
                    draw (detailedFirst , sourceCoords , detailedObs ,dimensions ,pathDetailed );
                
                if(pathDetailed.size() == 0 )
                    System.err.println("Detailed path not found for the first 2 pins in the current net block");
                GUI.Main.firstTimeInDetailedRouting = false;
            }
            detailedNetPaths.addAll(pathDetailed);
        }
        else {  // is first pin
            pathDetailed = null ;
            detailedNetPaths = new ArrayList<>();
            detailedFirst = new int[]{(int) pinLocation.x, (int) pinLocation.y, (int) pinLocation.z};
        }
    }
    
    private void addPathToHistory(List<Node> path) {
        for(Node n: path) {
            detailedPathsList.add(new Vector(n.x, n.y, n.z));
        }
    }

    /*
        Creates a list of the obstacles, the function transforms any node that is not in the Gpath to an obstacle
        so the AStar only searches in the global path
     */
    private List<Vector> constructObsLocationsFromGlobalPathToDetailed() {
        List<Vector> obsLocations = new ArrayList<>();
        for(int i = 0; i < Placer.xSize; i++) {
            for(int j = 0; j < Placer.ySize; j++) {
                for(int k = 1; k < Placer.zSize ; k++) {
                    if(!isInGlobalPath(i,j,k) || detailedPathsList.contains(new Vector(i,j,k)))
                        obsLocations.add(new Vector(i,j,k));
                }
            }
        }
        return obsLocations;
    }

    /*
        @return true if the detailed coords are in the global path
     */
    private boolean isInGlobalPath(int x, int y, int z) {
        for(Node gBox: globalPath) {
            if( (x >= gBox.x * gboxSize && x < (gBox.x + 1) * gboxSize) 
                 && (y >= gBox.y * gboxSize && y < (gBox.y + 1) * gboxSize)
                 && z == gBox.z)
                return true;
        }
        return false;
    }
    
    
    private void globallyRoute (Vector offset, boolean firstPin) {
        Vector legalizedOffset = legalizeVector(offset);
        if(firstPin) {      // 1st time
            globalNetPaths = new ArrayList<>();
            globalPath = null;
            this.grids[(int) legalizedOffset.x][(int) legalizedOffset.y][(int) offset.z].isTarget = true;
            targetCoords = new int[]{(int) legalizedOffset.x, (int) legalizedOffset.y, (int) offset.z};
        }
        else {
            Main.globalRouting = true;
            this.grids[(int) (legalizedOffset.x) ][(int) (legalizedOffset.y)][(int) offset.z].isSource = true;
            int[] dimensions = new int[]{xGridSize, yGridSize, Placer.zSize };
            int[] sourceCoords = new int[]{ (int) legalizedOffset.x, (int) legalizedOffset.y, (int) offset.z};
            if (globalPath != null && globalPath.size() != 0) {
                List <Node> tested = new ArrayList<>();
                List <Node> pathTemp;
                do {
                    Node target = getNearest (globalNetPaths, sourceCoords , tested );
                    targetCoords =  new int[]{target.getX(), target.getY(), target.getZ()};
                    pathTemp = Algorithm.Main.main(dimensions, sourceCoords, targetCoords, legalizedObsLocations);
                    tested.add(target);
                } while (pathTemp.size() == 0 && tested.size() != globalPath.size());
                if (pathTemp.size() != 0)
                globalPath = pathTemp ;
            }
            else {  // 2nd time
                Main.firstTimeInglobalRouting = true;
                globalPath = Algorithm.Main.main(dimensions, sourceCoords, targetCoords, legalizedObsLocations);
                if(globalPath.size() == 0)
                    System.err.println("Global path not found for the first 2 pins in the current net block");
                Main.firstTimeInglobalRouting = false;
            }
            globalNetPaths.addAll(globalPath);

            setPath (globalPath);
            Main.globalRouting = false;
        }
    }



    
    private Vector legalizeVector(Vector v) {
        return new Vector((int)Math.floor(v.x/gboxSize) , (int)Math.floor(v.y/gboxSize) , v.z);
    }
    
    
    
    
    
    
    public void printGbox ()
    {
        for (int z= 1 ;z < Placer.zSize ;z++)
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
            grids[n.getX()][n.getY()][n.getZ() + 1].isPath = true ;
        }
        
        
    }
    
    private Node getNearest (List <Node> path ,int[] sourceCoords ,List <Node> tested )
    {
        int min = Integer.MAX_VALUE;
        Node minNode = null ;
        int dist ;
        for (Node n : path) {
            if (!tested.contains(n)) {
                dist = (int) Math.sqrt(Math.pow(sourceCoords[0]-n.getX(), 2)+ Math.pow(sourceCoords[1]-n.getY(), 2) + Math.pow(sourceCoords[2]-n.getZ(), 2) );
                if (dist < min) {
                    min = dist ;
                    minNode = n ;
                }
            }
        }
        
       return minNode;     
    }
    public void draw (int [] detailedFirst, int [] sourceCoords ,  List <Vector> detailedObs,int []dimensions , List <Node> pathDetailed )
    {
        Matrix[(int)sourceCoords[0]][(int)sourceCoords[1]][(int)sourceCoords[2]].nodeType= NodeType.Pin ;
        Matrix[(int)detailedFirst[0]][(int)detailedFirst[1]][(int)detailedFirst[2]].nodeType= NodeType.Pin ;
        for (Vector v : detailedObs)
        {
                Matrix[(int)v.x][(int)v.y][(int)v.z].nodeType= NodeType.Obstacle ;
        }

        for (Node n: pathDetailed)
        {
                Matrix[(int)n.x][(int)n.y][(int)n.z].nodeType = NodeType.Obstacle ;
        }

                for (int k=1 ;k<(int)dimensions[2] ;k++ )
                {
                    for(int j=0 ;j<(int)dimensions[1];j++)
                    {
                        for (int i=0 ;i<(int)dimensions[0];i++)
                        {
                            if (Matrix[i][j][k].nodeType == NodeType.Obstacle)
                                System.out.print("O ");
                            else
                                if (Matrix[i][j][k].nodeType == NodeType.Pin)
                                System.out.print("P ");
                            else
                                    System.out.print("- ");
                                    
                        }
                        System.out.println(" ");
                    }
                    System.out.println("Metal" + k);

                }

    }
    
}
