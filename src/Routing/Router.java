package Routing;

import Parser.*;
import Placement.Placer;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class Router {
    private int gboxSize = 5;
    private int xGridSize;
    private int yGridSize;

    private GBox[][][] grids;

    private HashSet<Net> nets;
    private Hashtable<String, Macro> placedMacros;
    private Hashtable<String, Macro> definedMacros;
    private Hashtable<Parser.Net.Item, Vector> pinLocations;

    public Router(HashSet<Net> nets, Hashtable<String, Macro> placedMacros, Hashtable<String, Macro> definedMacros, Hashtable<Parser.Net.Item, Vector> pinLocations) {
        this.nets = nets;
        this.placedMacros = placedMacros;
        this.definedMacros = definedMacros;
        this.pinLocations = pinLocations;

        xGridSize = Placer.xSize / gboxSize;
        yGridSize = Placer.ySize / gboxSize;

        // Initialization
        grids = new GBox[xGridSize][yGridSize][Placer.zSize];
        for (int i = 0; i < xGridSize; i++) {
            for (int j = 0; j < yGridSize; j++) {
                for (int k = 0; k < Placer.zSize; k++) {
                    grids[i][j][k] = new GBox(new Vector(i,j,k), false, false, false);
                }
            }
        }


        nets.forEach((net) -> {
            final boolean[] first = {true};

            net.getNet().forEach((item)-> {
                Macro macro = placedMacros.get(item.compName);
                Vector baseLocation = macro.location;

                Iterator<Pin> iterator = definedMacros.get(macro.name).pins.iterator();
                final Pin[] pin = {null};
                iterator.forEachRemaining(pinIter -> {
                    if(pinIter.name.equals(item.pinName)) {
                        placeInGbox(pinLocations.get(item), first[0]);     // Get location of the pin in the placed grids
                    }
                });

                first[0] = false;
            });


        });
    }

    private void placeInGbox(Vector vector, boolean first) {
        if(first)
            this.grids[(int) vector.x / gboxSize ][(int) vector.y / gboxSize ][(int) vector.z].isSource = true;
        else {
            this.grids[(int) vector.x / gboxSize ][(int) vector.y / gboxSize ][(int) vector.z].isTarget = true;
        }
    }


}
