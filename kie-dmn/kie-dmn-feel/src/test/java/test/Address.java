package test;

import org.kie.dmn.feel.lang.FEELProperty;

public class Address {
    private String streetName;

    public Address(String streetName) {
        super();
        this.streetName = streetName;
    }

    @FEELProperty("street name")
    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Address [streetName=").append(streetName).append("]");
        return builder.toString();
    }
    
}
