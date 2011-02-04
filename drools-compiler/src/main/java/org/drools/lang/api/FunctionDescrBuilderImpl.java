package org.drools.lang.api;

import org.drools.lang.descr.FunctionDescr;

public class FunctionDescrBuilderImpl extends BaseDescrBuilderImpl<FunctionDescr>
    implements
    FunctionDescrBuilder {

    protected FunctionDescrBuilderImpl() {
        super( new FunctionDescr() );
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
        descr.setContent( body );
        return this;
    }

    public FunctionDescrBuilder argument( String type,
                                          String id ) {
        descr.addParameter( type, id );
        return this;
    }

    public FunctionDescrBuilder dialect( String dialect ) {
        descr.setDialect( dialect );
        return this;
    }
}
