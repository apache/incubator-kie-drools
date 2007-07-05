package org.drools;

public class Address {
    
    private String street;
    private String number;
    private String phone;
    
    public Address() {}
    
    public Address( String street, String number, String phone ) {
        this.street = street;
        this.number = number;
        this.phone  = phone;
    }
    
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String toString() {
        return "Address( "+this.street+", "+this.number+" - phone: "+this.phone+" )";
    }

}
