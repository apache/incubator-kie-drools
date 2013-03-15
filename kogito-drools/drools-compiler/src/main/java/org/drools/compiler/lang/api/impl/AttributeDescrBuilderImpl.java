package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.AttributeDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.descr.AttributeDescr;

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

    public AttributeDescrBuilder<P> type( AttributeDescr.Type type ) {
        descr.setType( type );
        return this;
    }

}
