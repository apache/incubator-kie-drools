package org.drools.drl.ast.dsl;

import java.util.List;

import org.drools.drl.ast.descr.BehaviorDescr;

/**
 *  A descriptor builder for pattern behaviors
 */
public interface BehaviorDescrBuilder<P extends DescrBuilder< ?, ? >>
    extends
    DescrBuilder<P, BehaviorDescr> {
    
    public BehaviorDescrBuilder<P> type( String type, String subtype );

    public BehaviorDescrBuilder<P> parameters( List<String> params );

}
