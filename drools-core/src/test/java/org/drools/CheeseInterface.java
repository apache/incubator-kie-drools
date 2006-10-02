package org.drools;

public interface CheeseInterface {

    public String getType();

    public int getPrice();

    /**
     * @param price the price to set
     */
    public void setPrice(final int price);

    /**
     * @param type the type to set
     */
    public void setType(final String type);

}