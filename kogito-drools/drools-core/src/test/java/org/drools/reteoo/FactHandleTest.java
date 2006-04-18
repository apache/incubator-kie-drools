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