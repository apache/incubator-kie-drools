package org.drools.base.factmodel.traits;

public interface CoreWrapper<K> extends TraitableBean<K,CoreWrapper<K>> {

    void init( K core );

    K getCore();

}
