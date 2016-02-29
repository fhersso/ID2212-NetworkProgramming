/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Fhersso
 */
public class GameClient extends JPanel{
    private JButton connectButton;
    private JButton newGameButton;
    private JButton playButton;
    private JLabel resultLabel = new JLabel();
    private GameServerConnection connection;

    GameClient(){
        buildGui();
    }
    
    private void buildGui(){
        setLayout(new GridLayout(4, 1));
        add(createConnectPanel());
        add(createNewGamePanel());
        add(createGuessPanel());
        add(createResultPanel());
    }
        
    private Component createConnectPanel(){
        JPanel connectPanel = new JPanel();
        connectPanel.setBorder(new TitledBorder(new EtchedBorder(), "Connection"));

        connectPanel.add(new JLabel("Host:"));
        JTextField hostField = new JTextField("localhost");
        connectPanel.add(hostField);

        connectPanel.add(new JLabel("Port:"));
        JTextField portField = new JTextField("4445");
        connectPanel.add(portField);

        connectButton = new JButton("Connect");
        connectPanel.add(connectButton);
        connectButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                connectButton.setEnabled(false);
                connection = new GameServerConnection(GameClient.this, host, port);
                new Thread(connection).start();//multithreaded
            }
        });
        return connectPanel;
        
    }
        
    private Component createNewGamePanel(){
        JPanel newGamePanel = new JPanel();
        newGamePanel.setBorder(new TitledBorder(new EtchedBorder(), "Options"));
        newGamePanel.add(new JLabel("New Game/Re-start:"));
        
        newGameButton = new JButton("New Game");
        newGameButton.setEnabled(false);
        newGamePanel.add(newGameButton);
        
        newGameButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                connection.guiMessage("new");
            }
        });
        return newGamePanel;        
    }
        
    private Component createGuessPanel(){
        JPanel guessPanel = new JPanel();
        guessPanel.setBorder(new TitledBorder(new EtchedBorder(),
                "Game"));

        guessPanel.add(new JLabel("Guess the word:"));
        JTextField wordField = new JTextField(10);
        guessPanel.add(wordField);

        playButton = new JButton("Guess");
        playButton.setEnabled(false);
        guessPanel.add(playButton);
        playButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                connection.guiMessage(wordField.getText());
            }
        });
        return guessPanel;        
    }
    
    private Component createResultPanel(){
        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder(new EtchedBorder(), "Display"));
        resultLabel.setText("");
        resultPanel.add(resultLabel);
        return resultPanel;        
    }
    
    void connected()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                newGameButton.setEnabled(true);
                playButton.setEnabled(true);
            }
        });
    }
    
    void gameOver()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                playButton.setEnabled(false);
            }
        });
    }
    
    void newGame()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                playButton.setEnabled(true);
            }
        });
    }

    void showResult(String result)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                resultLabel.setText(result);
            }
        });
    }
    
        public static void main(String[] args)
    {
        JFrame frame = new JFrame("The Name Game");
        frame.setContentPane(new GameClient());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
