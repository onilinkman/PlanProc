/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package procesamientosinf151;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Scion
 */
public class ProcesamientosInf151 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Queue<String> s=new LinkedList();
        s.add("a");
        s.add("b");
        s.add("c");
        s.add("d");
        System.out.println(s);
        for(int i=0;i<3;i++){
            s.add(s.remove());
            System.out.println(s);
        }
        System.out.println(true||false);
    }

}
