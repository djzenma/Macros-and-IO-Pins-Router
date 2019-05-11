 package Parser;

import java.util.ArrayList;
import java.util.List;

public class Net {
    private SpecialItem Special_Net ;


    private List<Item> Net;

    public Net() {
        this.Net = new ArrayList<>();
    }

    public void insertPin(String compName, String pinName) {
        this.Net.add(new Item(compName,pinName));
    }

    public void insertSpecialPin (String name , List<Rect> routingPath ,List <Via> vias)
    {
        this.Special_Net= new SpecialItem (name , routingPath , vias );
    }

    public List<Item> getNet() {
        return Net;
    }


    public static class Item {
        public String compName, pinName;

        public Item(String compName, String pinName) {
            this.compName = compName;
            this.pinName = pinName;
        }
    }

    public class SpecialItem{
        public String name ;
        public List<Rect> routingPath;
        public List <Via> vias ;
        
        public SpecialItem(String name , List<Rect> routingPath , List <Via> vias)
        {
            this.name = name ;
            this.routingPath = routingPath ;
            this.vias = vias ;
        }
    }
    
    
}
