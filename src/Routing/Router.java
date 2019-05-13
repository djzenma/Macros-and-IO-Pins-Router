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

    private HashSet<Net> nets;       // Set of all the nets blocks, each block contains the pins that need to be connected
    private Hashtable<String, Macro> placedMacros;
    private Hashtable<String, Macro> definedMacros;
    private Hashtable<Net.Item, Vector> pinLocations;

    public Router(HashSet<Net> nets, Hashtable<String, Macro> placedMacros, Hashtable<String, Macro> definedMacros, Hashtable<Net.Item, Vector> pinLocations) {
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


        this.nets.forEach((net) -> {
            final boolean[] first = {true};

            net.getNet().forEach((item)-> {
                Macro macro = this.placedMacros.get(item.compName);
                Vector baseLocation = macro.location;
                Iterator<Pin> iterator = this.definedMacros.get(macro.name).pins.iterator();

                final Pin[] pin = {null};
                iterator.forEachRemaining(pinIter -> {
                    if (pinIter.name.equals(item.pinName)) {
                        //placeInGbox(baseLocation, pinLocations.get(item), first[0]);     // Get location of the pin in the placed grids
                    }
                });

                first[0] = false;
            });
        });
    }

    private void placeInGbox(Vector base, Vector offset, boolean first) {
        if(first)
            this.grids[(int) (base.x + offset.x) / gboxSize ][(int) (base.y + offset.y) / gboxSize ][(int) offset.z].isSource = true;
        else {
            this.grids[(int) (base.x + offset.x) / gboxSize ][(int) (base.y + offset.y) / gboxSize ][(int) offset.z].isTarget = true;
        }
    }


}
