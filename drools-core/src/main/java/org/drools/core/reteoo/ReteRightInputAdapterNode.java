package org.drools.core.reteoo;

import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap.ObjectEntry;

public class ReteRightInputAdapterNode extends RightInputAdapterNode {

    public ReteRightInputAdapterNode() {
    }

    public ReteRightInputAdapterNode(int id, LeftTupleSource source, LeftTupleSource startTupleSource, BuildContext context) {
        super(id, source, startTupleSource, context);
    }

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        // creating a dummy fact handle to wrap the tuple
        final InternalFactHandle handle = createFactHandle( leftTuple, context, workingMemory );
        boolean useLeftMemory = true;
        if ( !isLeftTupleMemoryEnabled() ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle) leftTuple.get( 0 )).getObject();
            if ( !(object instanceof DroolsQuery) || !((DroolsQuery) object).isOpen() ) {
                useLeftMemory = false;
            }
        }

        if ( useLeftMemory) {
            final RiaNodeMemory memory = (RiaNodeMemory) workingMemory.getNodeMemory( this );
            // add it to a memory mapping
            memory.getMap().put( leftTuple, handle );
        }

        // propagate it
        this.sink.propagateAssertObject( handle,
                                         context,
                                         workingMemory );
    }

    /**
     * Retracts the corresponding tuple by retrieving and retracting
     * the fact created for it
     */
    public void retractLeftTuple(final LeftTuple tuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final RiaNodeMemory memory = (RiaNodeMemory) workingMemory.getNodeMemory( this );
        // retrieve handle from memory
        final InternalFactHandle factHandle = (InternalFactHandle) memory.getMap().remove( tuple );

        for ( RightTuple rightTuple = factHandle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                              context,
                                                              workingMemory );
        }
        factHandle.clearRightTuples();

        for ( LeftTuple leftTuple = factHandle.getLastLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                           context,
                                                           workingMemory );
        }
        factHandle.clearLeftTuples();
    }


    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        final RiaNodeMemory memory = (RiaNodeMemory) workingMemory.getNodeMemory( this );
        // add it to a memory mapping
        InternalFactHandle handle = (InternalFactHandle) memory.getMap().get( leftTuple );

        // propagate it
        for ( RightTuple rightTuple = handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            rightTuple.getRightTupleSink().modifyRightTuple( rightTuple,
                                                             context,
                                                             workingMemory );
        }
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {

        final RiaNodeMemory memory = (RiaNodeMemory) workingMemory.getNodeMemory( this );

        final Iterator it = memory.getMap().iterator();

        // iterates over all propagated handles and assert them to the new sink
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            sink.assertObject( (InternalFactHandle) entry.getValue(),
                               context,
                               workingMemory );
        }
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !this.isInUse() ) {
            for ( InternalWorkingMemory workingMemory : workingMemories ) {
                RiaNodeMemory memory = (RiaNodeMemory) workingMemory.getNodeMemory( this );

                Iterator it = memory.getMap().iterator();
                for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                    LeftTuple leftTuple = (LeftTuple) entry.getKey();
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                }
                workingMemory.clearNodeMemory( this );
            }
        }
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink(this);
        }
    }


}
