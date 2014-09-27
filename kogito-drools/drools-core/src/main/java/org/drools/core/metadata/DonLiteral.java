package org.drools.core.metadata;

import org.drools.core.factmodel.traits.AbstractTraitFactory;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.util.BitMaskUtil;
import org.drools.core.util.StandaloneTraitFactory;

import java.net.URI;

public abstract class DonLiteral<K, T> implements Don<K,T> {
    protected K core;
    private URI key;
    protected AbstractTraitFactory factory;

    protected abstract MetaClass<T> getMetaClassInfo();

    public DonLiteral( K target ) {
        this.core = target;
    }

    public K getCore() {
        return core;
    }

    void setCore( K target ) {
        this.core = target;
    }

    @Override
    public KIND kind() {
        return KIND.DON;
    }

    @Override
    public Object getTargetId() {
        return MetadataContainer.getIdentifier( core );
    }

    public T call() {
        if ( getCore() == null ) {
            return null;
        }
        if ( factory != null && factory instanceof StandaloneTraitFactory ) {
            StandaloneTraitFactory stf = (StandaloneTraitFactory) factory;
            try {
                return (T) stf.don( getCore() instanceof TraitableBean ? ((TraitableBean) getCore()) : stf.makeTraitable( getCore(), getCore().getClass() ),
                                    getTrait() != null ? getTrait() : Thing.class );
            } catch ( LogicalTypeInconsistencyException e ) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException(  );
        }
        return null;
    }

    @Override
    public URI getUri() {
        if ( key == null ) {
            key = createURI();
        }
        return key;
    }

    @Override
    public Object getId() {
        return getUri();
    }

    protected URI createURI() {
        StringBuilder sb = new StringBuilder();
        sb.append( MetadataContainer.getIdentifier( core ) );

        sb.append( "?don=" ).append( getTrait().getName() );

        return URI.create( sb.toString() );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DonLiteral that = (DonLiteral) o;

        if ( !core.equals( that.core ) ) {
            return false;
        }
        if ( !getTrait().equals( that.getTrait() ) ) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return core.hashCode() ^ getTrait().hashCode();
    }

    @Override
    public Don<K,T> setTraitFactory( AbstractTraitFactory factory ) {
        this.factory = factory;
        return this;
    }

}
