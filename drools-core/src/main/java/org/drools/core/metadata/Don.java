package org.drools.core.metadata;

import org.drools.core.factmodel.traits.AbstractTraitFactory;

public interface Don<K,T> extends WorkingMemoryTask<T> {

    public K getCore();

    public Class<T> getTrait();

    public Don<K,T> setTraitFactory( AbstractTraitFactory factory );

    public Modify getInitArgs();

}
