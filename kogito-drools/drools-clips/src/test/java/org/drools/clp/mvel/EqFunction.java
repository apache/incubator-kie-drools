package org.drools.clp.mvel;

public class EqFunction  extends BaseInfixFunction  {
    private static final String name = "eq";
    private static final String mappedSymbol = "==";
        
    public String getName() {
        return name;
    }

    public String getMappedSymbol() {
        return mappedSymbol;
    }
}
