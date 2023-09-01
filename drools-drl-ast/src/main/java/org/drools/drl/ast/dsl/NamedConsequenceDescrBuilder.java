package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.NamedConsequenceDescr;

public interface NamedConsequenceDescrBuilder<P extends DescrBuilder< ? , ? >> extends DescrBuilder<P, NamedConsequenceDescr> {

    /**
     * Sets the consequence name
     *
     * @param name the name of the consequence to be invoked
     * @return itself
     */
    public NamedConsequenceDescrBuilder<P> name( String name );

    /**
     * Sets the consequence invocation as breaking or not
     *
     * @param breaking
     * @return itself
     */
    public NamedConsequenceDescrBuilder<P> breaking( boolean breaking );
}
