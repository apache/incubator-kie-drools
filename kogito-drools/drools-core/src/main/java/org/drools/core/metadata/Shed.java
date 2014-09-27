package org.drools.core.metadata;

import org.drools.core.factmodel.traits.AbstractTraitFactory;

public interface Shed<K,T> extends WorkingMemoryTask<T> {

    public K getCore();

    public Class<T> getTrait();

    public Shed<K,T> setTraitFactory( AbstractTraitFactory factory );

}
