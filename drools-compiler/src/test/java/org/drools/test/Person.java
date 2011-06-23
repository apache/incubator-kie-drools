package org.drools.test;

import org.drools.definition.type.Position;

public abstract class Person {
    @Position(0)
    private String name;

    public Person() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }
 
    public Person(String name) {
        this.name = name;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }

}
