/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import org.drools.core.factmodel.traits.IndexedTypeHierarchy;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HierarchyTest {



    @Test
    public void testHierEncoderTrivial() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "B" ) );
        encoder.encode( "D", Arrays.asList( "B", "C" ) );

        System.out.println( encoder );

        assertEquals(  parseBitSet( "0" ), encoder.getCode( "A" ) );
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B" ) );
        assertEquals(  parseBitSet( "11" ), encoder.getCode( "C" ) );
        assertEquals(  parseBitSet( "111" ), encoder.getCode( "D" ) );
    }

    @Test
    public void testHierManyRoots() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Collections.EMPTY_LIST );
        encoder.encode( "C", Collections.EMPTY_LIST );
        encoder.encode( "D", Collections.EMPTY_LIST );
        encoder.encode( "E", Collections.EMPTY_LIST );

        System.out.println( encoder );

        assertEquals(  parseBitSet( "1" ), encoder.getCode( "A" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "B" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "C" ) );
        assertEquals(  parseBitSet( "1000" ), encoder.getCode( "D" ) );
        assertEquals(  parseBitSet( "10000" ), encoder.getCode( "E" ) );

        assertEquals( 5, encoder.size() );
        assertEquals( 5, encoder.getSortedMembers().size() );
        assertEquals( 5, encoder.getSortedMap().size() );

    }


    @Test
    public void testHierManyRootsPropagation() {
        HierarchyEncoderImpl encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "A" ) );
        encoder.encode( "D", Arrays.asList( "B", "C" ) );
        encoder.encode( "E", Collections.EMPTY_LIST );

        System.out.println( encoder );

        BitSet a = encoder.getCode( "A" );
        BitSet b = encoder.getCode( "B" );
        BitSet c = encoder.getCode( "C" );
        BitSet d = encoder.getCode( "D" );
        BitSet e = encoder.getCode( "E" );

        assertTrue( encoder.superset( b, a ) > 0 );
        assertTrue( encoder.superset( c, a ) > 0 );
        assertTrue( encoder.superset( d, a ) > 0 );
        assertTrue( encoder.superset( d, b ) > 0 );
        assertTrue( encoder.superset( d, c ) > 0 );

        assertTrue( encoder.superset( e, a ) < 0 );
        assertTrue( encoder.superset( e, b ) < 0 );
        assertTrue( encoder.superset( e, c ) < 0 );
        assertTrue( encoder.superset( e, d ) < 0 );
        assertTrue( encoder.superset( e, e ) == 0 );

    }


    @Test
    public void testHierALotOfClasses() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();
        int N = 1194;

        encoder.encode( "A", Collections.EMPTY_LIST );
        for ( int j = 1; j < N; j++ ) {
            encoder.encode( "X" + j, Arrays.asList( "A" ) );
        }

        assertEquals( N, encoder.size() );
        BitSet code = encoder.getCode( "X" + ( N -1 ) );
        assertEquals( 1, code.cardinality() );
        assertTrue( code.get( N - 2 ) );
    }



    @Test
    public void testHierEncoderSimpleInheritance() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "A" ) );
        encoder.encode( "D", Arrays.asList( "B" ) );
        encoder.encode( "E", Arrays.asList( "B" ) );
        encoder.encode( "F", Arrays.asList( "C" ) );
        encoder.encode( "G", Arrays.asList( "C" ) );

        System.out.println( encoder );

        assertEquals( parseBitSet("0"), encoder.getCode("A"));
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "C" ) );
        assertEquals(  parseBitSet( "101" ), encoder.getCode( "D" ) );
        assertEquals(  parseBitSet( "1001" ), encoder.getCode( "E" ) );
        assertEquals(  parseBitSet( "110" ), encoder.getCode( "F" ) );
        assertEquals(  parseBitSet( "1010" ), encoder.getCode( "G" ) );
    }




    @Test
    public void testHierEncoderAnotherSimpleInheritance() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "R", Collections.EMPTY_LIST );
        encoder.encode( "A1", Arrays.asList( "R" ) );
        encoder.encode( "A2", Arrays.asList( "R" ) );
        encoder.encode( "A3", Arrays.asList( "R" ) );
        encoder.encode( "B1", Arrays.asList( "R" ) );
        encoder.encode( "B2", Arrays.asList( "R" ) );
        encoder.encode( "B3", Arrays.asList( "R" ) );
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );

        System.out.println( encoder );

        assertEquals( parseBitSet( "0"), encoder.getCode("R"));
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "A1" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "A2" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "A3" ) );
        assertEquals(  parseBitSet( "1000" ), encoder.getCode( "B1" ) );
        assertEquals(  parseBitSet( "10000" ), encoder.getCode( "B2" ) );
        assertEquals(  parseBitSet( "100000" ), encoder.getCode( "B3" ) );
        assertEquals(  parseBitSet( "11000" ), encoder.getCode( "B4" ) );
        assertEquals(  parseBitSet( "101000" ), encoder.getCode( "B5" ) );
        assertEquals(  parseBitSet( "110000" ), encoder.getCode( "B6" ) );
        assertEquals(  parseBitSet( "111000" ), encoder.getCode( "B7" ) );
    }



    @Test
    public void testHierEncoderAnotherSimpleInheritanceChangeOrder() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "R", Collections.EMPTY_LIST );

        encoder.encode( "B1", Arrays.asList( "R" ) );
        encoder.encode( "B2", Arrays.asList( "R" ) );
        encoder.encode( "B3", Arrays.asList( "R" ) );
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );

        encoder.encode( "A1", Arrays.asList( "R" ) );
        encoder.encode( "A2", Arrays.asList( "R" ) );
        encoder.encode( "A3", Arrays.asList( "R" ) );

        System.out.println( encoder );

        assertEquals( parseBitSet( "0"), encoder.getCode("R"));
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B1" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "B2" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "B3" ) );

        assertEquals(  parseBitSet( "11" ), encoder.getCode( "B4" ) );
        assertEquals(  parseBitSet( "101" ), encoder.getCode( "B5" ) );
        assertEquals(  parseBitSet( "110" ), encoder.getCode( "B6" ) );
        assertEquals(  parseBitSet( "111" ), encoder.getCode( "B7" ) );
        assertEquals(  parseBitSet( "1000" ), encoder.getCode( "A1" ) );
        assertEquals(  parseBitSet( "10000" ), encoder.getCode( "A2" ) );
        assertEquals(  parseBitSet( "100000" ), encoder.getCode( "A3" ) );
    }







    @Test
    public void testHierEncoderBipartiteInheritance() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "R", Collections.EMPTY_LIST );
        encoder.encode( "A1", Arrays.asList( "R" ) );
        encoder.encode( "A2", Arrays.asList( "R" ) );
        encoder.encode( "A3", Arrays.asList( "R" ) );
        encoder.encode( "B1", Arrays.asList( "R" ) );
        encoder.encode( "B2", Arrays.asList( "R" ) );
        encoder.encode( "B3", Arrays.asList( "R" ) );
        encoder.encode( "B4", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B1", "B2", "B3" ) );

        System.out.println( encoder );

        assertEquals( parseBitSet( "0"), encoder.getCode("R"));
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "A1" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "A2" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "A3" ) );
        assertEquals(  parseBitSet( "1000" ), encoder.getCode( "B1" ) );
        assertEquals(  parseBitSet( "10000" ), encoder.getCode( "B2" ) );
        assertEquals(  parseBitSet( "100000" ), encoder.getCode( "B3" ) );
        assertEquals(  parseBitSet( "10111000" ), encoder.getCode( "B4" ) );
        assertEquals(  parseBitSet( "1111000" ), encoder.getCode( "B5" ) );
        assertEquals(  parseBitSet( "100111000" ), encoder.getCode( "B6" ) );
    }


    @Test
    public void testHierEncoderBipartiteInheritanceDiffOrder() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "R", Collections.EMPTY_LIST );

        encoder.encode( "B1", Arrays.asList( "R" ) );
        encoder.encode( "B2", Arrays.asList( "R" ) );
        encoder.encode( "B3", Arrays.asList( "R" ) );
        encoder.encode( "B4", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B1", "B2", "B3" ) );

        encoder.encode( "A1", Arrays.asList( "R" ) );
        encoder.encode( "A2", Arrays.asList( "R" ) );
        encoder.encode( "A3", Arrays.asList( "R" ) );

        System.out.println( encoder );

        assertEquals( parseBitSet( "0"), encoder.getCode("R"));
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B1" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "B2" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "B3" ) );
        assertEquals(  parseBitSet( "10111" ), encoder.getCode( "B4" ) );
        assertEquals(  parseBitSet( "1111" ), encoder.getCode( "B5" ) );
        assertEquals(  parseBitSet( "100111" ), encoder.getCode( "B6" ) );
        assertEquals(  parseBitSet( "1000000" ), encoder.getCode( "A1" ) );
        assertEquals(  parseBitSet( "10000000" ), encoder.getCode( "A2" ) );
        assertEquals(  parseBitSet( "100000000" ), encoder.getCode( "A3" ) );
    }



    @Test
    public void testSquare() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "T", Collections.EMPTY_LIST );
        encoder.encode( "A", Arrays.asList( "T" ) );
        encoder.encode( "B", Arrays.asList( "T" ) );
        encoder.encode( "C", Arrays.asList( "A", "B" ) );
        encoder.encode( "D", Arrays.asList( "A", "B" ) );

        System.out.println( encoder );

        assertEquals(  parseBitSet( "0" ), encoder.getCode( "T" ) );
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "A" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "B" ) );
        assertEquals(  parseBitSet( "111" ), encoder.getCode( "D" ) );
        assertEquals(  parseBitSet( "1011" ), encoder.getCode( "C" ) );


    }



    @Test
    public void testConflictArising() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "A" ) );
        encoder.encode( "D", Arrays.asList( "B" ) );
        encoder.encode( "E", Arrays.asList( "B" ) );
        encoder.encode( "F", Arrays.asList( "C" ) );
        encoder.encode( "G", Arrays.asList( "C" ) );
        encoder.encode( "H", Arrays.asList( "E" ) );


        System.out.println( encoder );

        assertEquals(  parseBitSet( "0" ), encoder.getCode( "A" ) );
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "C" ) );
        assertEquals(  parseBitSet( "101" ), encoder.getCode( "D" ) );
        assertEquals(  parseBitSet( "1001" ), encoder.getCode( "E" ) );
        assertEquals(  parseBitSet( "110" ), encoder.getCode( "F" ) );
        assertEquals(  parseBitSet( "1010" ), encoder.getCode( "G" ) );
        assertEquals(  parseBitSet( "11001" ), encoder.getCode( "H" ) );

        encoder.encode( "I", Arrays.asList( "E", "F" ) );

        System.out.println( encoder );

        assertEquals(  parseBitSet( "0" ), encoder.getCode( "A" ) );
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "C" ) );
        assertEquals(  parseBitSet( "101" ), encoder.getCode( "D" ) );
        assertEquals(  parseBitSet( "1000001" ), encoder.getCode( "E" ) );
        assertEquals(  parseBitSet( "100010" ), encoder.getCode( "F" ) );
        assertEquals(  parseBitSet( "1010" ), encoder.getCode( "G" ) );
        assertEquals(  parseBitSet( "1010001" ), encoder.getCode( "H" ) );
        assertEquals(  parseBitSet( "1100011" ), encoder.getCode( "I" ) );

        checkHier( encoder, 'I' );
    }


    @Test
    public void testConflictArising2() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "A" ) );
        encoder.encode( "D", Arrays.asList( "B" ) );
        encoder.encode( "E", Arrays.asList( "B" ) );
        encoder.encode( "F", Arrays.asList( "C" ) );
        encoder.encode( "G", Arrays.asList( "C" ) );
        encoder.encode( "H", Arrays.asList( "E" ) );
        encoder.encode( "J", Arrays.asList( "F" ) );
        encoder.encode( "K", Arrays.asList( "J" ) );


        System.out.println( encoder );

        encoder.encode( "I", Arrays.asList( "E", "F" ) );

        System.out.println( encoder );

        checkHier( encoder, 'K' );

    }




    @Test
    public void testHierEncoderBipartiteStarInheritance() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "R", Collections.EMPTY_LIST );

        encoder.encode( "B1", Arrays.asList( "R" ) );
        encoder.encode( "B2", Arrays.asList( "R" ) );
        encoder.encode( "B3", Arrays.asList( "R" ) );
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );

        encoder.encode( "A1", Arrays.asList( "R" ) );
        encoder.encode( "A2", Arrays.asList( "R" ) );
        encoder.encode( "A3", Arrays.asList( "R" ) );
        encoder.encode( "A4", Arrays.asList( "A1", "A2", "A3" ) );
        encoder.encode( "A5", Arrays.asList( "A4" ) );
        encoder.encode( "A6", Arrays.asList( "A4" ) );
        encoder.encode( "A7", Arrays.asList( "A4" ) );


        System.out.println( encoder );

        assertEquals( parseBitSet( "0"), encoder.getCode("R"));
        assertEquals(  parseBitSet( "1" ), encoder.getCode( "B1" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "B2" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "B3" ) );
        assertEquals(  parseBitSet( "11" ), encoder.getCode( "B4" ) );
        assertEquals(  parseBitSet( "101" ), encoder.getCode( "B5" ) );
        assertEquals(  parseBitSet( "110" ), encoder.getCode( "B6" ) );
        assertEquals(  parseBitSet( "111" ), encoder.getCode( "B7" ) );
        assertEquals(  parseBitSet( "1000" ), encoder.getCode( "A1" ) );
        assertEquals(  parseBitSet( "10000" ), encoder.getCode( "A2" ) );
        assertEquals(  parseBitSet( "100000" ), encoder.getCode( "A3" ) );
        assertEquals(  parseBitSet( "111000" ), encoder.getCode( "A4" ) );
        assertEquals(  parseBitSet( "1111000" ), encoder.getCode( "A5" ) );
        assertEquals(  parseBitSet( "10111000" ), encoder.getCode( "A6" ) );
        assertEquals(  parseBitSet( "100111000" ), encoder.getCode( "A7" ) );
    }


    @Test
    public void testHierEncoderBipartiteStarInheritanceDiffOrder() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "R", Collections.EMPTY_LIST );

        encoder.encode( "A1", Arrays.asList( "R" ) );
        encoder.encode( "A2", Arrays.asList( "R" ) );
        encoder.encode( "A3", Arrays.asList( "R" ) );
        encoder.encode( "A4", Arrays.asList( "A1", "A2", "A3" ) );
        encoder.encode( "A5", Arrays.asList( "A4" ) );
        encoder.encode( "A6", Arrays.asList( "A4" ) );
        encoder.encode( "A7", Arrays.asList( "A4" ) );

        encoder.encode( "B1", Arrays.asList( "R" ) );
        encoder.encode( "B2", Arrays.asList( "R" ) );
        encoder.encode( "B3", Arrays.asList( "R" ) );
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );



        System.out.println( encoder );

        assertEquals( parseBitSet( "0"), encoder.getCode("R"));

        assertEquals(  parseBitSet( "1" ), encoder.getCode( "A1" ) );
        assertEquals(  parseBitSet( "10" ), encoder.getCode( "A2" ) );
        assertEquals(  parseBitSet( "100" ), encoder.getCode( "A3" ) );
        assertEquals(  parseBitSet( "111" ), encoder.getCode( "A4" ) );
        assertEquals(  parseBitSet( "1111" ), encoder.getCode( "A5" ) );
        assertEquals(  parseBitSet( "10111" ), encoder.getCode( "A6" ) );
        assertEquals(  parseBitSet( "100111" ), encoder.getCode( "A7" ) );

        assertEquals(  parseBitSet( "1000000" ), encoder.getCode( "B1" ) );
        assertEquals(  parseBitSet( "10000000" ), encoder.getCode( "B2" ) );
        assertEquals(  parseBitSet( "100000000" ), encoder.getCode( "B3" ) );
        assertEquals(  parseBitSet( "011000000" ), encoder.getCode( "B4" ) );
        assertEquals(  parseBitSet( "101000000" ), encoder.getCode( "B5" ) );
        assertEquals(  parseBitSet( "110000000" ), encoder.getCode( "B6" ) );
        assertEquals(  parseBitSet( "111000000" ), encoder.getCode( "B7" ) );
    }

    private BitSet parseBitSet( String s ) {
        BitSet b = new BitSet();
        int n = s.length();
        for( int j = 0; j < s.length(); j++ ) {
            if ( s.charAt( j ) == '1' ) {
                b.set( n - j - 1 );
            }
        }
        return b;
    }


    @Test
    public void testHierEncoderComplexInheritance() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        checkHier( encoder, 'A' );

        encoder.encode( "B", Arrays.asList( "A" ) );
        checkHier( encoder, 'B' );

        encoder.encode( "C", Arrays.asList( "A" ) );
        checkHier( encoder, 'C' );

        encoder.encode( "D", Arrays.asList( "B" ) );
        checkHier( encoder, 'D' );

        encoder.encode( "E", Arrays.asList( "B" ) );
        checkHier( encoder, 'E' );

        encoder.encode( "F", Arrays.asList( "C" ) );
        checkHier( encoder, 'F' );

        encoder.encode( "G", Arrays.asList( "C" ) );
        checkHier( encoder, 'G' );

        encoder.encode( "H", Arrays.asList( "D" ) );
        checkHier( encoder, 'H' );

        encoder.encode( "I", Arrays.asList( "D" ) );
        checkHier( encoder, 'I' );
