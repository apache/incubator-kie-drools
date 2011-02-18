package org.drools;

public class OuterFact {

    private String name;
    private Cheese innerFact;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Cheese getInnerFact() {
        return innerFact;
    }
    public void setInnerFact(Cheese innerFact) {
        this.innerFact = innerFact;
    }

}
