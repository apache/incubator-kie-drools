package org.drools.reteoo;
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



import junit.framework.TestCase;

import org.drools.spi.PropagationContext;

public class BaseNodeTest extends TestCase {

    public void testBaseNode() {
        MockBaseNode node = new MockBaseNode( 10 );
        assertEquals( 10,
                      node.getId() );

        node = new MockBaseNode( 155 );
        assertEquals( 155,
                      node.getId() );
    }

    class MockBaseNode extends BaseNode {
        public MockBaseNode(int id) {
            super( id );
        }

        public void ruleAttached() {
            // TODO Auto-generated method stub

        }

        public void attach() {
            // TODO Auto-generated method stub

        }

        public void updateNewNode(WorkingMemoryImpl workingMemory,
                                  PropagationContext context) {
            // TODO Auto-generated method stub

        }

        public void remove(BaseNode node,
                           WorkingMemoryImpl[] workingMemories) {
            // TODO Auto-generated method stub

        }

        public void attach(WorkingMemoryImpl[] workingMemories) {
            // TODO Auto-generated method stub

        }

    }

}