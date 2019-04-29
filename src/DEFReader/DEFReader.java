/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DEFReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author owner
 */
public class DEFReader {
    private final String PINS, COMPONENTS, NETS, SPECNETS;
    private final String REGEX;

    public DEFReader() {
        this.REGEX = "\\s+.+\\n(.+\\n)+";
        this.PINS = "PINS";
        this.COMPONENTS = "COMPONENTS";
        this.NETS = "NETS";
        this.SPECNETS = "SPECNETS";
    }
    
    private void readDEFFile(String name) {
        // FileReader reads text files in the default encoding.
            FileReader fileReader;
        try {
            fileReader = new FileReader(name);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DEFReader.class.getName()).log(Level.SEVERE, null, ex);
        }

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }   

            // Always close files.
            bufferedReader.close(); 
    }
    
    
}
