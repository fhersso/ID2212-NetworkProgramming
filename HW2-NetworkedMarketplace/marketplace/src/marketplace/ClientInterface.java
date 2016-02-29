/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketplace;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Fhersso
 */
public interface ClientInterface extends Remote {

    public String getID() throws RemoteException;

    public void updateMarket(ArrayList<Item> itemList) throws RemoteException;

    public void notifyWish(Item item) throws RemoteException;

    //public void updateWish(Item item) throws RemoteException;

    public void notifyNoFunds() throws RemoteException;

    public void notifySold(String name, float price) throws RemoteException;

    public void updateBalance(float balance) throws RemoteException;
}
