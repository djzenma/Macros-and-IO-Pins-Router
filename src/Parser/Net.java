package Parser;

import java.util.ArrayList;
import java.util.List;

public class Net {
    private List<Item> list;

    public Net() {
        this.list = new ArrayList<>();
    }

    public void insertPin(String compName, String pinName) {
        this.list.add(new Item(compName,pinName));
    }


    class Item {
        public String compName, pinName;

        public Item(String compName, String pinName) {
            this.compName = compName;
            this.pinName = pinName;
        }
    }
}
