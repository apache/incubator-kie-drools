package org.drools.lang.api.impl;

import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.AttributeDescr.Type;

public class AttributeDescrBuilderImpl<P extends DescrBuilder< ? , ? >> extends BaseDescrBuilderImpl<P, AttributeDescr>
    implements
    AttributeDescrBuilder<P> {

    public AttributeDescrBuilderImpl(P parent,
                                     String name) {
        super( parent,
               new AttributeDescr( name ) );
    }

    public AttributeDescrBuilderImpl<P> value( String value ) {
        descr.setValue( value );
        return this;
    }

    public AttributeDescrBuilder<P> type( Type type ) {
        descr.setType( type );
        return this;
    }

}
