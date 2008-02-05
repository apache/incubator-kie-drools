package org.drools.rule;

public interface DialectData {    
    public void removeRule(Package pkg, Rule rule);
    
    public void removeFunction(Package pkg, Function function);
                
    public void merge(DialectData newData);
    
    public boolean isDirty();
    
    public void reload();
}
