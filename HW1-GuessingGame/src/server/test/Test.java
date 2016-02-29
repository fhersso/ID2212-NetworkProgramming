/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.test;

import server.NameGame;

/**
 *
 * @author Fhersso
 */
public class Test {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        NameGame game = new NameGame();
        System.out.println(game.newGame());
        System.out.println(game.newGame());
        System.out.println(game.playNameGame("x"));
        System.out.println(game.playNameGame("x"));
        System.out.println(game.playNameGame("x"));
        System.out.println(game.playNameGame("x"));
        System.out.println(game.playNameGame("x"));

    }
    
}
