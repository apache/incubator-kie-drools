/**
 * 
 */
package org.drools.decisiontable;

public class Cheese {
    private String type;
    private int    price;

    public Cheese(String type,
                  int price) {
        super();
        this.type = type;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }

}