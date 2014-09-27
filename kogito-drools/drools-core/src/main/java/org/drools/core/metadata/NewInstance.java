package org.drools.core.metadata;

import org.drools.core.factmodel.traits.InstantiatorFactory;

public interface NewInstance<T> extends WorkingMemoryTask<T> {

    public boolean isInterface();

    public Object callUntyped();

    public InstantiatorFactory getInstantiatorFactory();

    public NewInstance<T> setInstantiatorFactory( InstantiatorFactory factory );

    public Modify getInitArgs();

    public Class<T> getInstanceClass();
}
