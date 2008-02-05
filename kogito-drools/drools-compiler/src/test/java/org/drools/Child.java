package org.drools;

public class Child extends Parent {

    private Parent parent;
    
    public Child() {
    }

    public Child(final String name) {
        super( name );
    }

    /**
     * @return the parent
     */
    public Parent getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Parent parent) {
        this.parent = parent;
    }

}
