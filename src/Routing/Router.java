package Routing;

import Algorithm.Node;
import Parser.*;
import Placement.Placer;
import java.util.ArrayList;

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
    int [] detailedFirst = null;
    List <Node> globalPath = null ;
    List <Node> pathDetailed = null ;


    private GBox[][][] grids;

    private HashSet<Net> nets;       // Set of all the nets blocks, each block contains the pins that need to be connected
    private Hashtable<String, Macro> placedMacros;
    private Hashtable<String, Macro> definedMacros;
    private Hashtable<Net.Item, Vector> pinLocations;
    public static Hashtable <Integer , Track> tracks;
    private List<Vector> legalizedObsLocations;
    private List <Vector> obsLocations ;
    private List <Vector> detailedPathsList;
    

    public Router(HashSet<Net> nets, Hashtable<String, Macro> placedMacros, 
            Hashtable<String, Macro> definedMacros, Hashtable<Net.Item, Vector> pinLocations, 
            Hashtable <Integer , Track> tracks, List<Vector> obsLocations) {
        this.nets = nets;
        this.placedMacros = placedMacros;
        this.definedMacros = definedMacros;
        this.pinLocations = pinLocations;
        this.tracks = tracks;
        this.legalizedObsLocations = new ArrayList<>();
        this.obsLocations = obsLocations ;
        for( Vector v: obsLocations) {
            this.legalizedObsLocations.add(legalizeVector(v));
        }

        xGridSize = Placer.xSize / gboxSize;
        yGridSize = Placer.ySize / gboxSize;
        
        // Initialization
        grids = new GBox[xGridSize][yGridSize][Placer.zSize];
        for (int i = 0; i < xGridSize; i++) {
            for (int j = 0; j < yGridSize; j++) {
                for (int k = 1; k < Placer.zSize; k++) {
                    grids[i][j][k] = new GBox(new Vector(i,j,k), false, false, false, false);
                }
            }
        }

        detailedPathsList = new ArrayList<>();
        placeObs();        
        route();
        
    }
    
    private void placeObs() {
        this.legalizedObsLocations.forEach((obsVector) -> {
            this.grids[(int) (obsVector.x)  ][(int) ( obsVector.y)  ][(int) obsVector.z].isObs = true;
        });
    }
    
    
    private void route() {
         this.nets.forEach((net) -> { //pass by every net
            final boolean[] firstPin = {true};
            targetCoords = null ;

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
            int[] dimensions = new int[] {Placer.xSize, Placer.ySize, Placer.zSize -1};
            int[] sourceCoords = new int[] {(int) pinLocation.x, (int) pinLocation.y, (int) pinLocation.z};
            
            List <Vector> detailedObs = constructObsLocationsFromGlobalPathToDetailed();
            
            if (pathDetailed != null  && pathDetailed.size() != 0 ) //  second
            {
                addPathToHistory(pathDetailed);
                List <Node> pathDetailed_Temp = new ArrayList ();
                List <Node> tested = new ArrayList ();
                do
                {
                    Node target = getNearest (pathDetailed, sourceCoords , tested ); 
                    tested.add(target);
                    detailedFirst = new int[] {(int)target.x ,(int) target.y , (int)target.z} ;
                    pathDetailed_Temp = Algorithm.Main.main(dimensions, sourceCoords , detailedFirst, detailedObs);

                } while(pathDetailed.size()== 0 && tested.size() != pathDetailed.size());

                pathDetailed = pathDetailed_Temp ;
            }
            else
                pathDetailed = Algorithm.Main.main(dimensions, sourceCoords , detailedFirst, obsLocations);
        }
        else
            detailedFirst  = new int[]{(int)pinLocation.x, (int)pinLocation.y , (int)pinLocation.z};
    }
    
    private void addPathToHistory(List<Node> path) {
        for(Node n: path) {
            detailedPathsList.add(new Vector(n.x, n.y, n.z));
        }
    }
    
    private List<Vector> constructObsLocationsFromGlobalPathToDetailed() {
        List<Vector> obsLocations = new ArrayList<>();
        for(int i = 0; i < Placer.xSize; i++) {
            for(int j = 0; j < Placer.ySize; j++) {
                for(int k = 0; k < Placer.zSize - 1; k++) {
                    if(!isInGlobalPath(i,j,k) || detailedPathsList.contains(new Vector(i,j,k)))
                        obsLocations.add(new Vector(i,j,k));
                }
            }
        }
        return obsLocations;
    }
    
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
        if(firstPin) {
            this.grids[(int) (legalizedOffset.x)  ][(int) ( legalizedOffset.y)  ][(int) offset.z].isTarget = true;
            targetCoords = new int[]{(int) legalizedOffset.x, (int) legalizedOffset.y, (int) offset.z};
        }
        else {
            this.grids[(int) (legalizedOffset.x) ][(int) (legalizedOffset.y)][(int) offset.z].isSource = true;
            int[] dimensions = new int[]{this.xGridSize, this.yGridSize, Placer.zSize - 1};
            int[] sourceCoords = new int[]{ (int) legalizedOffset.x, (int) legalizedOffset.y, (int) offset.z};
            if (globalPath != null && globalPath.size() != 0)
            {
                List <Node> tested = new ArrayList ();
                List <Node> pathTemp  = new ArrayList ();
                do {
                    Node target = getNearest (globalPath, sourceCoords , tested ); // TODO:: add to the globalPath all the net block privious paths
                    targetCoords =  new int[]{ (int) target.getX(), (int) target.getY(), (int)target.getZ()};
                    pathTemp = Algorithm.Main.main(dimensions, sourceCoords, targetCoords, legalizedObsLocations);
                    tested.add(target);
                } while (pathTemp.size() == 0 && tested.size() != globalPath.size());
                
                globalPath = pathTemp ;
            }
            else
                globalPath = Algorithm.Main.main(dimensions, sourceCoords, targetCoords, legalizedObsLocations);
                
            setPath (globalPath);

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
        for (Node n : path)
        {
            if (!tested.contains(n))
            {
                dist = (int) Math.sqrt(Math.pow(sourceCoords[0]-n.getX(), 2)+ Math.pow(sourceCoords[1]-n.getY(), 2) + Math.pow(sourceCoords[2]-n.getZ(), 2) );
                if (dist < min)
                {

                    min = dist ;
                    minNode = n ;
                }
            }
        }
        
       return minNode;     
    }
    
}
