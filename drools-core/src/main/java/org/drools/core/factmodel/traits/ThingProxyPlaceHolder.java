package org.drools.core.factmodel.traits;

import java.io.Serializable;
import java.util.BitSet;

public class ThingProxyPlaceHolder<K> extends TraitProxy implements Thing<K>, TraitType, Serializable {

    private transient static ThingProxyPlaceHolder singleton;

    public static ThingProxyPlaceHolder getThingPlaceHolder() {
        if ( singleton == null ) {
            singleton = new ThingProxyPlaceHolder();
        }
        return singleton;
    }

    public ThingProxyPlaceHolder() {
        setTypeCode( new BitSet() );
    }

    @Override
    public boolean _isVirtual() {
        return true;
    }

    public K getCore() {
        return null;
    }

    public boolean isTop() {
        return true;
    }

    @Override
    public String _getTraitName() {
        return Thing.class.getName();
    }

    @Override
    public TraitableBean getObject() {
        return null;
    }

    @Override
    public boolean equals( Object o ) {
        return o == singleton;
    }

    @Override
    public int hashCode() {
        return Thing.class.hashCode() ^ 31;
    }
}