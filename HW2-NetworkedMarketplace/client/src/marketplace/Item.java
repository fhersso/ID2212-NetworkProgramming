/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marketplace;

import java.io.Serializable;

public class Item implements Serializable {

    private final String name;
    private final float Price;
    private final String owner;
    private final ClientInterface ownerInterface;

    public Item(String name, float price, String owner, ClientInterface ownerInterface) {
        this.name = name;
        this.Price = price;
        this.owner = owner;
        this.ownerInterface = ownerInterface;
    }

    public String getName() {
        return this.name;
    }

    public float getPrice() {
        return this.Price;
    }

    public String getOwner() {
        return this.owner;
    }

    public ClientInterface getOwnerInterface() {
        return this.ownerInterface;
    }
}
