/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseNodeTest {

    @Test
    public void testBaseNode() {
        MockBaseNode node = new MockBaseNode( 10 );
        assertEquals( 10,
                      node.getId() );

        node = new MockBaseNode( 155 );
        assertEquals( 155,
                      node.getId() );
    }

    class MockBaseNode extends BaseNode {
        private static final long serialVersionUID = 510l;

        public MockBaseNode() {
        }

        public MockBaseNode(final int id) {
            super( id, RuleBasePartitionId.MAIN_PARTITION, false );
        }

        public void ruleAttached() {
        }

        public void attach( BuildContext context ) {

        }

        public void updateNewNode(final InternalWorkingMemory workingMemory,
                                  final PropagationContext context) {
        }

        protected boolean doRemove(final RuleRemovalContext context,
                                   final ReteooBuilder builder,
                                   final InternalWorkingMemory[] workingMemories) {
            return true;
        }

        public boolean isInUse() {
            return true;
        }

        @Override
        public void networkUpdated(UpdateContext updateContext) {           
        }

        public short getType() {
            return 0;
        }

    }

}
