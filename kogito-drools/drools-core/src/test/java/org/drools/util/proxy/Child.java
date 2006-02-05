package org.drools.util.proxy;

import java.math.BigInteger;

public class Child {

    private BigInteger nappies;
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigInteger getNappies() {
        return nappies;
    }
    public void setNappies(BigInteger nappies) {
        this.nappies = nappies;
    }
    
    public Child(BigInteger nappies,
                 String name) {
        this.nappies = nappies;
        this.name = name;
    }
    
    Child() {};
    
    
    
    
}
