
package Parser;

import java.util.List;

public class Pin {
    public List<Rect> rectList ;
    public String name ;
    public Vector location;

    Pin (List<Rect> rectList , String name)
    {
        this.rectList = rectList;
        this.name = name ;
    }


}
