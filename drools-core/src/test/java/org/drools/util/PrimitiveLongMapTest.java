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

package org.drools.util;

import java.util.Collection;

import org.drools.core.util.PrimitiveLongMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrimitiveLongMapTest {
    @Test
    public void testValues() {
        final PrimitiveLongMap map = new PrimitiveLongMap();
        assertNotNull( "MapNotNullTest ",
                       map );

        final Collection values = map.values();
        assertNotNull( "ValuesNotNullTest ",
                       values );
        assertEquals( "ValuesZeroSizeTest ",
                      0,
                      values.size() );
    }

    @Test
    public void testPaging() {
        final PrimitiveLongMap map = new PrimitiveLongMap( 32,
                                                           8 );

        for ( int i = 0; i < 512; i++ ) {
            final Integer value = new Integer( i );

            final Object oldValue = map.put( i,
                                             value );
            assertNull( "OldValueNullTest ",
                        oldValue );
            assertEquals( "OldValueNullTest ",
                          value,
                          map.get( i ) );
        }

    }

    @Test
    public void testGetWithNegativeKeyReturnsNull() {
        final PrimitiveLongMap map = new PrimitiveLongMap( 2,
                                                           1 );

        assertNull( map.get( -1 ) );
    }

    @Test
    public void testRemoveWithNegativeReturnsNull() {
        final PrimitiveLongMap map = new PrimitiveLongMap( 2,
                                                           1 );

        assertNull( map.remove( -1 ) );
    }

    @Test
    public void testPutWithNegativeKeyThrowsIllegalArgumentException() {
        final PrimitiveLongMap map = new PrimitiveLongMap( 2,
                                                           1 );

        try {
            map.put( -1,
                     new Object() );
            fail();
        } catch ( final IllegalArgumentException e ) {
            // expected
        }
    }

    /**
     * this tests maxKey for gets and removes if ( key > this.maxKey || key < 0 ) {
     * return null; }
     * 
     */
    @Test
    public void testMaxKey() {

        final PrimitiveLongMap map = new PrimitiveLongMap( 8,
                                                           4 );

        // Test maxKey for key 0
        map.put( 0,
                 new Integer( 0 ) );

        assertEquals( new Integer( 0 ),
                      map.get( 0 ) );
        assertNull( map.remove( 1 ) );
        assertEquals( new Integer( 0 ),
                      map.get( 0 ) );
        assertNotNull( map.remove( 0 ) );
        assertNull( map.get( 0 ) );

        // Test maxKey for key 1
        map.put( 1,
                 new Integer( 1 ) );
        assertEquals( new Integer( 1 ),
                      map.get( 1 ) );
        assertNull( map.remove( 2 ) );
        assertEquals( new Integer( 1 ),
                      map.get( 1 ) );
        assertNotNull( map.remove( 1 ) );
        assertNull( map.get( 1 ) );

        // Test maxKey for key 127, an end to a page border
        map.put( 127,
                 new Integer( 127 ) );
        assertEquals( new Integer( 127 ),
                      map.get( 127 ) );
        assertNull( map.remove( 128 ) );
        assertEquals( new Integer( 127 ),
                      map.get( 127 ) );
        assertNotNull( map.remove( 127 ) );
        assertNull( map.get( 127 ) );

        // Test maxKey for key 128, a start to a new page
        map.put( 128,
                 new Integer( 128 ) );
        assertEquals( new Integer( 128 ),
                      map.get( 128 ) );
        assertNull( map.remove( 129 ) );
        assertEquals( new Integer( 128 ),
                      map.get( 128 ) );
        assertNotNull( map.remove( 128 ) );
        assertNull( map.get( 128 ) );
    }

    @Test
    public void testLastIndexBoundary() {
        final PrimitiveLongMap map = new PrimitiveLongMap( 32,
                                                           8 );
        map.put( 8192,
                 new Object() );
        map.remove( 8192 );
        map.put( 8192,
                 new Object() );
        map.put( 8191,
                 new Object() );
    }

    @Test
    public void testSize() {
        final PrimitiveLongMap map = new PrimitiveLongMap( 32,
                                                           8 );

        final Object object = new Object();
        map.put( 231,
                 object );
        final Object string = new Object();
        map.put( 211,
                 string );
        map.put( 99822,
                 null );
        assertEquals( 3,
                      map.size() );

        map.put( 211,
                 null );
        map.put( 99822,
                 string );
        assertEquals( 3,
                      map.size() );

        map.remove( 211 );
        assertEquals( 2,
                      map.size() );
        map.remove( 99822 );
        assertEquals( 1,
                      map.size() );

        map.remove( 231 );
        assertEquals( 0,
                      map.size() );
    }
}
