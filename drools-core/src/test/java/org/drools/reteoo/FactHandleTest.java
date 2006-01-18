package org.drools.reteoo;

import junit.framework.TestCase;

public class FactHandleTest extends TestCase {
    /*
     * Class under test for void FactHandleImpl(long)
     */
    public void testFactHandleImpllong() {
        FactHandleImpl f0 = new FactHandleImpl( 134 );
        assertEquals( 134,
                      f0.getId() );
        assertEquals( 134,
                      f0.getRecency() );
    }

    /*
     * Class under test for void FactHandleImpl(long, long)
     */
    public void testFactHandleImpllonglong() {
        FactHandleImpl f0 = new FactHandleImpl( 134,
                                                678 );
        assertEquals( 134,
                      f0.getId() );
        assertEquals( 678,
                      f0.getRecency() );
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        FactHandleImpl f0 = new FactHandleImpl( 134 );
        FactHandleImpl f1 = new FactHandleImpl( 96 );
        FactHandleImpl f3 = new FactHandleImpl( 96 );

        assertFalse( "f0 should not equal f1",
                     f0.equals( f1 ) );
        assertEquals( f1,
                      f3 );
        assertNotSame( f1,
                       f3 );
    }

    public void testHashCode() {
        FactHandleImpl f0 = new FactHandleImpl( 234 );
        assertEquals( 234,
                      f0.hashCode() );
    }

    public void testToExternalForm() {
        FactHandleImpl f0 = new FactHandleImpl( 134 );

        assertEquals( "[fid:134:134]",
                      f0.toExternalForm() );
    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
        FactHandleImpl f0 = new FactHandleImpl( 134 );

        assertEquals( "[fid:134:134]",
                      f0.toString() );
    }

    public void testInvalidate() {
        FactHandleImpl f0 = new FactHandleImpl( 134 );
        assertEquals( 134,
                      f0.getId() );

        f0.invalidate();
        assertEquals( -1,
                      f0.getId() );
    }

}
