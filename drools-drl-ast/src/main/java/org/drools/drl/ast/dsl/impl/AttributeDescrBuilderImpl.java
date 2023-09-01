package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.AttributeDescrBuilder;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.descr.AttributeDescr;

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
