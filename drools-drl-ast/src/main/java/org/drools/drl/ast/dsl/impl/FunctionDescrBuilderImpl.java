package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.FunctionDescrBuilder;
import org.drools.drl.ast.descr.FunctionDescr;

public class FunctionDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, FunctionDescr>
    implements
    FunctionDescrBuilder {

    protected FunctionDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new FunctionDescr() );
    }

    public FunctionDescrBuilder namespace( String namespace ) {
        descr.setNamespace( namespace );
        return this;
    }

    public FunctionDescrBuilder returnType( String type ) {
        descr.setReturnType( type );
        return this;
    }

    public FunctionDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public FunctionDescrBuilder body( String body ) {
        descr.setBody( body );
        return this;
    }

    public FunctionDescrBuilder parameter( String type,
                                           String variable ) {
        descr.addParameter( type,
                            variable );
        return this;
    }

    public FunctionDescrBuilder dialect( String dialect ) {
        descr.setDialect( dialect );
        return this;
    }
}
