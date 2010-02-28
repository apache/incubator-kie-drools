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

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((phone == null) ? 0 : phone.hashCode());
        result = prime * result + ((street == null) ? 0 : street.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Address other = (Address) obj;
        if ( number == null ) {
            if ( other.number != null ) return false;
        } else if ( !number.equals( other.number ) ) return false;
        if ( phone == null ) {
            if ( other.phone != null ) return false;
        } else if ( !phone.equals( other.phone ) ) return false;
        if ( street == null ) {
            if ( other.street != null ) return false;
        } else if ( !street.equals( other.street ) ) return false;
        return true;
    }

}
