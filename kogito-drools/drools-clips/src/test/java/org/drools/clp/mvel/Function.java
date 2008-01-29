package org.drools.clp.mvel;

public interface Function {

    public abstract String getName();

    public abstract void dump(LispForm lispForm, Appendable appendable, MVELBuildContext context);

}