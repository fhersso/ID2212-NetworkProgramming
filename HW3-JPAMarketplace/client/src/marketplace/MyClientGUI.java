/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketplace;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Fhersso
 */
public class MyClientGUI extends JPanel {

    public JButton loginButton;
    public JButton logoutButton;
    public JButton unregisterButton;
    public JButton registerButton;
    public JButton sellButton;
    public JButton wishButton;
    public JButton buyButton;
    public JButton recordsButton;
    public JTextArea messageLabel = new JTextArea();
    public JTextArea marketLabel = new JTextArea();
    public JTextField userName = new JTextField("Name");
    public JPasswordField password = new JPasswordField(8);
    public JTextField itemName = new JTextField("ItemName");
    public JTextField priceName = new JTextField("00.00");
    public JTextField itemNameBuy = new JTextField("ItemName");
    public JTextField priceNameBuy = new JTextField("00.00");
    public JTextField sellerNameBuy = new JTextField("Name");

    MyClientGUI() {
        buildGui();
    }

    private void buildGui() {
        //setLayout(new GridLayout(5, 1));
        add(createConnectPanel());
        add(createWishSellPanel());
        add(createBuyPanel());
        add(createMessagePanel());
        add(createMarketPanel());
        setPreferredSize(new Dimension(550, 700));
    }

    private Component createConnectPanel() {
        JPanel connectPanel = new JPanel();
        connectPanel.setBorder(new TitledBorder(new EtchedBorder(), "Marketplace"));
        connectPanel.add(new JLabel("User:"));
        connectPanel.add(userName);
        connectPanel.add(new JLabel("Pass:"));
        connectPanel.add(password);
        loginButton = new JButton("Login");
        connectPanel.add(loginButton);
        logoutButton = new JButton("Logout");
        connectPanel.add(logoutButton);
        registerButton = new JButton("Register");
        connectPanel.add(registerButton);
        unregisterButton = new JButton("Unregister");
        connectPanel.add(unregisterButton);
        recordsButton = new JButton("Records");
        connectPanel.add(recordsButton);
        connectPanel.setPreferredSize(new Dimension(500, 100));
        return connectPanel;
    }

    private Component createWishSellPanel() {
        JPanel wishSellPanel = new JPanel();
        wishSellPanel.setBorder(new TitledBorder(new EtchedBorder(), "Sell/Wish"));
        wishSellPanel.add(new JLabel("Item:"));
        wishSellPanel.add(itemName);
        wishSellPanel.add(new JLabel("Price:"));
        wishSellPanel.add(priceName);
        sellButton = new JButton("Sell");
        wishSellPanel.add(sellButton);
        wishButton = new JButton("Wish");
        wishSellPanel.add(wishButton);
        wishSellPanel.setPreferredSize(new Dimension(500, 60));
        return wishSellPanel;
    }

    private Component createBuyPanel() {
        JPanel buyPanel = new JPanel();
        buyPanel.setBorder(new TitledBorder(new EtchedBorder(), "Buy"));
        buyPanel.add(new JLabel("Item:"));
        buyPanel.add(itemNameBuy);
        buyPanel.add(new JLabel("Price:"));
        buyPanel.add(priceNameBuy);
        buyPanel.add(new JLabel("Seller:"));
        buyPanel.add(sellerNameBuy);
        buyButton = new JButton("Buy");
        buyPanel.add(buyButton);
        buyPanel.setPreferredSize(new Dimension(500, 60));
        return buyPanel;
    }

    private Component createMessagePanel() {
        JScrollPane messagePanel;
        messageLabel.setText("");
        messagePanel = new JScrollPane(messageLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        messagePanel.setBorder(new TitledBorder(new EtchedBorder(), "Messages"));
        messagePanel.setPreferredSize(new Dimension(500, 150));
        return messagePanel;
    }

    private Component createMarketPanel() {
        JScrollPane marketPanel;
        marketLabel.setText("");
        marketPanel = new JScrollPane(marketLabel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        marketPanel.setBorder(new TitledBorder(new EtchedBorder(), "Market"));
        marketPanel.setPreferredSize(new Dimension(500, 250));
        return marketPanel;
    }

    /*public static void main(String[] args) {
        JFrame frame = new JFrame("Marketplace");
        frame.setContentPane(new MyClientGUI());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }*/

}
