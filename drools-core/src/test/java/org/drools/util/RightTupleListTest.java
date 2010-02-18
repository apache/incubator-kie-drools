package org.drools.util;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.core.util.RightTupleList;
import org.drools.reteoo.LeftTuple;

public class RightTupleListTest extends TestCase {
    public void testEmptyIterator() {                
        final RightTupleList map = new RightTupleList();
        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );                        
        
        assertNull( map.getFirst( new LeftTuple( h1, null,
                                                 true ) ) );
    }
}