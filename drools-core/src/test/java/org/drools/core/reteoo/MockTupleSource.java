package org.drools.core.reteoo;

import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;

public class MockTupleSource extends LeftTupleSource {

    private static final long serialVersionUID = 510l;

    private int               attached;

    private int               updated;

    public MockTupleSource(final int id, BuildContext context) {
        super( id, context );
    }

    public void attach() {
        this.attached++;
    }

    public int getAttached() {
        return this.sink.getSinks().length;
    }

    public int getUdated() {
        return this.updated;
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        return true;
    }

    public void doAttach( BuildContext context ) {
    }

    @Override
    public void networkUpdated(UpdateContext updateContext) {
    }

    public short getType() {
        return 0;
    }

    public ObjectTypeNode getObjectTypeNode() {
        return null;
    }

    @Override
    public boolean isLeftTupleMemoryEnabled() {
        return true;
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }
}
