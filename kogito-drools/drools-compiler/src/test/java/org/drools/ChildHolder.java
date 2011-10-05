package org.drools;

public class ChildHolder {
    
    private Child child;

    public ChildHolder() {
        super();
    }

    public ChildHolder(Child child) {
        super();
        this.child = child;
    }

    /**
     * @return the child
     */
    public Child getChild() {
        return child;
    }

    /**
     * @param child the child to set
     */
    public void setChild( Child child ) {
        this.child = child;
    }
    
    

}
