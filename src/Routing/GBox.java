package Routing;

import Parser.Vector;

public class GBox {
    public Vector position;
    public boolean isSource, isTarget, isPath, isObs;

    public GBox(Vector position, boolean isSource, boolean isTarget, boolean isPath, boolean isObs) {
        this.position = position;
        this.isSource = isSource;
        this.isTarget = isTarget;
        this.isPath = isPath;
        this.isObs = isObs;
    }

    public GBox() {
    }
}
