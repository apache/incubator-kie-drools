package org.drools.core.metadata;

import java.net.URI;

public interface MetaClass<T> extends Identifiable {

    public MetaProperty<T,?>[] getProperties();

    public int getPropertyIndex( MetaProperty propertyLiteral );

    public NewInstance<T> newInstance( Object id );
}
