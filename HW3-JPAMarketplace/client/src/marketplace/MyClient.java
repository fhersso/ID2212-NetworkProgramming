/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketplace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Fhersso
 */
@SuppressWarnings("serial")
public class MyClient extends UnicastRemoteObject implements ClientInterface {

    private String clientName;
    private String password;
    private final Marketplace market;
    private final Bank bankInterface;
    private Account account;
    private final MyClientGUI gui;
    private static MyClient me;

    public MyClient(Marketplace market, Bank bank) throws RemoteException {
        super();
        this.market = market;
        this.bankInterface = bank;
        this.me = this;
        this.gui = new MyClientGUI();
        gui.buyButton.setEnabled(false);
        gui.unregisterButton.setEnabled(false);
        gui.sellButton.setEnabled(false);
        gui.wishButton.setEnabled(false);
        gui.logoutButton.setEnabled(false);
        gui.recordsButton.setEnabled(false);

        JFrame frame = new JFrame("Marketplace");
        frame.setContentPane(this.gui);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        setUp();

    }

    public void setUp() {

        gui.registerButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clientName = gui.userName.getText();
                    password = gui.password.getText();
                    if (password.length() == 8) {
                        if (market.registerClient(me)) {
                            account = bankInterface.newAccount(clientName);
                            account.deposit(1000);
                            gui.buyButton.setEnabled(true);
                            gui.unregisterButton.setEnabled(true);
                            gui.sellButton.setEnabled(true);
                            gui.wishButton.setEnabled(true);
                            gui.registerButton.setEnabled(false);
                            gui.loginButton.setEnabled(false);
                            gui.logoutButton.setEnabled(true);
                            gui.recordsButton.setEnabled(true);
                        }
                    } else {
                        gui.messageLabel.append("\n>>Password most be size 8");
                    }

                } catch (RejectedException | RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        gui.loginButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    clientName = gui.userName.getText();
                    password = gui.password.getText();
                    if (password.length() == 8) {
                        if (market.loginClient(me)) {
                            account = bankInterface.getAccount(clientName);
                            gui.buyButton.setEnabled(true);
                            gui.unregisterButton.setEnabled(true);
                            gui.sellButton.setEnabled(true);
                            gui.wishButton.setEnabled(true);
                            gui.registerButton.setEnabled(false);
                            gui.loginButton.setEnabled(false);
                            gui.logoutButton.setEnabled(true);
                            gui.recordsButton.setEnabled(true);
                        }
                    } else {
                        gui.messageLabel.append("\n>>Password most be size 8");
                    }

                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        //Unregister from market
        gui.unregisterButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bye");
                try {
                    market.unregisterClient(me);
                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                gui.buyButton.setEnabled(false);
                gui.unregisterButton.setEnabled(false);
                gui.sellButton.setEnabled(false);
                gui.wishButton.setEnabled(false);
                gui.registerButton.setEnabled(true);
                gui.loginButton.setEnabled(true);
                gui.logoutButton.setEnabled(false);
                gui.recordsButton.setEnabled(false);
                gui.marketLabel.setText("");
                gui.messageLabel.setText("");
            }

        });

        gui.logoutButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Bye");
                try {
                    market.logoutClient(me);
                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                gui.buyButton.setEnabled(false);
                gui.unregisterButton.setEnabled(false);
                gui.sellButton.setEnabled(false);
                gui.wishButton.setEnabled(false);
                gui.registerButton.setEnabled(true);
                gui.loginButton.setEnabled(true);
                gui.logoutButton.setEnabled(false);
                gui.recordsButton.setEnabled(false);
                gui.marketLabel.setText("");
                gui.messageLabel.setText("");
            }

        });
        
        gui.recordsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    market.getClientRecord(me);
                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });

        //Sell Item
        gui.sellButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String product = gui.itemName.getText();
                float price = Float.parseFloat(gui.priceName.getText());
                System.out.println(product + " " + price);
                Item item = new Item(product, price, clientName);
                try {
                    market.addItem(item);
                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        //Wish item    
        gui.wishButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String product = gui.itemName.getText();
                float price = Float.parseFloat(gui.priceName.getText());
                System.out.println(product + " " + price);
                Item item = new Item(product, price, clientName);
                try {
                    market.addWish(item);
                    gui.messageLabel.append("\nAdded to your WishList: " + product + " " + price);
                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        //Buy Item
        gui.buyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String productName = gui.itemNameBuy.getText();
                float price = Float.parseFloat(gui.priceNameBuy.getText());
                String owner = gui.sellerNameBuy.getText();
                try {
                    market.buyItem(clientName, me, productName, price, owner);
                } catch (RemoteException ex) {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
    }

    @Override
    public String getID() {
        return clientName;
    }

    @Override
    public void updateMarket(ArrayList<Item> itemList) throws RemoteException {
        String marketString = "";
        for (int i = 0; i < itemList.size(); i++) {
            marketString += ("Item: " + itemList.get(i).getName()
                    + " Price: " + String.valueOf(itemList.get(i).getPrice())
                    + " Seller: " + itemList.get(i).getOwner() + '\n');
        }
        gui.marketLabel.setText(marketString);
    }

    @Override
    public void notifyWish(Item item) throws RemoteException {
        String message = "\nYour wished item is here: "
                + item.getName() + " " + String.valueOf(item.getPrice()) + " " + item.getOwner();
        //System.out.println(message);
        gui.messageLabel.append(message);
    }

    @Override
    public void notifyNoFunds() throws RemoteException {
        gui.messageLabel.append("\nNot enough money");
        //System.out.println(" Not enough money");
    }

    @Override
    public void notifySold(String name, float price) throws RemoteException {
        gui.messageLabel.append("\nItem sold: " + name + ", " + String.valueOf(price));
    }

    @Override
    public void updateBalance(float balance) throws RemoteException { //Gui balance
        gui.messageLabel.append("\nBalance: " + String.valueOf(balance));
    }

    @Override
    public void message(String m) throws RemoteException { //Gui balance
        gui.messageLabel.append("\n>>" + m);
    }

    @Override
    public String getPassword() throws RemoteException {
        return this.password;
    }

    public static void main(String args[]) throws RemoteException,
            NotBoundException, MalformedURLException {
        Marketplace market = (Marketplace) Naming.lookup("rmi://localhost:1080/marketplace");
        Bank bankInterface = (Bank) Naming.lookup("rmi://localhost:1099/Nordea");
        MyClient me = new MyClient(market, bankInterface);

    }

}
