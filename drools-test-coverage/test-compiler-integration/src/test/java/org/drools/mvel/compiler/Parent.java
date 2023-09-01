package org.drools.mvel.compiler;

public class Parent extends GrandParent {
    
    private GrandParent grandParent;

    public Parent() {
    }

    public Parent(final String name) {
        super( name );
    }

    /**
     * @return the parent
     */
    public GrandParent getGrandParent() {
        return grandParent;
    }

    /**
     * @param parent the parent to set
     */
    public void setGrandParent(GrandParent parent) {
        this.grandParent = parent;
    }

}
