package org.drools.rule;

import org.drools.RuntimeDroolsException;
import org.drools.rule.JavaDialectRuntimeData.PackageClassLoader;

public interface DialectRuntimeData extends Cloneable {
    public void removeRule(Package pkg, Rule rule);

    public void removeFunction(Package pkg, Function function);

    public void merge(DialectRuntimeRegistry registry, DialectRuntimeData newData);

    public boolean isDirty();

    public void setDirty(boolean dirty);

    public void reload();

    public DialectRuntimeData clone(DialectRuntimeRegistry registry, CompositeClassLoader rootClassLoader);

    public void onAdd(DialectRuntimeRegistry dialectRuntimeRegistry,
                     CompositeClassLoader rootClassLoader);
    
    public void onRemove();
    
    public void onBeforeExecute();
}
