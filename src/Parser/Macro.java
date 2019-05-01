
package Parser;

import java.util.ArrayList;

public class Macro {
    
   Vector location;
   String name;
   ArrayList <Pin> pins;
   
   Macro(String name, Vector location, ArrayList <Pin> pins) {
       this.name = name;
       this.location = location;
       this.pins = pins;
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