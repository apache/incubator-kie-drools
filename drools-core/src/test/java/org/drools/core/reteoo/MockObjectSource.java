package org.drools.core.reteoo;

import org.drools.base.base.ObjectType;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.bitmask.BitMask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MockObjectSource extends ObjectSource {
    private static final long serialVersionUID = 510l;

    private int               attached;

    private int               updated;

    private List              facts;

    public MockObjectSource(int i, BuildContext context) {
    }

    public MockObjectSource(final int id) {
        super( id, RuleBasePartitionId.MAIN_PARTITION );
        this.facts = new ArrayList();
    }

    public void attach() {
        this.attached++;

    }

    public int getAttached() {
        return this.attached;
    }

    public int getUdated() {
        return this.updated;
    }

    public void addFact(final InternalFactHandle handle) {
        this.facts.add( handle );
    }
    
    public void removeFact(final InternalFactHandle handle) {
        this.facts.remove( handle );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        this.updated++;
        for ( final Iterator it = this.facts.iterator(); it.hasNext(); ) {
            final InternalFactHandle handle = (InternalFactHandle) it.next();
            sink.assertObject( handle,
                               context,
                               workingMemory );
        }
    }

    public void doAttach(BuildContext context) {
    }

   
    public short getType() {
        return 0;
    }
    
    @Override
    public BitMask calculateDeclaredMask(ObjectType modifiedType, List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }
}
