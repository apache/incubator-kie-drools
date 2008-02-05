package org.drools.clips;

public interface Function {

    public abstract String getName();

    public abstract void dump(LispForm lispForm, Appendable appendable);

}