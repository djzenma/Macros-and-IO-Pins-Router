package Routing;

import Parser.Vector;

public class GBox {
    public Vector position;
    public boolean isSource, isTarget, isPath;

    public GBox(Vector position, boolean isSource, boolean isTarget, boolean isPath) {
        this.position = position;
        this.isSource = isSource;
        this.isTarget = isTarget;
        this.isPath = isPath;
    }

    public GBox() {
    }
}
