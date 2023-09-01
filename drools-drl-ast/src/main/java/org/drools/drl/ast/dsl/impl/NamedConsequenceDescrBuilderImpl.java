package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.NamedConsequenceDescrBuilder;
import org.drools.drl.ast.descr.NamedConsequenceDescr;

/**
 * A builder implementation for NamedConsequenceDescr using a fluent API.
 */
public class NamedConsequenceDescrBuilderImpl<P extends DescrBuilder< ?, ? >>
        extends BaseDescrBuilderImpl<P, NamedConsequenceDescr>
        implements NamedConsequenceDescrBuilder<P> {

    protected NamedConsequenceDescrBuilderImpl(final P parent) {
        super(parent, new NamedConsequenceDescr());
    }

    public NamedConsequenceDescrBuilder<P> name( String name ) {
        getDescr().setName( name );
        return this;
    }

    public NamedConsequenceDescrBuilder<P> breaking( boolean breaking ) {
        getDescr().setBreaking( breaking );
        return this;
    }
}