//
        encoder.encode( "J", Arrays.asList( "E", "F" ) );
        checkHier( encoder, 'J' );

        encoder.encode( "K", Arrays.asList( "E", "F" ) );
        checkHier( encoder, 'K' );

        encoder.encode( "L", Arrays.asList( "G" ) );
        checkHier( encoder, 'L' );

        encoder.encode( "M", Arrays.asList( "G" ) );
        checkHier( encoder, 'M' );

        encoder.encode( "N", Arrays.asList( "I", "L" ) );
        checkHier( encoder, 'N' );

        encoder.encode( "O", Arrays.asList( "H", "M" ) );
        checkHier( encoder, 'O' );

        System.out.println( encoder );

        Collection<BitSet> codes = encoder.getSortedMap().values();
        Iterator<BitSet> iter = codes.iterator();
        Long last = -1L;
        for ( int j = 0; j < codes.size() -1; j++ ) {
            BitSet ns = iter.next();
            Long next = toLong( ns );
            System.out.println( next );
            assertTrue( next > last );
            last = next;
        }
    }

    private Long toLong( BitSet ns ) {
        Long l = 0L;
        for ( int j = 0; j < ns.length(); j++ ) {
            if ( ns.get( j ) ) {
                l += ( 1 << j );
            }
        }
        return l;
    }

    private void checkHier( HierarchyEncoder encoder, char fin ) {


        List<String>[] sups = new ArrayList[ fin - 'A' + 1 ];
        for ( int j = 'A'; j <= fin; j++ ) {
            sups[ j - 'A' ] = ((HierarchyEncoderImpl) encoder).ancestorValues( "" + (char) j );
        };

        for ( int j = 'A' ; j <  'A' + sups.length ; j++ ) {
            for ( int k = 'A'; k < 'A' + sups.length; k++ ) {
                String x = "" + (char) j;
                String y = "" + (char) k;
                BitSet xcode = encoder.getCode( x );
                BitSet ycode = encoder.getCode( y );

                int subOf = ((HierarchyEncoderImpl) encoder).superset(xcode, ycode);

                if ( x.equals( y ) ) {
                    assertEquals( x + " vs " + y, 0, subOf );
                } else if ( sups[ j - 'A' ].contains( y ) ) {
                    assertEquals( x + " vs " + y, 1, subOf );
                } else {
                    assertEquals( x + " vs " + y, -1, subOf );
                }

            }
        }

    }







    @Test
    public void testHierEncoderMoreInheritance() {
        HierarchyEncoderImpl encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "A" ) );
        encoder.encode( "D", Arrays.asList( "A" ) );
        encoder.encode( "E", Arrays.asList( "B" ) );
        encoder.encode( "F", Arrays.asList( "C" ) );
        encoder.encode( "G", Arrays.asList( "D" ) );
        encoder.encode( "H", Arrays.asList( "D" ) );
        encoder.encode( "I", Arrays.asList( "E" ) );
        encoder.encode( "J", Arrays.asList( "F" ) );
        encoder.encode( "K", Arrays.asList( "G" ) );
        encoder.encode( "L", Arrays.asList( "I", "J" ) );

        List<String>[] sups = new List[ ] {
                encoder.ancestorValues( "A" ),
                encoder.ancestorValues( "B" ),
                encoder.ancestorValues( "C" ),
                encoder.ancestorValues( "D" ),
                encoder.ancestorValues( "E" ),
                encoder.ancestorValues( "F" ),
                encoder.ancestorValues( "G" ),
                encoder.ancestorValues( "H" ),
                encoder.ancestorValues( "I" ),
                encoder.ancestorValues( "J" ),
                encoder.ancestorValues( "K" ),
                encoder.ancestorValues( "L" ),
        };

        for ( int j = 'A' ; j <=  'L' ; j++ ) {
            for ( int k = 'A'; k <= 'L'; k++ ) {
                String x = "" + (char) j;
                String y = "" + (char) k;
                BitSet xcode = encoder.getCode( x );
                BitSet ycode = encoder.getCode( y );

                int subOf = encoder.superset( xcode, ycode );

                if ( x.equals( y ) ) {
                    assertEquals( 0, subOf );
                } else if ( sups[ j - 'A' ].contains( y ) ) {
                    assertEquals( 1, subOf );
                } else {
                    assertEquals( -1, subOf );
                }

            }
        }
    }




    @Test
    public void testSecondOrderInheritance() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "T", Collections.EMPTY_LIST );
        encoder.encode( "A", Arrays.asList( "T" ) );
        encoder.encode( "B", Arrays.asList( "T" ) );
        encoder.encode( "C", Arrays.asList( "T" ) );
        encoder.encode( "D", Arrays.asList( "C" ) );
        encoder.encode( "F", Arrays.asList( "B", "C" ) );


        System.out.println( encoder );

        encoder.encode( "Z", Arrays.asList( "A", "B", "D" ) );

        System.out.println( encoder );

        assertTrue( ((HierarchyEncoderImpl) encoder).superset(encoder.getCode("Z"), encoder.getCode("F")) < 0 ) ;
        assertTrue( ((HierarchyEncoderImpl) encoder).superset(encoder.getCode("F"), encoder.getCode("Z")) < 0 ) ;
    }





    @Test
    public void testDecoderAncestors() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "Thing", Collections.EMPTY_LIST );
        encoder.encode( "A", Arrays.asList( "Thing" ) );
        encoder.encode( "Z", Arrays.asList( "Thing" ) );
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode( "P", Arrays.asList( "Thing" ) );
        encoder.encode( "Q", Arrays.asList( "Thing" ) );
        encoder.encode( "R", Arrays.asList( "Thing" ) );
        encoder.encode( "S", Arrays.asList( "R" ) );
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        System.out.println( encoder );

        BitSet b;
        Collection x;

        b = parseBitSet( "1100111" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "Q" ) );
        assertTrue( x.contains( "T" ) );
        assertTrue( x.contains( "R" ) );
        assertTrue( x.contains( "S" ) );
        assertTrue( x.contains( "M" ) );
        assertTrue( x.contains( "Thing" ) );
        assertEquals( 9, x.size() );


        b = parseBitSet( "100000" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "Q" ) );
        assertTrue( x.contains( "Thing" ) );

        b = parseBitSet( "1111" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 6, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "B" ) );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "N" ) );
        assertTrue( x.contains( "Thing" ) );

        b = parseBitSet( "111" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 4, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "Thing" ) );

        b = parseBitSet( "1" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Thing" ) );

        b = parseBitSet( "10" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "Thing" ) );

        b = parseBitSet( "0" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "Thing" ) );


        b = parseBitSet( "1011" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertEquals( 4, x.size() );
        assertTrue( x.contains( "Thing" ) );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "B" ) );
        assertTrue( x.contains( "Z" ) );

    }


    @Test
    public void testDecoderDescendants() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "Thing", Collections.EMPTY_LIST );
        encoder.encode( "A", Arrays.asList( "Thing" ) );
        encoder.encode( "Z", Arrays.asList( "Thing" ) );
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode( "P", Arrays.asList( "Thing" ) );
        encoder.encode( "Q", Arrays.asList( "Thing" ) );
        encoder.encode( "R", Arrays.asList( "Thing" ) );
        encoder.encode( "S", Arrays.asList( "R" ) );
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        System.out.println( encoder );

        BitSet b;
        Collection x;

        b = parseBitSet( "111" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertEquals( 3, x.size() );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "N" ) );
        assertTrue( x.contains( "T" ) );


        b = parseBitSet( "10" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertEquals( 5, x.size() );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "N" ) );
        assertTrue( x.contains( "T" ) );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "B" ) );


        b = parseBitSet( "100000" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertEquals( 4, x.size() );
        assertTrue( x.contains( "Q" ) );
        assertTrue( x.contains( "T" ) );
        assertTrue( x.contains( "M" ) );
        assertTrue( x.contains( "O" ) );




        b = parseBitSet( "100010" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "T" ) );

        b = parseBitSet( "1111" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "N" ) );


        b = parseBitSet( "1" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertEquals( 5, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "B" ) );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "N" ) );
        assertTrue( x.contains( "T" ) );

        System.out.println(" +*******************************+ ");

        x = encoder.lowerDescendants(new BitSet());
        System.out.println( "DESC " + x );

        assertEquals( 13, x.size() );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "Thing" ) );

    }


    @Test
    public void testHierEncoderDecoderLower() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "Thing", Collections.EMPTY_LIST );
        encoder.encode( "A", Arrays.asList( "Thing" ) );
        encoder.encode( "Z", Arrays.asList( "Thing" ) );
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode( "P", Arrays.asList( "Thing" ) );
        encoder.encode( "Q", Arrays.asList( "Thing" ) );
        encoder.encode( "R", Arrays.asList( "Thing" ) );
        encoder.encode( "S", Arrays.asList( "R" ) );
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        System.out.println( encoder );

        Collection x;

        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "B" ) ) );
        System.out.println( "GCS " + x );
        assertEquals( 1, x.size() );
        assertTrue( x.contains( "B" ) );

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "B" ) ) );
        System.out.println( "GCS " + x );
        assertEquals( 1, x.size() );
        assertTrue( x.contains( "N" ) );



        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "GCS " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "T" ) );

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "GCS " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "T" ) );




        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "A", "Z" ) ) );
        System.out.println( "GCS " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "B" ) );
        assertTrue( x.contains( "C" ) );

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "A", "Z" ) ) );
        System.out.println( "GCS " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "B" ) );
        assertTrue( x.contains( "C" ) );




        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "Thing" ) ) );
        System.out.println( "GCS " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "Thing" ) );

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "Thing" ) ) );
        System.out.println( "GCS " + x );

        assertEquals( 5, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );
        assertTrue( x.contains( "P" ) );
        assertTrue( x.contains( "Q" ) );
        assertTrue( x.contains( "R" ) );

    }



    @Test
    public void testHierEncoderDecoderUpper() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "Thing", Collections.EMPTY_LIST );
        encoder.encode( "A", Arrays.asList( "Thing" ) );
        encoder.encode( "Z", Arrays.asList( "Thing" ) );
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode( "P", Arrays.asList( "Thing" ) );
        encoder.encode( "Q", Arrays.asList( "Thing" ) );
        encoder.encode( "R", Arrays.asList( "Thing" ) );
        encoder.encode( "S", Arrays.asList( "R" ) );
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        System.out.println( encoder );

        Collection x;

        x = encoder.upperBorder( encoder.metMembersCode( Arrays.asList( "B" ) ) );
        System.out.println( "LCS " + x );
        assertEquals( 1, x.size() );
        assertTrue( x.contains( "B" ) );

        x = encoder.immediateParents( encoder.metMembersCode( Arrays.asList( "B" ) ) );
        System.out.println( "LCS " + x );
        assertEquals( 2, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );



        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "LCS " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "Thing" ) );

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "LCS " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "Thing" ) );



        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "B", "C" ) ) );
        System.out.println( "LCS " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "B", "C" ) ) );
        System.out.println( "LCS " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "A" ) );
        assertTrue( x.contains( "Z" ) );


        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "T" ) ) );
        System.out.println( "LCS " + x );

        assertEquals( 1, x.size() );
        assertTrue( x.contains( "T" ) );

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "T" ) ) );
        System.out.println( "LCS " + x );

        assertEquals( 2, x.size() );
        assertTrue( x.contains( "C" ) );
        assertTrue( x.contains( "Q" ) );


    }






    @Test
    public void testClassInstanceHierarchies() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        BitSet ak = encoder.encode( "A", Collections.EMPTY_LIST );
        BitSet bk = encoder.encode( "B", Arrays.asList( "A" ) );
        BitSet ck = encoder.encode( "C", Arrays.asList( "A" ) );
        BitSet dk = encoder.encode( "D", Arrays.asList( "B" ) );
        BitSet ek = encoder.encode( "E", Arrays.asList( "B" ) );
        BitSet fk = encoder.encode( "F", Arrays.asList( "C" ) );
        BitSet gk = encoder.encode( "G", Arrays.asList( "C" ) );
        BitSet hk = encoder.encode( "H", Arrays.asList( "D" ) );
        BitSet ik = encoder.encode( "I", Arrays.asList( "D" ) );
        BitSet jk = encoder.encode( "J", Arrays.asList( "E", "F" ) );
        BitSet kk = encoder.encode( "K", Arrays.asList( "E", "F" ) );
        BitSet lk = encoder.encode( "L", Arrays.asList( "G" ) );
        BitSet mk = encoder.encode( "M", Arrays.asList( "G" ) );
        BitSet nk = encoder.encode( "N", Arrays.asList( "I", "L" ) );
        BitSet ok = encoder.encode( "O", Arrays.asList( "H", "M" ) );

        System.out.println( encoder );

        CodedHierarchy<String> types = new IndexedTypeHierarchy<String>( "A", new BitSet(), "ZZZZ", encoder.getBottom() );

        types.addMember( "A", ak );
        types.addMember( "c", ck );
        types.addMember( "f", fk );
        types.addMember( "j", jk );
        types.addMember( "k", kk );
        types.addMember( "n", nk );
        types.addMember( "o", ok );
        types.addMember( "h", hk );


        System.out.println( types );

        assertEquals( Arrays.asList( "c", "h" ), types.children( "A" ) );
        assertEquals( Arrays.asList( "f", "n", "o" ), types.children( "c" ) );
        assertEquals( Arrays.asList( "j", "k" ), types.children( "f" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "j" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "k" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "n" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "o" ) );
        assertEquals( Arrays.asList( "o" ), types.children( "h" ) );
        assertEquals( Arrays.asList( ), types.children( "ZZZZ" ) );

        assertEquals( Arrays.asList( ), types.parents( "a" ) );
        assertEquals( Arrays.asList( "A" ), types.parents( "c" ) );
        assertEquals( Arrays.asList( "c" ), types.parents( "f" ) );
        assertEquals( Arrays.asList( "f" ), types.parents( "j" ) );
        assertEquals( Arrays.asList( "f" ), types.parents( "k" ) );
        assertEquals( Arrays.asList( "c" ), types.parents( "n" ) );
        assertEquals( Arrays.asList( "c", "h" ), types.parents( "o" ) );
        assertEquals( Arrays.asList( "A" ), types.parents( "h" ) );
        assertEquals( Arrays.asList( "j", "k", "n", "o" ), types.parents( "ZZZZ" ) );


        BitSet pk = encoder.encode( "P", Arrays.asList( "O" ) );
        types.addMember( "p", pk );

        System.out.println( types );

        assertEquals( Arrays.asList( "o" ), types.parents( "p" ) );
        assertEquals( Arrays.asList( "j", "k", "n", "p" ), types.parents( "ZZZZ" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "p" ) );


        types.removeMember( "o" );

        System.out.println( types );

        assertEquals( Arrays.asList( "c", "h" ), types.parents( "p" ) );
        assertEquals( Arrays.asList( "f", "n", "p" ), types.children( "c" ) );
        assertEquals( Arrays.asList( "j", "k" ), types.children( "f" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "n" ) );
        assertEquals( Arrays.asList( "ZZZZ" ), types.children( "p" ) );
        assertEquals( Arrays.asList( "p" ), types.children( "h" ) );

    }



    @Test
    public void testUnwantedCodeOverriding() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        BitSet ak = encoder.encode( "A", Collections.EMPTY_LIST );
        BitSet ck = encoder.encode( "C", Arrays.asList( "A" ) );
        BitSet dk = encoder.encode( "D", Arrays.asList( "A" ) );
        BitSet gk = encoder.encode( "G", Arrays.asList( "C", "D" ) );
        BitSet bk = encoder.encode( "B", Arrays.asList( "A" ) );
        BitSet ek = encoder.encode( "E", Arrays.asList( "B" ) );
        BitSet ik = encoder.encode( "I", Arrays.asList( "E", "C" ) );
        BitSet fk = encoder.encode( "F", Arrays.asList( "B", "C" ) );
        BitSet jk = encoder.encode( "J", Arrays.asList( "F", "D" ) );
        BitSet lk = encoder.encode( "L", Arrays.asList( "J" ) );

        assertNotNull( encoder.getCode( "L" ) );

        BitSet ok = encoder.encode( "O", Arrays.asList( "L" ) );

        assertNotNull( encoder.getCode( "L" ) );

        BitSet kk = encoder.encode( "K", Arrays.asList( "F", "G" ) );

        assertNotNull( encoder.getCode( "L" ) );

        BitSet mk = encoder.encode( "M", Arrays.asList( "J", "K" ) );

        assertNotNull( encoder.getCode( "L" ) );

        BitSet nk = encoder.encode( "N", Arrays.asList( "K" ) );

        assertNotNull( encoder.getCode( "L" ) );

        BitSet hk = encoder.encode( "H", Arrays.asList( "F" ) );

        assertNotNull( encoder.getCode( "L" ) );

        BitSet pk = encoder.encode( "P", Arrays.asList( "A" ) );

        assertNotNull( encoder.getCode( "L" ) );

        System.out.println( encoder );
        assertEquals( 16, encoder.size() );
    }



    @Test
    public void testDeepTree() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        encoder.encode( "A", Collections.EMPTY_LIST );

        encoder.encode( "B", Arrays.asList( "A" ) );

        encoder.encode( "C", Arrays.asList( "A" ) );
        encoder.encode( "D", Arrays.asList( "C" ) );
        encoder.encode( "E", Arrays.asList( "D" ) );
        encoder.encode( "F", Arrays.asList( "D" ) );
        encoder.encode( "G", Arrays.asList( "C" ) );
        encoder.encode( "H", Arrays.asList( "G" ) );
        encoder.encode( "I", Arrays.asList( "G" ) );
        encoder.encode( "J", Arrays.asList( "C" ) );
        encoder.encode( "K", Arrays.asList( "C" ) );

        encoder.encode( "L", Arrays.asList( "B" ) );
        encoder.encode( "M", Arrays.asList( "B" ) );

        encoder.encode( "N", Arrays.asList( "A" ) );
        encoder.encode( "O", Arrays.asList( "N" ) );



        System.out.println( encoder );

        checkHier( encoder, 'O' );
    }



    @Test
    public void testNestedTree() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A") );
        encoder.encode( "C", Arrays.asList( "B") );
        encoder.encode( "D", Arrays.asList( "B") );
        encoder.encode( "E", Arrays.asList( "D") );
        encoder.encode( "F", Arrays.asList( "E") );
        encoder.encode( "G", Arrays.asList( "E") );
        encoder.encode( "H", Arrays.asList( "G") );
        encoder.encode( "I", Arrays.asList( "H") );
        encoder.encode( "J", Arrays.asList( "E") );
        encoder.encode( "K", Arrays.asList( "J") );
        encoder.encode( "L", Arrays.asList( "K") );
        encoder.encode( "M", Arrays.asList( "J") );
        encoder.encode( "N", Arrays.asList( "M") );
        encoder.encode( "O", Arrays.asList( "J") );
        encoder.encode( "P", Arrays.asList( "O") );
        encoder.encode( "Q", Arrays.asList( "J") );
        encoder.encode( "R", Arrays.asList( "Q") );
        encoder.encode( "S", Arrays.asList( "B") );
        encoder.encode( "T", Arrays.asList( "S") );
        encoder.encode( "U", Arrays.asList( "T") );
        encoder.encode( "V", Arrays.asList( "B") );
        encoder.encode( "W", Arrays.asList( "V") );
        encoder.encode( "X", Arrays.asList( "W") );

        System.out.println( encoder );

        encoder.encode( "Y", Arrays.asList( "F", "W") );

        System.out.println( encoder );

        checkHier( encoder, (char) ( 'A' + encoder.size() - 1 ) );

    }

}