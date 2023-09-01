package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.QueryDescr;

/**
 *  A descriptor builder for queries
 */
public interface QueryDescrBuilder
    extends
    AnnotatedDescrBuilder<QueryDescrBuilder>,
    ParameterSupportBuilder<QueryDescrBuilder>,
    DescrBuilder<PackageDescrBuilder, QueryDescr> {

    public QueryDescrBuilder name( String name );

    public CEDescrBuilder<QueryDescrBuilder, AndDescr> lhs();

}
