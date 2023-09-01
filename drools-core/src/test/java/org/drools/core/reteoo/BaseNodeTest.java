package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.common.PropagationContext;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseNodeTest {

    @Test
    public void testBaseNode() {
        MockBaseNode node = new MockBaseNode( 10 );
        assertThat(node.getId()).isEqualTo(10);

        node = new MockBaseNode( 155 );
        assertThat(node.getId()).isEqualTo(155);
    }

    class MockBaseNode extends BaseNode {
        private static final long serialVersionUID = 510l;

        public MockBaseNode() {
        }

        public MockBaseNode(final int id) {
            super( id, RuleBasePartitionId.MAIN_PARTITION );
        }

        public void ruleAttached() {
        }

        public void doAttach( BuildContext context ) {

        }

        public void updateNewNode(final InternalWorkingMemory workingMemory,
                                  final PropagationContext context) {
        }

        protected boolean doRemove(final RuleRemovalContext context,
                                   final ReteooBuilder builder) {
            return true;
        }

        public boolean isInUse() {
            return true;
        }

        @Override
        public ObjectTypeNode getObjectTypeNode() {
            return null;
        }

        @Override
        public void networkUpdated(UpdateContext updateContext) {           
        }

        public short getType() {
            return 0;
        }
    }
}
