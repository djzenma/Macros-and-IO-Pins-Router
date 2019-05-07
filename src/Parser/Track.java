/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parser;

public class Track {
    public Integer number;
    public boolean direction;
    public int step;
    public int start;
    public int end;
    public String name;

    public static boolean X = true;
    public static boolean Y = false;

    
    Track(String name, Integer num, boolean direction, int step, int start) {
        this.name = name;
        this.number = num;
        this.direction = direction;
        this.step = step;
        this.start = start;
        this.end = this.start + (this.number-1) * this.step;
    }
}
