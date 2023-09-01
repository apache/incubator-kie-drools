package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.descr.BaseDescr;

/**
 * A base class for all DescrBuilders
 */
public class BaseDescrBuilderImpl<P extends DescrBuilder<?,? extends BaseDescr>, T extends BaseDescr>
    implements
    DescrBuilder<P, T> {

    protected final T descr;
    protected P parent;

    protected BaseDescrBuilderImpl(final P parent,
                                   final T descr) {
        this.parent = parent;
        this.descr = descr;
        if( parent != null ) {
            this.descr.setResource(parent.getDescr().getResource());
        }
    }

    public DescrBuilder<P, T> startLocation( int line,
                                       int column ) {
        descr.setLocation( line,
                           column );
        return this;
    }

    public DescrBuilder<P, T> endLocation( int line,
                                     int column ) {
        descr.setEndLocation( line,
                              column );
        return this;
    }

    public DescrBuilder<P, T> startCharacter( int offset ) {
        descr.setStartCharacter( offset );
        return this;
    }

    public DescrBuilder<P, T> endCharacter( int offset ) {
        descr.setEndCharacter( offset );
        return this;
    }

    public T getDescr() {
        return descr;
    }

    public P end() {
        return parent;
    }

}
