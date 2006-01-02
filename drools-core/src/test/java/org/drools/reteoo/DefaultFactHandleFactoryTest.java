package org.drools.reteoo;

import junit.framework.TestCase;

public class DefaultFactHandleFactoryTest extends TestCase {

    /*
     * Class under test for FactHandle newFactHandle()
     */
    public void testNewFactHandle() {
        DefaultFactHandleFactory factory = new DefaultFactHandleFactory();
        FactHandleImpl handle = (FactHandleImpl) factory.newFactHandle();
        assertEquals( 1,
                      handle.getId() );
        assertEquals( 1,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle();
        assertEquals( 2,
                      handle.getId() );
        assertEquals( 2,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle();
        assertEquals( 3,
                      handle.getId() );
        assertEquals( 3,
                      handle.getRecency() );
    }

    /*
     * Class under test for FactHandle newFactHandle(long)
     */
    public void testNewFactHandlelong() {
        DefaultFactHandleFactory factory = new DefaultFactHandleFactory();
        FactHandleImpl handle = (FactHandleImpl) factory.newFactHandle( 5 );
        assertEquals( 5,
                      handle.getId() );
        assertEquals( 1,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle( 3 );
        assertEquals( 3,
                      handle.getId() );
        assertEquals( 2,
                      handle.getRecency() );

        handle = (FactHandleImpl) factory.newFactHandle( 255 );
        assertEquals( 255,
                      handle.getId() );
        assertEquals( 3,
                      handle.getRecency() );
    }

}
