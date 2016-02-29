/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Fhersso
 */
public class Reader {
    private BufferedReader words = null;
    private List<String> lines = new ArrayList<String>();
    private Random r;
    
    public Reader(){
        try{
            words = new BufferedReader(new FileReader("files/words.txt"));
            String line = words.readLine();
            while( line != null ) {
                lines.add(line);
                line = words.readLine();
            }
            r = new Random();
        }catch(IOException e){
            System.out.println("Error: File cannot be read");
            System.exit(0);
        }
    }
    
    public String getWord() {
        String word;
        do {
            word=lines.get(r.nextInt(lines.size()));
        }while(word.length()<5 );
        return word;
    }
}
