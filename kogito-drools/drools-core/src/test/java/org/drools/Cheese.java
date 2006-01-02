/**
 * 
 */
package org.drools;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class Cheese {
    private String type;

    private int    price;

    public Cheese(String type,
                  int price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return this.type;
    }

    public int getPrice() {
        return this.price;
    }
    
    public String toString() {
        return "Cheese type='" + this.type + "' price='" + this.price + "'";
    }

    public boolean equals(Object object) {
        if ( object == null || ! ( object instanceof Cheese) ) {
            return false;
        }
        
        Cheese other = ( Cheese ) object;
        
        return (this.type.equals( other.getType() ) && this.price == other.getPrice() );
    }

    public static int getIndex(Class clazz,
                               String name) throws IntrospectionException {
        PropertyDescriptor[] descriptors = Introspector.getBeanInfo( clazz ).getPropertyDescriptors();
        for ( int i = 0; i < descriptors.length; i++ ) {
            if ( descriptors[i].getName().equals( name ) ) {
                return i;
            }
        }
        return -1;
    }
}