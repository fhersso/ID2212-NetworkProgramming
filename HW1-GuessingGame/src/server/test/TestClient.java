/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author Fhersso
 */
public class TestClient {

    public static void main(String[] args) throws IOException
    {
        Socket clientSocket = null;

        try
        {
            clientSocket = new Socket("localhost", 4445);
        } catch (UnknownHostException e)
        {
            System.err.println("Don't know about host: " + ".");
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for " +
                    "the connection to: " +  "");
            System.exit(1);
        }



        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));)
        { 


        System.out.println(in.readLine());
        out.write("x");out.newLine();out.flush();
        System.out.println(in.readLine());
        out.write("x");out.newLine();out.flush();
        System.out.println(in.readLine());
        out.write("x");out.newLine();out.flush();
        System.out.println(in.readLine());
        out.write("x");out.newLine();out.flush();
        System.out.println(in.readLine());
        out.write("x");out.newLine();out.flush();
        System.out.println(in.readLine());
        out.write("x");out.newLine();out.flush();
        System.out.println(in.readLine());
        out.write("new");out.newLine();out.flush();
        System.out.println(in.readLine());
        System.out.println(in.readLine());

        out.close();
        in.close();
        clientSocket.close();
            
        } catch (IOException e)
        {
            System.out.println(e.toString());
            System.exit(1);
        }

    }
}