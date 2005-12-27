package org.drools.reteoo;

import junit.framework.TestCase;

import org.drools.FactException;
import org.drools.spi.PropagationContext;

public class BaseNodeTest extends TestCase {

    public void testBaseNode(){
        MockBaseNode node = new MockBaseNode( 10 );
        assertEquals( 10,
                      node.getId() );

        node = new MockBaseNode( 155 );
        assertEquals( 155,
                      node.getId() );
    }

    class MockBaseNode extends BaseNode {
        public MockBaseNode(int id){
            super( id );
        }

        public void ruleAttached(){
            // TODO Auto-generated method stub

        }

        public void attach(){
            // TODO Auto-generated method stub

        }

        public void remove(){
            // TODO Auto-generated method stub

        }

        public void updateNewNode(WorkingMemoryImpl workingMemory,
                                  PropagationContext context) throws FactException{
            // TODO Auto-generated method stub

        }

    }

}
