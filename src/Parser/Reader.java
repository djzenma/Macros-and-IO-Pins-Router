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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author owner
 */
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

    public List<String> getSection(String section , String ext)
    {
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
    
    public Set getMacrosWOPins ()
    {
       Set <Macro> Macros_Set = new HashSet<Macro>();
       List<String[]> allMatches = new ArrayList<String[]>();
       
       List<String> matches = this.getSection(COMPONENTS+SECTION_REGEX, Reader.DEF_EXT);
       
       String match = matches.get(0);
       String [] comps = match.split("\n");
       
       for(String component : comps)
       {
         
                String[] spaceDelimited = component.split("\\s");
               /*for(String s : spaceDelimited)
                    System.out.println(s);*/
                if (spaceDelimited.length == 11)
                Macros_Set.add(new Macro(spaceDelimited[1], new Vector(Integer.parseInt(spaceDelimited[6]), Integer.parseInt(spaceDelimited[7]))));
           
       }
       for(Macro m : Macros_Set) {
           System.out.println(m);
       }
       
       return Macros_Set ;
    }
    
    public Set getMacrosWPins (HashSet<Macro> MacrosSet) {
        Set <Macro> Macros_Set = new HashSet<Macro>();
        MacrosSet.get()
    }
}
