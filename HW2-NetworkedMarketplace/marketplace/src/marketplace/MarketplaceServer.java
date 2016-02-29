/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketplace;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Marketplace server
 *
 * @author Fhersso
 */
@SuppressWarnings("serial")
public class MarketplaceServer extends UnicastRemoteObject implements Marketplace {

    private static final int REGISTRY_PORT_NUMBER = 1080;
    private final List<ClientInterface> clientTable = new ArrayList<>();
    private final ArrayList<Item> itemList = new ArrayList<>();
    private final ArrayList<Item> wishList = new ArrayList<>();
    private Bank bankInterface;

    public MarketplaceServer() throws RemoteException, MalformedURLException {
        super();
        try {
            bankInterface = (Bank) Naming.lookup("rmi://localhost:1099/Nordea");
        } catch (NotBoundException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void registerClient(ClientInterface client) throws RemoteException {
        if (clientTable.contains(client)) {
            throw new RemoteException("client already registered");
        }
        clientTable.add(client);
        System.out.println("Welcome " + client.getID());
        if (itemList.size() > 0) {
            try {
                client.updateMarket(itemList);
            } catch (RemoteException ex) {
                Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void unregisterClient(ClientInterface client) throws RemoteException {
        if (!clientTable.contains(client)) {
            throw new RemoteException("client not registered");
        }
        clientTable.remove(client);
        System.out.println("bye " + client.getID());
        removeItem(itemList, client);
        removeItem(wishList, client);
        updateAll();
    }

    @Override
    public void addItem(Item item) throws RemoteException {
        itemList.add(item);
        System.out.println("New Item on Sale " + item.getName());
        updateAll();
        checkWishList(item);
    }

    @Override
    public void addWish(Item wish) throws RemoteException {
        if (!checkMarket(wish.getOwnerInterface(), wish)) {
            wishList.add(wish); //Maybe needs to check before adding to avoid duplicate
            System.out.println("Added to wish list "+wish.getName());
        }
    }

    private synchronized void updateAll() {
        for (ClientInterface client : clientTable) {
            try {
                client.updateMarket(itemList);
            } catch (RemoteException ex) {
                Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean checkMarket(ClientInterface wisher, Item item) throws RemoteException {
        boolean bl = false;
        //System.out.println(itemList.size());
        for (int i = 0; i < itemList.size(); i++) {
            if (item.getName().equals(itemList.get(i).getName())) {
                if (item.getPrice() >= itemList.get(i).getPrice()) {
                    wisher.notifyWish(itemList.get(i));
                    //wisher.updateWish(item);
                    bl = true;

                }
            }
        }
        return bl;
    }

    private void checkWishList(Item item) throws RemoteException {
        //System.out.println(wishList.size());
        for (int i = 0; i < wishList.size(); i++) {
            if (item.getName().equals(wishList.get(i).getName())) {
                if (item.getPrice() <= wishList.get(i).getPrice()) {
                    wishList.get(i).getOwnerInterface().notifyWish(item);
                    //wishList.get(i).getOwnerInterface().updateWish(item);
                    wishList.remove(i);
                    --i;
                }
            }
        }
    }

    private void removeItem(ArrayList<Item> list, ClientInterface client) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getOwnerInterface().equals(client)) {
                list.remove(i);
                --i;
            }
        }
    }

    @Override
    public void buyItem(String buyer, ClientInterface buyerInterface, String productName, float price, String seller) throws RemoteException {
        Account buyerAcc = bankInterface.getAccount(buyer);
        Account sellerAcc = bankInterface.getAccount(seller);
        try {
            for (int i = 0; i < itemList.size(); i++) {
                if (itemList.get(i).getName().equals(productName) && itemList.get(i).getPrice() == price && itemList.get(i).getOwner().equals(seller)) {
                    ClientInterface sellerInterface = itemList.get(i).getOwnerInterface();                
                    buyerAcc.withdraw(price);
                    sellerAcc.deposit(price);
                    sellerInterface.notifySold(productName, price);
                    buyerInterface.updateBalance(buyerAcc.getBalance());
                    sellerInterface.updateBalance(sellerAcc.getBalance());
                    itemList.remove(i);
                    --i;
                    break;
                }
            }
            updateAll();

        } catch (RejectedException ex) {
            buyerInterface.notifyNoFunds();
        }
    }

    public static void main(String[] args) throws NotBoundException {
        try {
            try {
                LocateRegistry.getRegistry(REGISTRY_PORT_NUMBER).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);
            }

            Naming.rebind("rmi://localhost:1080/marketplace", new MarketplaceServer());
            System.out.println("Market is ready.");

        } catch (RemoteException | MalformedURLException re) {
            System.out.println(re);
            System.exit(1);
        }
    }
}
