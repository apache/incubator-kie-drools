package org.drools.core.factmodel.traits;


public interface InstantiatorFactory {

    public TraitableBean instantiate( Class<?> trait, Object id );

    public Object createId( Class<?> klass );
}
