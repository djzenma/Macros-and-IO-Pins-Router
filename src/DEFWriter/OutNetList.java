package DEFWriter;

import Parser.Net;
import Parser.Vector;

import java.util.ArrayList;
import java.util.List;

public class OutNetList {
    public ArrayList<OutNet> Nets;
    //public ArrayList<Layer> layers;

    public Integer currentSize;

    public  OutNetList(){
        //this.layers = layers;
        Nets = new ArrayList<>();
        this.currentSize = -1;
    }

    public void addNet(String name, List<Net.Item> pins){
        if(pins != null & name != null){
            System.out.println(name);
            System.out.println(pins);
            System.out.println(pins.size());
            for (int i =0; i < pins.size(); i++){
                System.out.println(pins.get(i).compName);
                System.out.println(pins.get(i).pinName);
            }

            this.Nets.add(new OutNet(name, pins));
            this.currentSize = this.currentSize + 1;
        }
    }

    public void addPaths(ArrayList<Vector> vecs){
        OutNet ON = Nets.get(currentSize);
        ON.addPath(vecs);
        Nets.set(currentSize, ON);
    }

    public String getAsString(){
        Integer size = Nets.size();
        String rtn = "NETS " + size.toString() + " ; \n";

        for (int i = 0; i < this.Nets.size(); i++){
            rtn = rtn + Nets.get(i).getAsString();
        }

        return rtn + "END NETS \n";
    }

}
