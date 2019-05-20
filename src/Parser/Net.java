 package Parser;

import java.util.ArrayList;
import java.util.List;

public class Net {
    private List<SpecialItem> Special_Net ;
    private List<Item> Net;

    public Net() {
        this.Net = new ArrayList<>();
        this.Special_Net = new ArrayList<>();
    }

    public void insertPin(String compName, String pinName) {
        this.Net.add(new Item(compName,pinName));
    }

    public void insertSpecialPin (String name , List<Rect> routingPath ,List <Via> vias)
    {
        this.Special_Net.add(new SpecialItem (name , routingPath , vias ));
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

        public boolean equals(Object other) {
            Item otherItem = (Item) other;
            return this.pinName.equals(otherItem.pinName) && this.compName.equals(otherItem.compName);
        }

        public int hashCode() { return 0; }
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
