package org.drools.core.metadata;

import org.drools.core.factmodel.traits.InstantiatorFactory;
import org.drools.core.util.BitMaskUtil;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class MetadataContainer<T extends Metadatable> implements Serializable {

	protected transient T target;

	public MetadataContainer( T metadatableObject ) {
		this.target = metadatableObject;
	}

    protected MetaClass metaClassInfo;

    public MetaClass<T> getMetaClassInfo() {
        return metaClassInfo;
    }

    protected T getTarget() {
        return target;
    }

    public void setTarget( T target ) {
        this.target = target;
    }

    public List<MetaProperty<T,?,?>> properties() {
        return Collections.unmodifiableList( Arrays.asList( getMetaClassInfo().getProperties() ) );
    }

    public String[] propertyNames() {
        String[] names = new String[ metaClassInfo.getProperties().length ];
        for ( int j = 0; j < metaClassInfo.getProperties().length; j++ ) {
            names[ j ] = metaClassInfo.getProperties()[ j ].getName();
        }
        return names;
    }

    protected <T,R> MetaProperty<T,R,?> getProperty( String name ) {
        for ( MetaProperty p : metaClassInfo.getProperties() ) {
            if ( p.getName().equals( name ) ) {
                return p;
            }
        }
        return null;
    }

    protected <T,R> MetaProperty getProperty( int index ) {
        return metaClassInfo.getProperties()[ index ];
    }

    public static URI getIdentifier( Object object ) {
        if ( object instanceof Identifiable ) {
            return ( (Identifiable) object ).getUri();
        } else if ( object instanceof MetadataHolder ) {
            return URI.create( createObjectIdentifier( ( (MetadataHolder) object ).get_().getMetaClassInfo().getUri().toString(), object ) );
        } else {
            return URI.create( createObjectIdentifier( object ) );
        }
    }

    protected static String createObjectIdentifier( Object target ) {
        return createObjectIdentifier( "urn:" + target.getClass().getPackage().getName(), target );
    }

    protected static String createObjectIdentifier( String classUri, Object target ) {
        StringBuilder sb = new StringBuilder();
        sb.append( classUri )
                .append( "/" )
                .append( target.getClass().getSimpleName() )
                .append( "/" )
                .append( System.identityHashCode( target ) );
        return sb.toString();
    }

}
