package org.drools.decisiontable;

public class Cheese {
    private final String type;
    private final double price;

    public Cheese(String type, double price) {
        super();
        this.type = type;
        this.price = price;
    }

    public double getPrice() {
        return this.price;
    }

    public String getType() {
        return this.type;
    }
}
