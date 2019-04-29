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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author owner
 */
public class Reader {
    static public final String PINS = "PINS", COMPONENTS = "COMPONENTS", NETS = "NETS", SPECNETS = "SPECIALNETS";
    static public final String DEF_EXT = ".def", LEF_EXT = ".lef" ;
    private String defFile, lefFile;

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

    public String getSection(String section , String ext) 
    {
        Matcher m ;
        if (ext.equals(this.DEF_EXT))
             m = Pattern.compile(section).matcher(this.defFile);
        else
             m = Pattern.compile(section).matcher(this.lefFile);
        
        if(m.find())
            System.out.println(m.group());
        
        return m.group();
    }
}
