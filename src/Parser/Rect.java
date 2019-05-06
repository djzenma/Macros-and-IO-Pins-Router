package Parser;

public class Rect {
    private Vector point1;
    private Vector point2;

    public Rect(Vector point1, Vector point2) {
        this.point1 = point1;
        this.point2 = point2;
    }
    
    public Vector[] getVectors() {
        Vector[] points = {this.point1, this.point2};
        return points;
    }
    public int getX ()
    {
        return (int) Math.abs(point2.x - point1.x);
    }
    
    public int getY ()
    {
        return (int) Math.abs(point2.y - point1.y);
    }
}
