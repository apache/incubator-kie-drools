package org.drools.core.metadata;

import java.net.URI;
import java.util.Collection;

public interface MetaProperty<T,R,C> extends Comparable<MetaProperty<T,R,C>>, Identifiable {

    public int getIndex();

    public String getName();

    public URI getKey();

    public boolean isManyValued();

    public OneValuedMetaProperty<T,C> asFunctionalProperty();

    public <X extends Collection<R>> ManyValuedMetaProperty<T,R,X> asManyValuedProperty();

    public C get( T o );

}
