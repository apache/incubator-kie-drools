package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.CollectDescr;

/**
 *  A descriptor builder for Collect
 */
public interface CollectDescrBuilder<P extends DescrBuilder< ?, ? >>
    extends
    PatternContainerDescrBuilder<CollectDescrBuilder<P>, CollectDescr>,
    DescrBuilder<P, CollectDescr> {

}
