package DEFWriter;

import Parser.Net;
import Parser.Parser;
import Parser.Vector;
import Parser.Layer;

import java.util.ArrayList;
import java.util.List;
import Parser.Net.Item;

public class OutNet {
    public String name;
    public ArrayList<OutPath> paths;
    public List<Item> pins;
    public ArrayList<Layer> layers;

    public OutNet(String name, List<Net.Item> pins){
        paths =new ArrayList<>();
        this.name = name;
        this.pins = pins;
        //this.layers = layers;
    }

    public void addPath(ArrayList<Vector> vecs){

        if(vecs.size() >1){

            int start = 0;
            int direction = 0;
            int index = 1;
            Boolean lastTime = false;

            while (index <= vecs.size()){

                if(index == vecs.size()){
                    lastTime = true;
                    index = index - 1;
                }

                if(start >= vecs.size()) {
                    break;
                }

                if(direction == 0){
                    if(lastTime){
                        paths.add(new OutPath((int)vecs.get(index).x, (int)vecs.get(index).y, (int)vecs.get(index).z, 0, 0));
                        lastTime = false;
                        break;
                    }
                    if(vecs.get(index).x != vecs.get(start).x){
                        direction = 1;
                        continue;
                    }else if(vecs.get(index).y != vecs.get(start).y){
                        direction = 2;
                        continue;
                    }else if(vecs.get(index).z != vecs.get(start).z){
                        // TODO: This VIA
                        direction = 3;
                    }
                }else if (direction == 1){
                    if(vecs.get(index).y != vecs.get(start).y | vecs.get(index).z != vecs.get(start).z |lastTime ){
                        paths.add(new OutPath((int)vecs.get(index).x, (int)vecs.get(index).y, (int)vecs.get(index).z, (int)(vecs.get(index).x - vecs.get(start).x), direction));
                        start = index;
                        index = index + 1;
                        if (lastTime){
                            lastTime = false;
                            break;
                        }
                        continue;
                    }
                }else if (direction == 2){
                    if(vecs.get(index).x != vecs.get(start).x | vecs.get(index).z != vecs.get(start).z | lastTime){
                        paths.add(new OutPath((int)vecs.get(index).x, (int)vecs.get(index).y, (int)vecs.get(index).z, (int)(vecs.get(index).y - vecs.get(start).y), direction));
                        start = index;
                        index = index + 1;
                        if (lastTime){
                            lastTime = false;
                            break;
                        }
                        continue;
                    }
                }else if(direction == 3){
                    // TODO: This VIA
                    if(vecs.get(index).x != vecs.get(start).x | vecs.get(index).y != vecs.get(start).y | lastTime){
                        paths.add(new OutPath("VIA", (int)vecs.get(index).x, (int)vecs.get(index).y, (int)vecs.get(index).z, (int)(vecs.get(index).z - vecs.get(start).z)));
                        start = index;
                        index = index + 1;
                        if (lastTime){
                            lastTime = false;
                            break;
                        }
                        continue;
                    }

                }


                index = index + 1;
            }
        }
    }

    /*
    public OutNet(String name, ArrayList<Vector> vecs){
        this.name = name;
        addPath(vecs);
    }
*/
    public String getAsString(){
        String rtn = "- " + name + " \n";

        for (Integer i = 0; i < pins.size(); i++){
            rtn = rtn + "( " + pins.get(i).compName + " " + pins.get(i).pinName + " ) \n";
        }

        rtn = rtn + "\tROUTED\n";

        Integer maxZ= 0;
        for (Integer i =0; i < paths.size(); i++){
            if (paths.get(i).z > maxZ){
                maxZ =paths.get(i).z;
            }
        }

        for (Integer k =0; k <=maxZ; k++){
            boolean firstTime = true;
            for (int i =0; i < paths.size(); i++){
                if (!paths.get(i).isVIA && paths.get(i).z == k){
                    if(firstTime){
                        rtn = rtn + "metal" + k.toString() + " ";
                        firstTime = false;
                    }
                    rtn = rtn +paths.get(i).getAsString();
                }
            }
            if(!firstTime){
                rtn = rtn +"\n";
            }
        }

        return rtn + "\n";
    }
}
