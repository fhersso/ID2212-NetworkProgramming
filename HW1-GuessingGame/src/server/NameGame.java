/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import org.apache.commons.lang3.StringUtils;
/**
 *
 * @author Fhersso
 */
public class NameGame {
    private int score;
    private int attemptCounter;
    private final Reader dictionary;
    private String secretWord;
    private String guessWord;
    
    public NameGame(){
        score=0;
        attemptCounter=10;
        dictionary= new Reader();
    }
    
    public int getScore(){
        return score;
    }
    
    public int getAttemptCounter(){
        return attemptCounter; 
    }
    
    public String newGame(){
        secretWord = dictionary.getWord();
        guessWord = StringUtils.repeat("-", secretWord.length());
        attemptCounter=10;
        return "Guess the word: " + guessWord + " Tries: " + attemptCounter + " - Total Score " + score;
    }
    
    private String singleGuess(String guess){
        //Single char guess            
            if(StringUtils.containsIgnoreCase(secretWord, guess)){
                guessWord = addGuessChar(guess);
                if(StringUtils.equalsIgnoreCase(guessWord, secretWord))
                    return wordGuess(guessWord);
                else
                    return "Correct! " + guessWord + " Tries: " + attemptCounter + " - Total Score " + score;
            }else {
                attemptCounter-=1;
                if (attemptCounter==0){
                    return "Game Over - The word was: " + secretWord+ " - Total Score " + score;
                }else{
                    return "Wrong! " + guessWord + " Tries: " + attemptCounter + " - Total Score " + score;
                }
            }
    }
    
    private String wordGuess(String guess){
        //word guess            
            if(StringUtils.equalsIgnoreCase(secretWord, guess)){
                score+=1;
                attemptCounter=0;
                return "You won! " + secretWord + " - Total score: " + score;
            }else {
                attemptCounter-=1;
                if (attemptCounter==0){
                    return "Game Over - The word was: " + secretWord+ " - Total Score " + score;
                }else{
                    return "Wrong! " + guessWord + " Tries: " + attemptCounter + " - Total Score " + score;
                }
            }
    }
    
    private String addGuessChar(String guess){
        StringBuilder gWord = new StringBuilder(guessWord);
        for( int i = 0; i <= guessWord.length()-1; i++) {           
        //Do your stuff here
            if(StringUtils.equalsIgnoreCase(secretWord.substring(i, i+1), guess.substring(0, 1)))
            {
                gWord.replace(i, i+1, guess);
            }
        }
        guessWord = gWord.toString();
        return guessWord;
    }

    public String playNameGame(String s){
        if(attemptCounter>0){
            if(s.length()==1)
                return singleGuess(s);
            else if(s.length()==secretWord.length())
                return wordGuess(s);
            else
                return "Please guess a word or a letter" + " - Total Score " + score;
        }else{
            return "Game Over - The word was: " + secretWord+ " - Total Score " + score;
        }
    }
    
}
