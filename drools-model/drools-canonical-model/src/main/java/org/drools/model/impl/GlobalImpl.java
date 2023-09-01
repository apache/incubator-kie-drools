package org.drools.model.impl;

import org.drools.model.Global;

public class GlobalImpl<T> extends VariableImpl<T> implements Global<T>, ModelComponent {
    private final String pkg;

    public GlobalImpl(Class<T> type, String pkg) {
        super(type);
        this.pkg = pkg;
    }

    public GlobalImpl(Class<T> type, String pkg, String name) {
        super(type, name);
        this.pkg = pkg;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        GlobalImpl global = ( GlobalImpl ) o;
        if (!getType().equals( global.getType() )) return false;
        if (!getName().equals( global.getName() )) return false;
        return pkg != null ? pkg.equals( global.pkg ) : global.pkg == null;
    }
}
