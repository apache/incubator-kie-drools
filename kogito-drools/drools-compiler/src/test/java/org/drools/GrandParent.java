package org.drools;

/**
 * A grand parent class
 * @author etirelli
 */
public class GrandParent {
    private String name;

    public GrandParent() {
    }
    
    public GrandParent( String name ) {
        this.name = name;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
