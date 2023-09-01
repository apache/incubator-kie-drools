package org.drools.traits.core.factmodel;

import java.io.Serializable;
import java.util.BitSet;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.base.factmodel.traits.TraitableBean;

public class ThingProxyImplPlaceHolder<K> extends TraitProxyImpl implements Thing<K>,
                                                                            TraitType, Serializable {

    private static final long serialVersionUID = 6017272084020598391L;

    private transient static ThingProxyImplPlaceHolder singleton;

    public static ThingProxyImplPlaceHolder getThingPlaceHolder() {
        if ( singleton == null ) {
            singleton = new ThingProxyImplPlaceHolder();
        }
        return singleton;
    }

    public ThingProxyImplPlaceHolder() {
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