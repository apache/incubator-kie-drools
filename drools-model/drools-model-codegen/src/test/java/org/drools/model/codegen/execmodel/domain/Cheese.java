package org.drools.model.codegen.execmodel.domain;

import java.io.Serializable;
import java.util.Date;


public class Cheese
    implements
    Serializable {

    public static final String STILTON = "stilton";

    public static final int BASE_PRICE = 10;

    private static final long serialVersionUID = 510l;
    private String            type;
    private int               price;
    private int               oldPrice;
    private Date              usedBy;
    private double            doublePrice;

    public Cheese() {

    }

    public Cheese(final String type) {
        super();
        this.type = type;
        this.price = 0;
    }

    public Cheese(final String type,
                  final int price) {
        super();
        this.type = type;
        this.price = price;
    }

    public Cheese(final String type,
                  final int price,
                  final int oldPrice ) {
        super();
        this.type = type;
        this.price = price;
        this.oldPrice = oldPrice;
    }

    public int getPrice() {
        return this.price;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setPrice(final int price) {
        this.price = price;
    }

    public String toString() {
        return "Cheese( type='" + this.type + "', price=" + this.price + " )";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + price;
        result = PRIME * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        final Cheese other = (Cheese) obj;
        if ( price != other.price ) return false;
        if ( type == null ) {
            if ( other.type != null ) return false;
        } else if ( !type.equals( other.type ) ) return false;
        return true;
    }

    public int getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    public Date getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(Date usedBy) {
        this.usedBy = usedBy;
    }

    public synchronized double getDoublePrice() {
        return doublePrice;
    }

    public synchronized void setDoublePrice(double doublePrice) {
        this.doublePrice = doublePrice;
    }



}
