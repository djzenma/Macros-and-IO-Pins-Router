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
    private final String REGEX;
    private String defFile;

    public Reader() {
        this.REGEX = "\\s+.+\\n(.+\\n)+";
    }
    
    public String readFile(String name) {
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
        this.defFile = file.toString();
        return file.toString();
    }

    public String getSection(String section) {
        Matcher m = Pattern.compile(section + this.REGEX).matcher(this.defFile);
        if(m.find())
            System.out.println(m.group());
        return m.group();
    }
}
