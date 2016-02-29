/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketplace;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Fhersso
 */
public interface Marketplace extends Remote {

    public void registerClient(ClientInterface client) throws RemoteException;

    public void unregisterClient(ClientInterface client) throws RemoteException;

    public void addItem(Item item) throws RemoteException;

    public void addWish(Item item) throws RemoteException;

    public void buyItem(String buyer, ClientInterface buyerInterface, String productName, float price, String seller) throws RemoteException;

}

