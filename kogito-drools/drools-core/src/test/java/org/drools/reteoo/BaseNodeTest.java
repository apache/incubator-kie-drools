package org.drools.reteoo;

import junit.framework.TestCase;

public class BaseNodeTest extends TestCase
{

    public void testBaseNode()
    {
        MockBaseNode node = new MockBaseNode( 10 );
        assertEquals( 10,
                      node.getId() );

        node = new MockBaseNode( 155 );
        assertEquals( 155,
                      node.getId() );
    }

    class MockBaseNode extends BaseNode
    {
        public MockBaseNode(int id)
        {
            super( id );
        }
    }

}
