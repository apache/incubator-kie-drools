package org.drools.clp.mvel;

public interface Function {

    public abstract String getName();

    public abstract void dump(LispForm2 lispForm, Appendable appendable);

}