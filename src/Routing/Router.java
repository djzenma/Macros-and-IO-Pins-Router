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
                for (int k = 1; k < Placer.zSize; k++) {
                    grids[i][j][k] = new GBox(new Vector(i,j,k), false, false, false);
                }
            }
        }


        this.nets.forEach((net) -> {
            final boolean[] firstPin = {true};

            net.getNet().forEach((item)-> {
                // Get the macro's base location from the placed Macros Table
                Macro macro = this.placedMacros.get(item.compName);
                Placer.convertUnitToCellFromVector(macro.location);
                Vector baseLocation = macro.location;

                // Look up the pin item in its corresponding Macro from the defined Macros table
                Iterator<Pin> iterator = this.definedMacros.get(macro.name).pins.iterator();
                iterator.forEachRemaining(pinIter -> {
                    if (pinIter.name.equals(item.pinName)) {
                        Vector offset = this.pinLocations.get(item);
                        this.pinLocations.forEach((keystr, pinLocation) -> {
                            if(keystr.compName.equals(item.compName) && keystr.pinName.equals(item.pinName))
                            System.out.println(keystr.compName + " " + keystr.pinName + " vector: " + pinLocation);
                        });
                        placeInGbox(baseLocation, offset, firstPin[0]);     // Get location of the pin in the placed grids
                    }
                });

                firstPin[0] = false;
            });
        });
    }

    private void placeInGbox(Vector base, Vector offset, boolean firstPin) {
        if(firstPin)
            this.grids[(int) (base.x + offset.x) / gboxSize ][(int) (base.y + offset.y) / gboxSize ][(int) offset.z].isSource = true;
        else {
            this.grids[(int) (base.x + offset.x) / gboxSize ][(int) (base.y + offset.y) / gboxSize ][(int) offset.z].isTarget = true;
        }
    }


}
