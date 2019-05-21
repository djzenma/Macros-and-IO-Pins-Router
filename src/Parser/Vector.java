package Parser;


public class Vector {
    public double x, y, z;

    public Vector (double x , double y , double z) {
        this.x = x ;
        this.y = y ;
        this.z = z ;
    }
    
    public Vector (double x , double y ) {
        this.x = x ;
        this.y = y ;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        Vector v = (Vector) obj;
        return (v.x == this.x && v.y == this.y && v.z == this.z);
    }
}
