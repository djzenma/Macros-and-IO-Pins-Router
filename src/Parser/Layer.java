package Parser;

public class Layer {
    private String name;
    private String direction;
    private double layer;
    private boolean isMetal;
    private boolean isVia;

    public Layer(String name, String direction, float layer) {
        this.name = name;
        this.direction = direction;
        this.layer = layer;
        this.isMetal = true;
        this.isVia = false;
    }

    public Layer(String name, double layer) {
        this.name = name;
        this.layer = layer - 0.5;
        this.isMetal = false;
        this.isVia = true;
    }

    public Layer(String name) {
        this.name = name;
        this.isMetal = false;
        this.isVia = false;
    }


    public double getLayer() {
        return layer;
    }

    public boolean isMetal() {
        return isMetal;
    }

    public boolean isVia() {
        return isVia;
    }
}
