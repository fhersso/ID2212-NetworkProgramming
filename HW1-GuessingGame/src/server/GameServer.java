/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Fhersso
 */
public class GameServer {
    private static final int PORT = 4445;

    /**
     * Starts the reverse server.
     *
     * @param args No command line arguments are used.
     */
    public static void main(String[] args)
    {
        boolean listening = true;
        ServerSocket serverSocket;

        try
        {
            serverSocket = new ServerSocket(PORT);
            while (listening)
            {
                Socket clientSocket = serverSocket.accept();
                new Thread(new GameConnectionHandler(clientSocket)).start();
            }
            serverSocket.close();
        } catch (IOException e)
        {
            System.err.println("Could not listen on port: " + PORT);
            System.exit(1);
        }
    }    
}
