package org.drools.traits.core.metadata;

import org.drools.traits.core.factmodel.AbstractTraitFactory;

public interface Shed<K,T> extends WorkingMemoryTask<T> {

    public K getCore();

    public Class<T> getTrait();

    public Shed<K,T> setTraitFactory( AbstractTraitFactory factory );

}
