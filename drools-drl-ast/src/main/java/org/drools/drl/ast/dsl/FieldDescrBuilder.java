package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.TypeFieldDescr;

/**
 *  A descriptor builder for Globals
 */
public interface FieldDescrBuilder<T extends DescrBuilder<?,?>>
    extends
    AnnotatedDescrBuilder<FieldDescrBuilder<T>>,
    DescrBuilder<T, TypeFieldDescr> {

    public FieldDescrBuilder<T> index( int index );

    public FieldDescrBuilder<T> name( String name );

    public FieldDescrBuilder<T> type( String type );

    public FieldDescrBuilder<T> initialValue( String value );
}
