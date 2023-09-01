package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.EvalDescr;

/**
 *  A descriptor builder for evals
 */
public interface EvalDescrBuilder<P extends DescrBuilder< ? , ? >>
    extends
    DescrBuilder<P, EvalDescr> {

    public EvalDescrBuilder<P> constraint( String expr );

}
