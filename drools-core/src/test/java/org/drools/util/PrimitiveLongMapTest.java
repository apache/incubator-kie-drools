package org.drools.util;

/*
 * $Id: PrimitiveLongMapTest.java,v 1.2 2005/08/01 00:01:11 mproctor Exp $
 *
 * Copyright 2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.Collection;

import junit.framework.TestCase;

public class PrimitiveLongMapTest extends TestCase {
    public void testValues(){
        PrimitiveLongMap map = new PrimitiveLongMap();
        assertNotNull( "MapNotNullTest ",
                       map );

        Collection values = map.values();
        assertNotNull( "ValuesNotNullTest ",
                       values );
        assertEquals( "ValuesZeroSizeTest ",
                      0,
                      values.size() );
    }

    public void testPaging(){
        PrimitiveLongMap map = new PrimitiveLongMap( 32,
                                                     8 );

        for ( int i = 0; i < 512; i++ ) {
            Integer value = new Integer( i );

            Object oldValue = map.put( i,
                                       value );
            assertNull( "OldValueNullTest ",
                        oldValue );
            assertEquals( "OldValueNullTest ",
                          value,
                          map.get( i ) );
        }

    }

    public void testGetWithNegativeKeyReturnsNull(){
        PrimitiveLongMap map = new PrimitiveLongMap( 2,
                                                     1 );

        assertNull( map.get( -1 ) );
    }

    public void testRemoveWithNegativeReturnsNull(){
        PrimitiveLongMap map = new PrimitiveLongMap( 2,
                                                     1 );

        assertNull( map.remove( -1 ) );
    }

    public void testPutWithNegativeKeyThrowsIllegalArgumentException(){
        PrimitiveLongMap map = new PrimitiveLongMap( 2,
                                                     1 );

        try {
            map.put( -1,
                     new Object() );
            fail();
        }
        catch ( IllegalArgumentException e ) {
            // expected
        }
    }

    /**
     * this tests maxKey for gets and removes if ( key > this.maxKey || key < 0 ) {
     * return null; }
     * 
     */
    public void testMaxKey(){

        PrimitiveLongMap map = new PrimitiveLongMap( 8,
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

    public void testLastIndexBoundary(){
        PrimitiveLongMap map = new PrimitiveLongMap( 32,
                                                     8 );
        map.put( 8192,
                 new Object() );
        map.remove( 8192 );
        map.put( 8192,
                 new Object() );
        map.put( 8191,
                 new Object() );
    }

    public void testSize(){
        PrimitiveLongMap map = new PrimitiveLongMap( 32,
                                                     8 );

        Object object = new Object();
        map.put( 231,
                 object );
        Object string = new Object();
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
