package org.drools.common;

import org.drools.Cheese;
import org.drools.reteoo.ReteooFactHandleFactory;
import org.drools.spi.FactHandleFactory;

import junit.framework.TestCase;

public class EqualityKeyTest extends TestCase {
    public void test1() {
        ReteooFactHandleFactory factory = new ReteooFactHandleFactory();
        
        InternalFactHandle ch1 = factory.newFactHandle( new Cheese ("c", 10) );
        EqualityKey key = new EqualityKey( ch1 );
        
        assertSame( ch1, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        InternalFactHandle ch2 = factory.newFactHandle( new Cheese ("c", 10) );
        key.addFactHandle( ch2 );
        
        assertEquals( 1, key.getOtherFactHandle().size() );
        assertEquals( ch2, key.getOtherFactHandle().get( 0 ) );
        
        key.removeFactHandle( ch1 );
        assertSame( ch2, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        key.removeFactHandle( ch2 );
        assertNull( key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );         
        
        key = new EqualityKey( ch2 );
        key.addFactHandle( ch1 );
        assertSame( ch2, key.getFactHandle() );
        assertEquals( 1, key.getOtherFactHandle().size() );
        assertEquals( ch1, key.getOtherFactHandle().get( 0 ) );    
        
        key.removeFactHandle( ch1 );
        assertSame( ch2, key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );
        
        key.removeFactHandle( ch2 );
        assertNull( key.getFactHandle() );
        assertNull( key.getOtherFactHandle() );        
    }
}
