/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

/**
 *
 * @author owner
 */
public class Track {
    public Integer number;
    public boolean direction;
    
    public static boolean X = true;
    public static boolean Y = false;
    
    
    Track(Integer num, boolean direction) {
        this.number = num;
        this.direction = direction;
    }
}
