
package Placement;
import Algorithm.Node;
import Parser.Macro;
import Parser.Rect;
import Parser.Track;
import Parser.Vector;
import java.util.Hashtable;
import java.util.Set;
import java.util.function.Consumer;

public class Placer {
    
    Hashtable <String , Track> tracks;
    Rect dieArea ;
    Vector coreSite ;
    Node[][][] grids;
    Hashtable<String,Macro> macrosTable ;
    Set<Macro> macrosSet ;
    
    public Placer (Hashtable <String , Track> tracks, Rect dieArea , Vector coreSite , Hashtable<String,Macro> macrosTable, Set<Macro> macrosSet)
    {
        this.tracks = tracks ;
        this.dieArea = dieArea ;
        this.coreSite = coreSite ;
        this.macrosTable = macrosTable ;
        this.macrosSet = macrosSet ;
    }
    
    public void constructGrids() {
        int x = (int) Math.ceil(dieArea.getX()/coreSite.x );
        int y = (int) Math.ceil(dieArea.getY()/coreSite.y) ;
        int z = tracks.size() ;
        grids = new Node [x][y][z];
        for (int k=0 ;k<z ;k++)
            for (int j=0 ;j<y ;j++)
                for (int i=0 ;i<x ;i++)
                    grids[i][j][k]= new Node(i,j,k);
                
     
    }
    
    public void addPins ()
    {
        macrosSet.forEach((macro)-> {
            macro.pins.computeLocation(macro.location);
        });
    }
    
    
}
