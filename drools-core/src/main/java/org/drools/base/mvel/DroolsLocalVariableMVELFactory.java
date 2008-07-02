package org.drools.base.mvel;

public interface DroolsLocalVariableMVELFactory {
    public void setLocalValue(final String identifier,
                              final Object value);
    
    public Object getLocalValue(final String identifier);

}
