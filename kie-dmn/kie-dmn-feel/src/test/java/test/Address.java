package test;

import org.kie.dmn.feel.lang.FEELProperty;

public class Address {
    private String streetName;
    private String zip;

    public Address(String streetName) {
        super();
        this.streetName = streetName;
    }
    
    public Address(String streetName, String zip) {
        super();
        this.streetName = streetName;
        this.zip = zip;
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

    public String getZip() {
        return zip;
    }
    
    public void setZip(String zip) {
        this.zip = zip;
    }
    
    
}
