 package Parser;

import java.util.ArrayList;
import java.util.List;

public class Net {
    private List <SpecialItem> routing ;
    private List<Item> list;

    public Net() {
        this.list = new ArrayList<>();
        this.routing = new ArrayList<>();
    }

    public void insertPin(String compName, String pinName) {
        this.list.add(new Item(compName,pinName));
    }
    
    public void insertSpecialPin (String name , List<Rect> routingPath ,List <Via> vias)
    {
        this.routing.add(new SpecialItem (name , routingPath , vias ));
    }
    
    class Item {
        public String compName, pinName;

        public Item(String compName, String pinName) {
            this.compName = compName;
            this.pinName = pinName;
        }
    }
    class SpecialItem{
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
