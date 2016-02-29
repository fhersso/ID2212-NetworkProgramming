/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Fhersso
 */
public class GameServerConnection implements Runnable {
    private final String host;
    private final int port;
    private final GameClient gui;
    private final LinkedBlockingQueue<String> strings = new LinkedBlockingQueue<>();
    private BufferedReader in;
    private BufferedWriter out;

    GameServerConnection(GameClient gui, String host, int port)
    {
        this.host = host;
        this.port = port;
        this.gui = gui;
    }
    
    @Override
    public void run()
    {
        connect();
        while(true){
            callGameServer();
        }
    }

    void connect()
    {
        try
        {
            Socket clientSocket = new Socket(host, port);
            gui.connected();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); 
            gui.showResult(in.readLine());
            
        } catch (UnknownHostException e)
        {
            System.err.println("Don't know about host: " + host + ".");
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to: "
                    + host + ".");
            System.exit(1);
        }
        
        
    }
    
    void guiMessage(String text)
    {   
        strings.add(text);
    }
    
    void callGameServer()
    {   
        String result;
        String guess;
        try
        {
        guess = strings.take();
        if( guess.equals("new")){
            gui.newGame();
        }
        out.write(guess);out.newLine();out.flush();
        result=in.readLine();
        gui.showResult(result);
        if(result.contains("Game Over") || result.contains("You won") ){
                gui.gameOver();
            }
        } catch (InterruptedException | IOException e)
        {
            System.out.println("Failed" + e.getMessage());
        }
        
    }
}
