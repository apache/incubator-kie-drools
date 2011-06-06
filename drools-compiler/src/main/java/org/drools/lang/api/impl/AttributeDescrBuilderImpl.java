package org.drools.lang.api;

import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.AttributeDescr.Type;

public class AttributeDescrBuilderImpl extends BaseDescrBuilderImpl<AttributeDescr>
    implements
    AttributeDescrBuilder {

    public AttributeDescrBuilderImpl(String name) {
        super( new AttributeDescr( name ) );
    }

    public AttributeDescrBuilderImpl value( String value ) {
        descr.setValue( value );
        return this;
    }

    public AttributeDescrBuilder type( Type type ) {
        descr.setType( type );
        return this;
    }

}
