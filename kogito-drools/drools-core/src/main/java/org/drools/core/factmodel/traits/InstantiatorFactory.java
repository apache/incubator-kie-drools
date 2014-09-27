package org.drools.core.factmodel.traits;


public interface InstantiatorFactory {

    public TraitableBean instantiate( Class<? extends Thing> trait, Object id );

}
