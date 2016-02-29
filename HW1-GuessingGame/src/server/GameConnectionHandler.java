/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author Fhersso
 */
public class GameConnectionHandler implements Runnable{
    private final Socket clientSocket;
    private final NameGame game;

    /**
     * Creates anew instance.
     *
     * @param clientSocket This socket should be connected to a game client.
     */
    GameConnectionHandler(Socket clientSocket)
    {   game = new NameGame();
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); )
        {
            String result;
            String guessWord;
            while (true){
                //Start game
                result = game.newGame();
            
                out.write(result);out.newLine();out.flush(); //send the word to guess
                guessWord = in.readLine();
            
                while(!guessWord.equalsIgnoreCase("new")) { //Guess loop
                    result = game.playNameGame(guessWord);
                    out.write(result);out.newLine();out.flush();
                    guessWord = in.readLine();
                } 
            }
            
            
        } catch (Exception e)
        {
            System.out.println("oops");
        }
       
    }
    
}
