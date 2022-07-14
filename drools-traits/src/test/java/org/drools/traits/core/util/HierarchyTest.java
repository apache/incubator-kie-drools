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

package org.drools.traits.core.util;

import org.drools.core.util.CodedHierarchy;
import org.drools.core.util.HierarchyEncoder;
import org.drools.traits.core.factmodel.IndexedTypeHierarchy;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HierarchyTest {



    @Test
    public void testHierEncoderTrivial() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();

        encoder.encode( "A", Collections.EMPTY_LIST );
        encoder.encode( "B", Arrays.asList( "A" ) );
        encoder.encode( "C", Arrays.asList( "B" ) );
        encoder.encode( "D", Arrays.asList( "B", "C" ) );

        System.out.println( encoder );

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("11"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("111"));
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

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("1000"));
        assertThat(encoder.getCode("E")).isEqualTo(parseBitSet("10000"));

        assertThat(encoder.size()).isEqualTo(5);
        assertThat(encoder.getSortedMembers().size()).isEqualTo(5);
        assertThat(encoder.getSortedMap().size()).isEqualTo(5);

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

        assertThat(encoder.superset(b, a) > 0).isTrue();
        assertThat(encoder.superset(c, a) > 0).isTrue();
        assertThat(encoder.superset(d, a) > 0).isTrue();
        assertThat(encoder.superset(d, b) > 0).isTrue();
        assertThat(encoder.superset(d, c) > 0).isTrue();

        assertThat(encoder.superset(e, a) < 0).isTrue();
        assertThat(encoder.superset(e, b) < 0).isTrue();
        assertThat(encoder.superset(e, c) < 0).isTrue();
        assertThat(encoder.superset(e, d) < 0).isTrue();
        assertThat(encoder.superset(e, e) == 0).isTrue();

    }


    @Test
    public void testHierALotOfClasses() {
        HierarchyEncoder encoder = new HierarchyEncoderImpl();
        int N = 1194;

        encoder.encode( "A", Collections.EMPTY_LIST );
        for ( int j = 1; j < N; j++ ) {
            encoder.encode( "X" + j, Arrays.asList( "A" ) );
        }

        assertThat(encoder.size()).isEqualTo(N);
        BitSet code = encoder.getCode( "X" + ( N -1 ) );
        assertThat(code.cardinality()).isEqualTo(1);
        assertThat(code.get(N - 2)).isTrue();
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

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("101"));
        assertThat(encoder.getCode("E")).isEqualTo(parseBitSet("1001"));
        assertThat(encoder.getCode("F")).isEqualTo(parseBitSet("110"));
        assertThat(encoder.getCode("G")).isEqualTo(parseBitSet("1010"));
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

        assertThat(encoder.getCode("R")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("A1")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("A2")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("A3")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("B1")).isEqualTo(parseBitSet("1000"));
        assertThat(encoder.getCode("B2")).isEqualTo(parseBitSet("10000"));
        assertThat(encoder.getCode("B3")).isEqualTo(parseBitSet("100000"));
        assertThat(encoder.getCode("B4")).isEqualTo(parseBitSet("11000"));
        assertThat(encoder.getCode("B5")).isEqualTo(parseBitSet("101000"));
        assertThat(encoder.getCode("B6")).isEqualTo(parseBitSet("110000"));
        assertThat(encoder.getCode("B7")).isEqualTo(parseBitSet("111000"));
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

        assertThat(encoder.getCode("R")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B1")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B2")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("B3")).isEqualTo(parseBitSet("100"));

        assertThat(encoder.getCode("B4")).isEqualTo(parseBitSet("11"));
        assertThat(encoder.getCode("B5")).isEqualTo(parseBitSet("101"));
        assertThat(encoder.getCode("B6")).isEqualTo(parseBitSet("110"));
        assertThat(encoder.getCode("B7")).isEqualTo(parseBitSet("111"));
        assertThat(encoder.getCode("A1")).isEqualTo(parseBitSet("1000"));
        assertThat(encoder.getCode("A2")).isEqualTo(parseBitSet("10000"));
        assertThat(encoder.getCode("A3")).isEqualTo(parseBitSet("100000"));
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

        assertThat(encoder.getCode("R")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("A1")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("A2")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("A3")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("B1")).isEqualTo(parseBitSet("1000"));
        assertThat(encoder.getCode("B2")).isEqualTo(parseBitSet("10000"));
        assertThat(encoder.getCode("B3")).isEqualTo(parseBitSet("100000"));
        assertThat(encoder.getCode("B4")).isEqualTo(parseBitSet("10111000"));
        assertThat(encoder.getCode("B5")).isEqualTo(parseBitSet("1111000"));
        assertThat(encoder.getCode("B6")).isEqualTo(parseBitSet("100111000"));
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

        assertThat(encoder.getCode("R")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B1")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B2")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("B3")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("B4")).isEqualTo(parseBitSet("10111"));
        assertThat(encoder.getCode("B5")).isEqualTo(parseBitSet("1111"));
        assertThat(encoder.getCode("B6")).isEqualTo(parseBitSet("100111"));
        assertThat(encoder.getCode("A1")).isEqualTo(parseBitSet("1000000"));
        assertThat(encoder.getCode("A2")).isEqualTo(parseBitSet("10000000"));
        assertThat(encoder.getCode("A3")).isEqualTo(parseBitSet("100000000"));
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

        assertThat(encoder.getCode("T")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("111"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("1011"));


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

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("101"));
        assertThat(encoder.getCode("E")).isEqualTo(parseBitSet("1001"));
        assertThat(encoder.getCode("F")).isEqualTo(parseBitSet("110"));
        assertThat(encoder.getCode("G")).isEqualTo(parseBitSet("1010"));
        assertThat(encoder.getCode("H")).isEqualTo(parseBitSet("11001"));

        encoder.encode( "I", Arrays.asList( "E", "F" ) );

        System.out.println( encoder );

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("101"));
        assertThat(encoder.getCode("E")).isEqualTo(parseBitSet("1000001"));
        assertThat(encoder.getCode("F")).isEqualTo(parseBitSet("100010"));
        assertThat(encoder.getCode("G")).isEqualTo(parseBitSet("1010"));
        assertThat(encoder.getCode("H")).isEqualTo(parseBitSet("1010001"));
        assertThat(encoder.getCode("I")).isEqualTo(parseBitSet("1100011"));

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

        assertThat(encoder.getCode("R")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B1")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B2")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("B3")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("B4")).isEqualTo(parseBitSet("11"));
        assertThat(encoder.getCode("B5")).isEqualTo(parseBitSet("101"));
        assertThat(encoder.getCode("B6")).isEqualTo(parseBitSet("110"));
        assertThat(encoder.getCode("B7")).isEqualTo(parseBitSet("111"));
        assertThat(encoder.getCode("A1")).isEqualTo(parseBitSet("1000"));
        assertThat(encoder.getCode("A2")).isEqualTo(parseBitSet("10000"));
        assertThat(encoder.getCode("A3")).isEqualTo(parseBitSet("100000"));
        assertThat(encoder.getCode("A4")).isEqualTo(parseBitSet("111000"));
        assertThat(encoder.getCode("A5")).isEqualTo(parseBitSet("1111000"));
        assertThat(encoder.getCode("A6")).isEqualTo(parseBitSet("10111000"));
        assertThat(encoder.getCode("A7")).isEqualTo(parseBitSet("100111000"));
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

        assertThat(encoder.getCode("R")).isEqualTo(parseBitSet("0"));

        assertThat(encoder.getCode("A1")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("A2")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("A3")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("A4")).isEqualTo(parseBitSet("111"));
        assertThat(encoder.getCode("A5")).isEqualTo(parseBitSet("1111"));
        assertThat(encoder.getCode("A6")).isEqualTo(parseBitSet("10111"));
        assertThat(encoder.getCode("A7")).isEqualTo(parseBitSet("100111"));

        assertThat(encoder.getCode("B1")).isEqualTo(parseBitSet("1000000"));
        assertThat(encoder.getCode("B2")).isEqualTo(parseBitSet("10000000"));
        assertThat(encoder.getCode("B3")).isEqualTo(parseBitSet("100000000"));
        assertThat(encoder.getCode("B4")).isEqualTo(parseBitSet("011000000"));
        assertThat(encoder.getCode("B5")).isEqualTo(parseBitSet("101000000"));
        assertThat(encoder.getCode("B6")).isEqualTo(parseBitSet("110000000"));
        assertThat(encoder.getCode("B7")).isEqualTo(parseBitSet("111000000"));
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
            assertThat(next > last).isTrue();
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
                    assertThat(subOf).as(x + " vs " + y).isEqualTo(0);
                } else if ( sups[ j - 'A' ].contains( y ) ) {
                    assertThat(subOf).as(x + " vs " + y).isEqualTo(1);
                } else {
                    assertThat(subOf).as(x + " vs " + y).isEqualTo(-1);
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
                    assertThat(subOf).isEqualTo(0);
                } else if ( sups[ j - 'A' ].contains( y ) ) {
                    assertThat(subOf).isEqualTo(1);
                } else {
                    assertThat(subOf).isEqualTo(-1);
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

        assertThat(((HierarchyEncoderImpl) encoder).superset(encoder.getCode("Z"), encoder.getCode("F")) < 0).isTrue() ;
        assertThat(((HierarchyEncoderImpl) encoder).superset(encoder.getCode("F"), encoder.getCode("Z")) < 0).isTrue() ;
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

        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("Q")).isTrue();
        assertThat(x.contains("T")).isTrue();
        assertThat(x.contains("R")).isTrue();
        assertThat(x.contains("S")).isTrue();
        assertThat(x.contains("M")).isTrue();
        assertThat(x.contains("Thing")).isTrue();
        assertThat(x.size()).isEqualTo(9);


        b = parseBitSet( "100000" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("Q")).isTrue();
        assertThat(x.contains("Thing")).isTrue();

        b = parseBitSet( "1111" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(6);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("B")).isTrue();
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("N")).isTrue();
        assertThat(x.contains("Thing")).isTrue();

        b = parseBitSet( "111" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(4);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("Thing")).isTrue();

        b = parseBitSet( "1" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Thing")).isTrue();

        b = parseBitSet( "10" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("Thing")).isTrue();

        b = parseBitSet( "0" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("Thing")).isTrue();


        b = parseBitSet( "1011" );
        x = encoder.upperAncestors(b);
        System.out.println( "ANC " + x );

        assertThat(x.size()).isEqualTo(4);
        assertThat(x.contains("Thing")).isTrue();
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("B")).isTrue();
        assertThat(x.contains("Z")).isTrue();

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

        assertThat(x.size()).isEqualTo(3);
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("N")).isTrue();
        assertThat(x.contains("T")).isTrue();


        b = parseBitSet( "10" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertThat(x.size()).isEqualTo(5);
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("N")).isTrue();
        assertThat(x.contains("T")).isTrue();
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("B")).isTrue();


        b = parseBitSet( "100000" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertThat(x.size()).isEqualTo(4);
        assertThat(x.contains("Q")).isTrue();
        assertThat(x.contains("T")).isTrue();
        assertThat(x.contains("M")).isTrue();
        assertThat(x.contains("O")).isTrue();




        b = parseBitSet( "100010" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("T")).isTrue();

        b = parseBitSet( "1111" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("N")).isTrue();


        b = parseBitSet( "1" );
        x = encoder.lowerDescendants(b);
        System.out.println( "DESC " + x );

        assertThat(x.size()).isEqualTo(5);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("B")).isTrue();
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("N")).isTrue();
        assertThat(x.contains("T")).isTrue();

        System.out.println(" +*******************************+ ");

        x = encoder.lowerDescendants(new BitSet());
        System.out.println( "DESC " + x );

        assertThat(x.size()).isEqualTo(13);
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("Thing")).isTrue();

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
        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("B")).isTrue();

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "B" ) ) );
        System.out.println( "GCS " + x );
        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("N")).isTrue();



        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "GCS " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("T")).isTrue();

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "GCS " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("T")).isTrue();




        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "A", "Z" ) ) );
        System.out.println( "GCS " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("B")).isTrue();
        assertThat(x.contains("C")).isTrue();

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "A", "Z" ) ) );
        System.out.println( "GCS " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("B")).isTrue();
        assertThat(x.contains("C")).isTrue();




        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "Thing" ) ) );
        System.out.println( "GCS " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("Thing")).isTrue();

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "Thing" ) ) );
        System.out.println( "GCS " + x );

        assertThat(x.size()).isEqualTo(5);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();
        assertThat(x.contains("P")).isTrue();
        assertThat(x.contains("Q")).isTrue();
        assertThat(x.contains("R")).isTrue();

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
        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("B")).isTrue();

        x = encoder.immediateParents( encoder.metMembersCode( Arrays.asList( "B" ) ) );
        System.out.println( "LCS " + x );
        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();



        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "LCS " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("Thing")).isTrue();

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "Z", "Q" ) ) );
        System.out.println( "LCS " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("Thing")).isTrue();



        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "B", "C" ) ) );
        System.out.println( "LCS " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "B", "C" ) ) );
        System.out.println( "LCS " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("A")).isTrue();
        assertThat(x.contains("Z")).isTrue();


        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "T" ) ) );
        System.out.println( "LCS " + x );

        assertThat(x.size()).isEqualTo(1);
        assertThat(x.contains("T")).isTrue();

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "T" ) ) );
        System.out.println( "LCS " + x );

        assertThat(x.size()).isEqualTo(2);
        assertThat(x.contains("C")).isTrue();
        assertThat(x.contains("Q")).isTrue();


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

        CodedHierarchy<String> types = new IndexedTypeHierarchy<String>("A", new BitSet(), "ZZZZ", encoder.getBottom() );

        types.addMember( "A", ak );
        types.addMember( "c", ck );
        types.addMember( "f", fk );
        types.addMember( "j", jk );
        types.addMember( "k", kk );
        types.addMember( "n", nk );
        types.addMember( "o", ok );
        types.addMember( "h", hk );


        System.out.println( types );

        assertThat(types.children("A")).isEqualTo(Arrays.asList("c", "h"));
        assertThat(types.children("c")).isEqualTo(Arrays.asList("f", "n", "o"));
        assertThat(types.children("f")).isEqualTo(Arrays.asList("j", "k"));
        assertThat(types.children("j")).isEqualTo(Arrays.asList("ZZZZ"));
        assertThat(types.children("k")).isEqualTo(Arrays.asList("ZZZZ"));
        assertThat(types.children("n")).isEqualTo(Arrays.asList("ZZZZ"));
        assertThat(types.children("o")).isEqualTo(Arrays.asList("ZZZZ"));
        assertThat(types.children("h")).isEqualTo(Arrays.asList("o"));
        assertThat(types.children("ZZZZ")).isEqualTo(Arrays.asList());

        assertThat(types.parents("a")).isEqualTo(Arrays.asList());
        assertThat(types.parents("c")).isEqualTo(Arrays.asList("A"));
        assertThat(types.parents("f")).isEqualTo(Arrays.asList("c"));
        assertThat(types.parents("j")).isEqualTo(Arrays.asList("f"));
        assertThat(types.parents("k")).isEqualTo(Arrays.asList("f"));
        assertThat(types.parents("n")).isEqualTo(Arrays.asList("c"));
        assertThat(types.parents("o")).isEqualTo(Arrays.asList("c", "h"));
        assertThat(types.parents("h")).isEqualTo(Arrays.asList("A"));
        assertThat(types.parents("ZZZZ")).isEqualTo(Arrays.asList("j", "k", "n", "o"));


        BitSet pk = encoder.encode( "P", Arrays.asList( "O" ) );
        types.addMember( "p", pk );

        System.out.println( types );

        assertThat(types.parents("p")).isEqualTo(Arrays.asList("o"));
        assertThat(types.parents("ZZZZ")).isEqualTo(Arrays.asList("j", "k", "n", "p"));
        assertThat(types.children("p")).isEqualTo(Arrays.asList("ZZZZ"));


        types.removeMember( "o" );

        System.out.println( types );

        assertThat(types.parents("p")).isEqualTo(Arrays.asList("c", "h"));
        assertThat(types.children("c")).isEqualTo(Arrays.asList("f", "n", "p"));
        assertThat(types.children("f")).isEqualTo(Arrays.asList("j", "k"));
        assertThat(types.children("n")).isEqualTo(Arrays.asList("ZZZZ"));
        assertThat(types.children("p")).isEqualTo(Arrays.asList("ZZZZ"));
        assertThat(types.children("h")).isEqualTo(Arrays.asList("p"));

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

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet ok = encoder.encode( "O", Arrays.asList( "L" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet kk = encoder.encode( "K", Arrays.asList( "F", "G" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet mk = encoder.encode( "M", Arrays.asList( "J", "K" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet nk = encoder.encode( "N", Arrays.asList( "K" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet hk = encoder.encode( "H", Arrays.asList( "F" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet pk = encoder.encode( "P", Arrays.asList( "A" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        System.out.println( encoder );
        assertThat(encoder.size()).isEqualTo(16);
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