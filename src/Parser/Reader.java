/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Reader {
    static public final String DEF_EXT = ".def", LEF_EXT = ".lef" ;
    private String defFile, lefFile;
    private final String DIEAREA_REGEX = "DIEAREA.+" ;
    private final String SECTION_REGEX = "\\s+.+\\n(.+\\n)+";
    private final String  SITE_REGEX = "SITE\\s+core\\n(.+\\n)+END" ;
    //private final String  OBS_REGEX = "OBS (\n.+)+END";
    private final String  MACRO_REGEX = "MACRO.+(\\n.+)+";
    static public final String PINS = "PINS", COMPONENTS = "COMPONENTS", NETS = "NETS", SPECNETS = "SPECIALNETS";
    

    public Reader() {
    }
    
    public String readFile(String name , String ext) {
        String line;
        StringBuilder file = new StringBuilder();
        // FileReader reads text files in the default encoding.
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(name);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Reader.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader =
            new BufferedReader(fileReader);

        try {
            while((line = bufferedReader.readLine()) != null) {
                file.append('\n');
                file.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ext.equals(this.DEF_EXT))
            this.defFile = file.toString();
        else
            this.lefFile = file.toString();
     
        return file.toString();
    }

    public List<String> getSection(String section , String ext) {
        List<String> allMatches = new ArrayList<String>();
        String file;
        Matcher m ;

        if (ext.equals(DEF_EXT))
            file = this.defFile;
        else
            file = this.lefFile;

        m = Pattern.compile(section).matcher(file);
        while (m.find())
            allMatches.add(m.group());

       /* for (String x : allMatches) {
            System.out.println(x);
        }*/
        
        return allMatches;
    }

    /*
     *  @return Hash Table with all the MACROS placed from the DEF File
     */
    public Hashtable<String, Macro> getPlacedMacros() {
       Hashtable < String , Macro> macrosTable = new Hashtable<>();

       List<String> matches = this.getSection(COMPONENTS+SECTION_REGEX, Reader.DEF_EXT);
       
       String match = matches.get(0);
       String [] comps = match.split("\n");
       
       for(String component : comps) {
         
                String[] spaceDelimited = component.split("\\s");
                if (spaceDelimited.length == 11)
                    macrosTable.put(spaceDelimited[1], new Macro(spaceDelimited[2], new Vector(Integer.parseInt(spaceDelimited[6]), Integer.parseInt(spaceDelimited[7]))));
           
       }
       
       return macrosTable ;
    }

    /*
     *  @return Set with all the Library MACROS defined in the LEF File
     */
    public Set getMacrosSet() {
        Set <Macro> macrosSet = new HashSet<>();

        List<String> lefMacros = this.getSection(MACRO_REGEX, Reader.LEF_EXT);  // All Macros
        // Iterate over all Macros
        lefMacros.forEach(s -> {
            // Extract Macro Name
            String macroName;
            macroName = regexMatcher("MACRO.+", s).get(0).replaceAll("(MACRO|\\s+)", "");
            System.out.println(macroName);

            // Extract All the Pins block of each Macro
            StringBuilder allPins = new StringBuilder();
            regexMatcher("PIN\\s.+\\n(.+\\n)+\\s+END\\s+.+\\n", s).forEach(allPins::append);

            // Extract Each Pins separately from the PINS Block
            ArrayList<Pin> macroPins = new ArrayList<>();
            for (String pin: allPins.toString().split("PIN")) {
                if(!pin.isEmpty()) {
                    List<Rect> pinRects = new ArrayList<>();

                    String pinName =  pin.split("\\n")[0].replaceAll(" ", "");
                    // Get the Port of the individual Pin
                    String port = pin.split("PORT")[1];

                    // Get the rectangles of the port
                    String rectDimensions = port.split("LAYER.+")[1].replaceAll("(END.+|END\\n|;|\\n)", "").replaceAll("\\s+R", " R");

                    float x1 = -1000000,x2 = -1000000,y1=-1000000,y2= -1000000;
                    for (String rect: rectDimensions.split("RECT")) {
                        if(!rect.isEmpty() && !rect.equals(" ")) {
                            int count = 0;
                            for (String coord : rect.split(" ")) {
                                if(!coord.isEmpty()) {
                                    float num = Float.parseFloat(coord);
                                    if(count == 0)
                                        x1 = num;
                                    else if(count == 1)
                                        y1 = num;
                                    else if(count == 2)
                                        x2 = num;
                                    else
                                        y2 = num;
                                    count++;
                                }
                            } // End of Each coordinate in a Rect loop
                            System.out.println("Coords " + x1 + y1 + x2 + y2);
                            pinRects.add(new Rect(new Vector(x1, y1), new Vector(x2, y2)));
                        }
                    } // End of Each Rect in a Port loop
                    macroPins.add(new Pin(pinRects, pinName));
                }

            } // End Each Pin Loop
            macrosSet.add(new Macro(macroName, new Vector(0,0,0), macroPins));
        });

        return macrosSet;
    }


    private List<String> regexMatcher(String regex, String matcher) {
        List<String> matches = new ArrayList<>();
        Matcher m = Pattern.compile(regex).matcher(matcher);
        while(m.find())
            matches.add(m.group());

        return matches;
    }

}
