package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.DataProvider;
import org.drools.spi.PropagationContext;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap.ObjectEntry;

public class FromNode extends TupleSource
    implements
    TupleSink,
    NodeMemory {
    /**
     * 
     */
    private static final long          serialVersionUID = 320;

    private DataProvider               dataProvider;
    private TupleSource                tupleSource;
    private AlphaNodeFieldConstraint[] constraints;
    private BetaConstraints            binder;

    public FromNode(final int id,
                    final DataProvider dataProvider,
                    final TupleSource tupleSource,
                    final AlphaNodeFieldConstraint[] constraints,
                    final BetaConstraints binder) {
        super( id );
        this.dataProvider = dataProvider;
        this.tupleSource = tupleSource;
        this.constraints = constraints;
        this.binder = ( binder == null ) ? EmptyBetaConstraints.getInstance() : binder;
    }

    /**
     * @inheritDoc 
     */
    public void assertTuple(final ReteTuple leftTuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );

        memory.getTupleMemory().add( leftTuple );
        this.binder.updateFromTuple( workingMemory, leftTuple );

        for ( final java.util.Iterator it = this.dataProvider.getResults( leftTuple,
                                                          workingMemory,
                                                          context ); it.hasNext(); ) {
            final Object object = it.next();

            // First alpha node filters
            for ( int i = 0, length = this.constraints.length; i < length; i++ ) {
                if ( !this.constraints[i].isAllowed( object,
                                                     workingMemory ) ) {
                    // next iteration
                    continue;
                }
            }

            if ( this.binder.isAllowedCachedLeft( object ) ) {
                final InternalFactHandle handle = workingMemory.getFactHandleFactory().newFactHandle( object );
                
                memory.getCreatedHandles().put( leftTuple, handle );

                this.sink.propagateAssertTuple( leftTuple,
                                                handle,
                                                context,
                                                workingMemory );
            }
        }
    }

    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {

        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        memory.getTupleMemory().remove( leftTuple );
        final InternalFactHandle handle = (InternalFactHandle) memory.getCreatedHandles().remove( leftTuple );

        // if tuple was propagated
        if ( handle != null ) {

            this.sink.propagateRetractTuple( leftTuple,
                                             handle,
                                             context,
                                             workingMemory );

            // Destroying the 'from' result object 
            workingMemory.getFactHandleFactory().destroyFactHandle( handle );
        }
    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this, propagationContext, workingMemory );
        }
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        
        if ( !node.isInUse() ) {
            removeTupleSink( (TupleSink) node );
        }
        removeShare();

        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.tupleSource.remove( this,
                               workingMemories );
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return this.binder.createBetaMemory();
    }

    public void updateSink(TupleSink sink,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory) {
        
        final BetaMemory memory = (BetaMemory) workingMemory.getNodeMemory( this );
        
        final Iterator it = memory.getCreatedHandles().iterator();

        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next()) {
            sink.assertTuple( new ReteTuple( (ReteTuple)entry.getKey(),
                                             (InternalFactHandle) entry.getValue()),
                              context,
                              workingMemory );
        }
    }
}
