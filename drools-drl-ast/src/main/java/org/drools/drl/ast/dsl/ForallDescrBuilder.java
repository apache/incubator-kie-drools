package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.ForallDescr;

/**
 *  A descriptor builder for Forall
 */
public interface ForallDescrBuilder<P extends DescrBuilder<?, ?>>
    extends
    PatternContainerDescrBuilder<ForallDescrBuilder<P>,ForallDescr>,
    DescrBuilder<P, ForallDescr> {

}
