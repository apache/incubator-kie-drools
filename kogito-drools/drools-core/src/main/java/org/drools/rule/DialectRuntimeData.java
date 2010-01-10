package org.drools.rule;


public interface DialectRuntimeData extends Cloneable {
    public void removeRule(Package pkg, Rule rule);

    public void removeFunction(Package pkg, Function function);

    public void merge(DialectRuntimeRegistry registry, DialectRuntimeData newData);

    public boolean isDirty();

    public void setDirty(boolean dirty);

    public void reload();

    public DialectRuntimeData clone(DialectRuntimeRegistry registry, DroolsCompositeClassLoader rootClassLoader);

    public void onAdd(DialectRuntimeRegistry dialectRuntimeRegistry,
                     DroolsCompositeClassLoader rootClassLoader);
    
    public void onRemove();
    
    public void onBeforeExecute();
}
