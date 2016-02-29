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
import java.sql.*;
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

    /*DB*/
    private PreparedStatement registerClientStatement;
    private PreparedStatement unregisterClientStatement;
    private PreparedStatement findClientStatement;
    private PreparedStatement addItemStatement;
    private PreparedStatement removeItemStatement;
    private PreparedStatement removeAllItemStatement;
    private PreparedStatement AllItemStatement;
    private PreparedStatement addWishStatement;
    private PreparedStatement removeWishStatement;
    private PreparedStatement removeAllWishStatement;
    private PreparedStatement AllWishStatement;
    private PreparedStatement updateBuyerStatement;
    private PreparedStatement updateSellerStatement;

    public MarketplaceServer() throws RemoteException, MalformedURLException, ClassNotFoundException, SQLException {
        super();
        try {
            bankInterface = (Bank) Naming.lookup("rmi://localhost:1099/Nordea");
            Connection userConnection = createDatasource("USERS", " (name VARCHAR(32) PRIMARY KEY, password varchar(8), sell int, buy int)");
            prepareUserStatements(userConnection);
            Connection itemConnection = createDatasource("ITEMS", " (name VARCHAR(32) PRIMARY KEY, price FLOAT, owner VARCHAR(32))");
            prepareItemStatements(itemConnection);
            Connection wishConnection = createDatasource("WISHES", " (name VARCHAR(32) PRIMARY KEY, price FLOAT, owner VARCHAR(32))");
            prepareWishStatements(wishConnection);
            loadWishes();
            loadItems();

        } catch (NotBoundException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean registerClient(ClientInterface client) throws RemoteException {
        boolean r = false;
        try {

            String name;
            name = client.getID();
            String pass = client.getPassword();
            findClientStatement.setString(1, name);
            ResultSet result = findClientStatement.executeQuery();

            if (result.next()) { //if its already registered
                client.message("client already registered");
                result.close();
            } else {
                registerClientStatement.setString(1, name);
                registerClientStatement.setString(2, pass);
                registerClientStatement.setInt(3, 0);
                registerClientStatement.setInt(4, 0);
                int noOfAffectedRows = registerClientStatement.executeUpdate();
                System.out.println();
                System.out.println("data inserted in " + noOfAffectedRows + " row(s).");
                if (noOfAffectedRows > 0) {
                    clientTable.add(client);
                    System.out.println("Welcome " + client.getID());
                    if (itemList.size() > 0) {
                        client.updateMarket(itemList);
                    }
                    r = true;
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    @Override
    public boolean loginClient(ClientInterface client) throws RemoteException {
        boolean r = false;
        try {

            String name = client.getID();
            String pass = client.getPassword();
            findClientStatement.setString(1, name);
            ResultSet result = findClientStatement.executeQuery();

            if (result.next()) { //client found in db
                if (pass.equalsIgnoreCase(result.getString("password"))) {
                    clientTable.add(client);
                    System.out.println("Welcome " + client.getID());
                    client.message("Welcome back " + client.getID());
                    if (itemList.size() > 0) {
                        client.updateMarket(itemList);
                    }
                    r = true;
                } else {
                    client.message("wrong password");
                }
            } else {
                client.message("No user found");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r;
    }

    @Override
    public void logoutClient(ClientInterface client) throws RemoteException {
        if (!clientTable.contains(client)) {
            throw new RemoteException("client not registered");
        }
        System.out.println("bye " + client.getID());
        clientTable.remove(client);
    }

    @Override
    public void unregisterClient(ClientInterface client) throws RemoteException {
        if (!clientTable.contains(client)) {
            throw new RemoteException("client not registered");
        }
        System.out.println("bye " + client.getID());
        removeItem(itemList, client);
        removeItem(wishList, client);
        bankInterface.deleteAccount(client.getID());
        clientTable.remove(client);
        try {
            unregisterClientStatement.setString(1, client.getID());
            int noOfAffectedRows = unregisterClientStatement.executeUpdate();
            System.out.println();
            System.out.println("data deleted from " + noOfAffectedRows + " row(s)");

            removeAllItemStatement.setString(1, client.getID());
            int noOfAffectedRows2 = removeAllItemStatement.executeUpdate();
            System.out.println();
            System.out.println("data deleted from " + noOfAffectedRows2 + " row(s)");

            removeAllWishStatement.setString(1, client.getID());
            int noOfAffectedRows3 = removeAllWishStatement.executeUpdate();
            System.out.println();
            System.out.println("data deleted from " + noOfAffectedRows3 + " row(s)");

        } catch (SQLException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        updateAll();
    }

    @Override
    public void addItem(Item item) throws RemoteException {
        try {
            itemList.add(item);
            addItemStatement.setString(1, item.getName());
            addItemStatement.setDouble(2, item.getPrice());
            addItemStatement.setString(3, item.getOwner());
            int noOfAffectedRows = addItemStatement.executeUpdate();
            System.out.println("add item - data inserted in " + noOfAffectedRows + " row(s).");
            updateAll();
            checkWishList(item);
        } catch (SQLException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addWish(Item wish) throws RemoteException {
        try {
            if (!checkMarket(wish)) {
                wishList.add(wish);
                addWishStatement.setString(1, wish.getName());
                addWishStatement.setDouble(2, wish.getPrice());
                addWishStatement.setString(3, wish.getOwner());
                int noOfAffectedRows = addWishStatement.executeUpdate();
                System.out.println("add wish - data inserted in " + noOfAffectedRows + " row(s).");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
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

    private boolean checkMarket(Item item) throws RemoteException {
        boolean bl = false;
        for (int i = 0; i < itemList.size(); i++) {
            if (item.getName().equals(itemList.get(i).getName())) {
                if (item.getPrice() >= itemList.get(i).getPrice()) {

                    for (int j = 0; j < clientTable.size(); j++) {
                        if (item.getOwner().equals(clientTable.get(j).getID())) {
                            clientTable.get(j).notifyWish(itemList.get(i));
                            bl = true;
                            break;
                        }
                    }

                }
            }
        }
        return bl;
    }

    private void checkWishList(Item item) throws RemoteException {
        for (int i = 0; i < wishList.size(); i++) {
            if (item.getName().equals(wishList.get(i).getName())) {
                if (item.getPrice() <= wishList.get(i).getPrice()) {

                    for (int j = 0; j < clientTable.size(); j++) {
                        if (wishList.get(i).getOwner().equals(clientTable.get(j).getID())) {
                            clientTable.get(j).notifyWish(item);
                            break;
                        }
                    }
                    try {
                        removeWishStatement.setString(1, wishList.get(i).getOwner());
                        removeWishStatement.setString(2, wishList.get(i).getName());
                        int noOfAffectedRows = removeWishStatement.executeUpdate();
                        System.out.println("wish - data deleted from " + noOfAffectedRows + " row(s)");

                    } catch (SQLException ex) {
                        Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    wishList.remove(i);
                    --i;
                }
            }
        }
    }

    private void removeItem(ArrayList<Item> list, ClientInterface client) throws RemoteException {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getOwner().equals(client.getID())) {
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

                    buyerAcc.withdraw(price);
                    sellerAcc.deposit(price);

                    ClientInterface sellerInterface = null;

                    for (int j = 0; j < clientTable.size(); j++) {
                        if (itemList.get(i).getOwner().equals(clientTable.get(j).getID())) {
                            sellerInterface = clientTable.get(j);
                            sellerInterface.notifySold(productName, price);
                            sellerInterface.updateBalance(sellerAcc.getBalance());
                            break;
                        }
                    }

                    buyerInterface.updateBalance(buyerAcc.getBalance());

                    try {
                        removeItemStatement.setString(1, itemList.get(i).getOwner());
                        removeItemStatement.setString(2, itemList.get(i).getName());
                        int noOfAffectedRows = removeItemStatement.executeUpdate();
                        System.out.println("wish - data deleted from " + noOfAffectedRows + " row(s)");

                        updateBuyerStatement.setString(1, buyerInterface.getID());
                        int noOfAffectedRows2 = updateBuyerStatement.executeUpdate();
                        System.out.println("register - data deleted from " + noOfAffectedRows2 + " row(s)");

                        updateSellerStatement.setString(1, seller);
                        int noOfAffectedRows3 = updateSellerStatement.executeUpdate();
                        System.out.println("register - data deleted from " + noOfAffectedRows3 + " row(s)");

                    } catch (SQLException ex) {
                        Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
                    }

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

    public static void main(String[] args) throws NotBoundException, ClassNotFoundException, SQLException {
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

    /*DB*/
    private Connection createDatasource(String tableName, String query) throws ClassNotFoundException, SQLException {
        Class.forName("org.apache.derby.jdbc.ClientXADataSource");
        Connection connection = DriverManager.getConnection("jdbc:derby://localhost:1527/Marketplace;create=true");
        boolean exist = false;
        int tableNameColumn = 3;
        DatabaseMetaData dbm = connection.getMetaData();
        for (ResultSet rs = dbm.getTables(null, null, null, null); rs.next();) {
            if (rs.getString(tableNameColumn).equals(tableName)) {
                exist = true;
                rs.close();
                break;
            }
        }
        if (!exist) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE " + tableName
                    + query);
        }
        return connection;
    }

    private void prepareUserStatements(Connection connection) throws SQLException {
        registerClientStatement = connection.prepareStatement("INSERT INTO USERS VALUES (?, ?, ?, ?)");
        unregisterClientStatement = connection.prepareStatement("DELETE FROM USERS WHERE name = ?");
        findClientStatement = connection.prepareStatement("SELECT * from USERS WHERE name = ?");
        updateBuyerStatement = connection.prepareStatement("UPDATE USERS SET buy=buy+1 WHERE name=?");
        updateSellerStatement = connection.prepareStatement("UPDATE USERS SET sell=sell+1  WHERE name=?");
    }

    private void prepareItemStatements(Connection connection) throws SQLException {
        addItemStatement = connection.prepareStatement("INSERT INTO ITEMS VALUES (?, ?, ?)");
        removeItemStatement = connection.prepareStatement("DELETE FROM ITEMS WHERE (owner = ? and name = ?)");
        removeAllItemStatement = connection.prepareStatement("DELETE FROM ITEMS WHERE owner = ?");
        AllItemStatement = connection.prepareStatement("SELECT * from ITEMS");
    }

    private void prepareWishStatements(Connection connection) throws SQLException {
        addWishStatement = connection.prepareStatement("INSERT INTO WISHES VALUES (?, ?, ?)");
        removeWishStatement = connection.prepareStatement("DELETE FROM WISHES WHERE (owner = ? and name = ?)");
        removeAllWishStatement = connection.prepareStatement("DELETE FROM WISHES WHERE owner = ?");
        AllWishStatement = connection.prepareStatement("SELECT * from WISHES");

    }

    private void loadItems() throws SQLException {
        try (ResultSet result = AllItemStatement.executeQuery()) {
            for (int i = 1; result.next(); i++) {
                itemList.add(new Item(result.getString("name"), result.getFloat("price"), result.getString("owner")));
            }
        }
    }

    private void loadWishes() throws SQLException {
        try (ResultSet result = AllWishStatement.executeQuery()) {
            for (int i = 1; result.next(); i++) {
                wishList.add(new Item(result.getString("name"), result.getFloat("price"), result.getString("owner")));
            }
        }
    }

    @Override
    public void getClientRecord(ClientInterface client) throws RemoteException {

        try {
            String name = client.getID();
            String pass = client.getPassword();
            findClientStatement.setString(1, name);
            ResultSet result = findClientStatement.executeQuery();

            if (result.next()) { //client found in db
                client.message("*****User records: *****");
                client.message("Total items sold : " + result.getString("sell"));
                client.message("Total items bought : " + result.getString("buy"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MarketplaceServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
