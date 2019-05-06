
package Parser;

import java.util.ArrayList;
import java.util.List;

public class Macro {
    
   public Vector location;
   public String name;
   public ArrayList <Pin> pins;
   public List <Rect> obsList;

   Macro(String name, Vector location, ArrayList <Pin> pins, List<Rect> obsList) {
       this.name = name;
       this.location = location;
       this.pins = pins;
       this.obsList = obsList;
   }
   
   Macro(String name, Vector location) {
       this.name = name;
       this.location = location;
   }
   
   public void setPins(ArrayList <Pin> pins) {
       this.pins = pins;
   }
   
   @Override
   public String toString() {
       return "Name : " + this.name + " x:" + this.location.x + " y: " + this.location.y ;
   }
    
}