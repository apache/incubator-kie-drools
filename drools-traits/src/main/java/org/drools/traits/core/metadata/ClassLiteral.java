package org.drools.traits.core.metadata;


import java.io.Serializable;
import java.net.URI;
import java.util.List;

public abstract class ClassLiteral<T> implements MetaClass<T>, Serializable {

    protected MetaProperty<T,?,?>[] properties;
    protected List<String> propertyNames;
    protected URI key;

    public ClassLiteral( MetaProperty<T,?,?>[] propertyLiterals ) {
        this.properties = propertyLiterals;
        cachePropertyNames();
    }

    protected abstract void cachePropertyNames();

    @Override
    public MetaProperty<T,?,?>[] getProperties() {
        return properties;
    }

    public int getPropertyIndex( MetaProperty prop ) {
        return propertyNames.indexOf( prop.getName() );
    }

    public Object getId() {
        return getUri();
    }

    public abstract Class<T> getTargetClass();
}
