package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.CEDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.dsl.AnnotationDescrBuilder;
import org.drools.drl.ast.dsl.QueryDescrBuilder;
import org.drools.drl.ast.descr.AndDescr;

/**
 * A descr builder for queries
 */
public class QueryDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, QueryDescr>
    implements
    QueryDescrBuilder {

    protected QueryDescrBuilderImpl( PackageDescrBuilder parent ) {
        super( parent, new QueryDescr() );
    }


    public AnnotationDescrBuilder<QueryDescrBuilder> newAnnotation( String name ) {
        AnnotationDescrBuilder<QueryDescrBuilder> annotation = new AnnotationDescrBuilderImpl<>( this, name );
        descr.addAnnotation( annotation.getDescr() );
        return annotation;
    }

    public QueryDescrBuilder name( String name ) {
        descr.setName( name );
        return this;
    }

    public CEDescrBuilder<QueryDescrBuilder, AndDescr> lhs() {
        CEDescrBuilder<QueryDescrBuilder, AndDescr> ce = new CEDescrBuilderImpl<>( this, new AndDescr() );
        descr.setLhs( ce.getDescr() );
        return ce;
    }


    public QueryDescrBuilder parameter( String type,
                                        String variable ) {
        descr.addParameter( type,
                            variable );
        return this;
    }


}
