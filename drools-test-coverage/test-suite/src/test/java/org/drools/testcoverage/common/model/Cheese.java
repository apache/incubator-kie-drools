package org.drools.testcoverage.common.model;

import java.io.Serializable;

public class Cheese implements Serializable {

    private static final long serialVersionUID = -6959012349000689087L;

    public static final String STILTON = "stilton";

    public static final int BASE_PRICE = 10;

    private String type;
    private int price;
    private int oldPrice;

    public Cheese() {
    }

    public Cheese(final String type) {
        super();
        this.type = type;
    }

    public Cheese(final String type, final int price) {
        super();
        this.type = type;
        this.price = price;
    }

    public Cheese(final String type, final int price, final int oldPrice) {
        this.type = type;
        this.price = price;
        this.oldPrice = oldPrice;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(final int oldPrice) {
        this.oldPrice = oldPrice;
    }

    @Override
    public String toString() {
        return "Cheese [type=" + type + ", price=" + price + ", oldPrice=" + oldPrice + "]";
    }

}
