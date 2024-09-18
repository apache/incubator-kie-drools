/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.traits.core.util;

import org.drools.traits.core.factmodel.CodedHierarchy;
import org.drools.traits.core.factmodel.HierarchyEncoder;
import org.drools.traits.core.factmodel.IndexedTypeHierarchy;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HierarchyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HierarchyTest.class);

    @Test
    public void testHierEncoderTrivial() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("B"));
        encoder.encode( "D", Arrays.asList( "B", "C" ) );

        LOGGER.debug( encoder.toString() );

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("11"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("111"));
    }

    @Test
    public void testHierManyRoots() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode( "B", Collections.emptyList());
        encoder.encode( "C", Collections.emptyList());
        encoder.encode( "D", Collections.emptyList());
        encoder.encode( "E", Collections.emptyList());

        LOGGER.debug( encoder.toString() );

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("100"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("1000"));
        assertThat(encoder.getCode("E")).isEqualTo(parseBitSet("10000"));

        assertThat(encoder.size()).isEqualTo(5);
        assertThat(encoder.getSortedMembers()).hasSize(5);
        assertThat(encoder.getSortedMap()).hasSize(5);

    }


    @Test
    public void testHierManyRootsPropagation() {
        HierarchyEncoderImpl<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("A"));
        encoder.encode( "D", Arrays.asList( "B", "C" ) );
        encoder.encode( "E", Collections.emptyList());

        LOGGER.debug( encoder.toString() );

        BitSet a = encoder.getCode( "A" );
        BitSet b = encoder.getCode( "B" );
        BitSet c = encoder.getCode( "C" );
        BitSet d = encoder.getCode( "D" );
        BitSet e = encoder.getCode( "E" );

        assertThat(encoder.superset(b, a)).isGreaterThan(0);
        assertThat(encoder.superset(c, a)).isGreaterThan(0);
        assertThat(encoder.superset(d, a)).isGreaterThan(0);
        assertThat(encoder.superset(d, b)).isGreaterThan(0);
        assertThat(encoder.superset(d, c)).isGreaterThan(0);

        assertThat(encoder.superset(e, a)).isLessThan(0);
        assertThat(encoder.superset(e, b)).isLessThan(0);
        assertThat(encoder.superset(e, c)).isLessThan(0);
        assertThat(encoder.superset(e, d)).isLessThan(0);
        assertThat(encoder.superset(e, e)).isEqualTo(0);

    }


    @Test
    public void testHierALotOfClasses() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();
        int N = 1194;

        encoder.encode( "A", Collections.emptyList());
        for ( int j = 1; j < N; j++ ) {
            encoder.encode( "X" + j, List.of("A"));
        }

        assertThat(encoder.size()).isEqualTo(N);
        BitSet code = encoder.getCode( "X" + ( N -1 ) );
        assertThat(code.cardinality()).isEqualTo(1);
        assertThat(code.get(N - 2)).isTrue();
    }



    @Test
    public void testHierEncoderSimpleInheritance() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("A"));
        encoder.encode("D", List.of("B"));
        encoder.encode("E", List.of("B"));
        encoder.encode("F", List.of("C"));
        encoder.encode("G", List.of("C"));

        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "R", Collections.emptyList());
        encoder.encode("A1", List.of("R"));
        encoder.encode("A2", List.of("R"));
        encoder.encode("A3", List.of("R"));
        encoder.encode("B1", List.of("R"));
        encoder.encode("B2", List.of("R"));
        encoder.encode("B3", List.of("R"));
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );

        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "R", Collections.emptyList());

        encoder.encode("B1", List.of("R"));
        encoder.encode("B2", List.of("R"));
        encoder.encode("B3", List.of("R"));
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );

        encoder.encode("A1", List.of("R"));
        encoder.encode("A2", List.of("R"));
        encoder.encode("A3", List.of("R"));

        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "R", Collections.emptyList());
        encoder.encode("A1", List.of("R"));
        encoder.encode("A2", List.of("R"));
        encoder.encode("A3", List.of("R"));
        encoder.encode("B1", List.of("R"));
        encoder.encode("B2", List.of("R"));
        encoder.encode("B3", List.of("R"));
        encoder.encode( "B4", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B1", "B2", "B3" ) );

        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "R", Collections.emptyList());

        encoder.encode("B1", List.of("R"));
        encoder.encode("B2", List.of("R"));
        encoder.encode("B3", List.of("R"));
        encoder.encode( "B4", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B2", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B1", "B2", "B3" ) );

        encoder.encode("A1", List.of("R"));
        encoder.encode("A2", List.of("R"));
        encoder.encode("A3", List.of("R"));

        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "T", Collections.emptyList());
        encoder.encode("A", List.of("T"));
        encoder.encode("B", List.of("T"));
        encoder.encode( "C", Arrays.asList( "A", "B" ) );
        encoder.encode( "D", Arrays.asList( "A", "B" ) );

        LOGGER.debug( encoder.toString() );

        assertThat(encoder.getCode("T")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("111"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("1011"));


    }



    @Test
    public void testConflictArising() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("A"));
        encoder.encode("D", List.of("B"));
        encoder.encode("E", List.of("B"));
        encoder.encode("F", List.of("C"));
        encoder.encode("G", List.of("C"));
        encoder.encode("H", List.of("E"));


        LOGGER.debug( encoder.toString() );

        assertThat(encoder.getCode("A")).isEqualTo(parseBitSet("0"));
        assertThat(encoder.getCode("B")).isEqualTo(parseBitSet("1"));
        assertThat(encoder.getCode("C")).isEqualTo(parseBitSet("10"));
        assertThat(encoder.getCode("D")).isEqualTo(parseBitSet("101"));
        assertThat(encoder.getCode("E")).isEqualTo(parseBitSet("1001"));
        assertThat(encoder.getCode("F")).isEqualTo(parseBitSet("110"));
        assertThat(encoder.getCode("G")).isEqualTo(parseBitSet("1010"));
        assertThat(encoder.getCode("H")).isEqualTo(parseBitSet("11001"));

        encoder.encode( "I", Arrays.asList( "E", "F" ) );

        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("A"));
        encoder.encode("D", List.of("B"));
        encoder.encode("E", List.of("B"));
        encoder.encode("F", List.of("C"));
        encoder.encode("G", List.of("C"));
        encoder.encode("H", List.of("E"));
        encoder.encode("J", List.of("F"));
        encoder.encode("K", List.of("J"));


        LOGGER.debug( encoder.toString() );

        encoder.encode( "I", Arrays.asList( "E", "F" ) );

        LOGGER.debug( encoder.toString() );

        checkHier( encoder, 'K' );

    }




    @Test
    public void testHierEncoderBipartiteStarInheritance() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "R", Collections.emptyList());

        encoder.encode("B1", List.of("R"));
        encoder.encode("B2", List.of("R"));
        encoder.encode("B3", List.of("R"));
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );

        encoder.encode("A1", List.of("R"));
        encoder.encode("A2", List.of("R"));
        encoder.encode("A3", List.of("R"));
        encoder.encode( "A4", Arrays.asList( "A1", "A2", "A3" ) );
        encoder.encode("A5", List.of("A4"));
        encoder.encode("A6", List.of("A4"));
        encoder.encode("A7", List.of("A4"));


        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "R", Collections.emptyList());

        encoder.encode("A1", List.of("R"));
        encoder.encode("A2", List.of("R"));
        encoder.encode("A3", List.of("R"));
        encoder.encode( "A4", Arrays.asList( "A1", "A2", "A3" ) );
        encoder.encode("A5", List.of("A4"));
        encoder.encode("A6", List.of("A4"));
        encoder.encode("A7", List.of("A4"));

        encoder.encode("B1", List.of("R"));
        encoder.encode("B2", List.of("R"));
        encoder.encode("B3", List.of("R"));
        encoder.encode( "B4", Arrays.asList( "B1", "B2" ) );
        encoder.encode( "B5", Arrays.asList( "B1", "B3" ) );
        encoder.encode( "B6", Arrays.asList( "B2", "B3" ) );
        encoder.encode( "B7", Arrays.asList( "B4", "B5", "B6" ) );



        LOGGER.debug( encoder.toString() );

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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        checkHier( encoder, 'A' );

        encoder.encode("B", List.of("A"));
        checkHier( encoder, 'B' );

        encoder.encode("C", List.of("A"));
        checkHier( encoder, 'C' );

        encoder.encode("D", List.of("B"));
        checkHier( encoder, 'D' );

        encoder.encode("E", List.of("B"));
        checkHier( encoder, 'E' );

        encoder.encode("F", List.of("C"));
        checkHier( encoder, 'F' );

        encoder.encode("G", List.of("C"));
        checkHier( encoder, 'G' );

        encoder.encode("H", List.of("D"));
        checkHier( encoder, 'H' );

        encoder.encode("I", List.of("D"));
        checkHier( encoder, 'I' );
//
        encoder.encode( "J", Arrays.asList( "E", "F" ) );
        checkHier( encoder, 'J' );

        encoder.encode( "K", Arrays.asList( "E", "F" ) );
        checkHier( encoder, 'K' );

        encoder.encode("L", List.of("G"));
        checkHier( encoder, 'L' );

        encoder.encode("M", List.of("G"));
        checkHier( encoder, 'M' );

        encoder.encode( "N", Arrays.asList( "I", "L" ) );
        checkHier( encoder, 'N' );

        encoder.encode( "O", Arrays.asList( "H", "M" ) );
        checkHier( encoder, 'O' );

        LOGGER.debug( encoder.toString() );

        Collection<BitSet> codes = encoder.getSortedMap().values();
        Iterator<BitSet> iter = codes.iterator();
        Long last = -1L;
        for ( int j = 0; j < codes.size() -1; j++ ) {
            BitSet ns = iter.next();
            Long next = toLong( ns );
            LOGGER.debug( next.toString() );
            assertThat(next).isGreaterThan(last);
            last = next;
        }
    }

    private Long toLong( BitSet ns ) {
        long l = 0L;
        for ( int j = 0; j < ns.length(); j++ ) {
            if ( ns.get( j ) ) {
                l += ( 1 << j );
            }
        }
        return l;
    }

    private void checkHier( HierarchyEncoder<String> encoder, char fin ) {


        List<String>[] sups = new ArrayList[ fin - 'A' + 1 ];
        for ( int j = 'A'; j <= fin; j++ ) {
            sups[ j - 'A' ] = ((HierarchyEncoderImpl<String>) encoder).ancestorValues( "" + (char) j );
        };

        for ( int j = 'A' ; j <  'A' + sups.length ; j++ ) {
            for ( int k = 'A'; k < 'A' + sups.length; k++ ) {
                String x = "" + (char) j;
                String y = "" + (char) k;
                BitSet xcode = encoder.getCode( x );
                BitSet ycode = encoder.getCode( y );

                int subOf = ((HierarchyEncoderImpl<String>) encoder).superset(xcode, ycode);

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
        HierarchyEncoderImpl<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("A"));
        encoder.encode("D", List.of("A"));
        encoder.encode("E", List.of("B"));
        encoder.encode("F", List.of("C"));
        encoder.encode("G", List.of("D"));
        encoder.encode("H", List.of("D"));
        encoder.encode("I", List.of("E"));
        encoder.encode("J", List.of("F"));
        encoder.encode("K", List.of("G"));
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
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "T", Collections.emptyList());
        encoder.encode("A", List.of("T"));
        encoder.encode("B", List.of("T"));
        encoder.encode("C", List.of("T"));
        encoder.encode("D", List.of("C"));
        encoder.encode( "F", Arrays.asList( "B", "C" ) );


        LOGGER.debug( encoder.toString() );

        encoder.encode( "Z", Arrays.asList( "A", "B", "D" ) );

        LOGGER.debug( encoder.toString() );

        assertThat(((HierarchyEncoderImpl<String>) encoder).superset(encoder.getCode("Z"), encoder.getCode("F"))).isLessThan(0);
        assertThat(((HierarchyEncoderImpl<String>) encoder).superset(encoder.getCode("F"), encoder.getCode("Z"))).isLessThan(0);
    }





    @Test
    public void testDecoderAncestors() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "Thing", Collections.emptyList());
        encoder.encode("A", List.of("Thing"));
        encoder.encode("Z", List.of("Thing"));
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode("P", List.of("Thing"));
        encoder.encode("Q", List.of("Thing"));
        encoder.encode("R", List.of("Thing"));
        encoder.encode("S", List.of("R"));
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        LOGGER.debug( encoder.toString() );

        BitSet b;
        Collection<String> x;

        b = parseBitSet( "1100111" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).contains("A");
        assertThat(x).contains("Z");
        assertThat(x).contains("C");
        assertThat(x).contains("Q");
        assertThat(x).contains("T");
        assertThat(x).contains("R");
        assertThat(x).contains("S");
        assertThat(x).contains("M");
        assertThat(x).contains("Thing");
        assertThat(x).hasSize(9);


        b = parseBitSet( "100000" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("Q");
        assertThat(x).contains("Thing");

        b = parseBitSet( "1111" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(6);
        assertThat(x).contains("A");
        assertThat(x).contains("Z");
        assertThat(x).contains("B");
        assertThat(x).contains("C");
        assertThat(x).contains("N");
        assertThat(x).contains("Thing");

        b = parseBitSet( "111" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(4);
        assertThat(x).contains("A");
        assertThat(x).contains("Z");
        assertThat(x).contains("C");
        assertThat(x).contains("Thing");

        b = parseBitSet( "1" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("A");
        assertThat(x).contains("Thing");

        b = parseBitSet( "10" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("Z");
        assertThat(x).contains("Thing");

        b = parseBitSet( "0" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("Thing");


        b = parseBitSet( "1011" );
        x = encoder.upperAncestors(b);
        LOGGER.debug( "ANC " + x );

        assertThat(x).hasSize(4);
        assertThat(x).contains("Thing");
        assertThat(x).contains("A");
        assertThat(x).contains("B");
        assertThat(x).contains("Z");

    }


    @Test
    public void testDecoderDescendants() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "Thing", Collections.emptyList());
        encoder.encode("A", List.of("Thing"));
        encoder.encode("Z", List.of("Thing"));
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode("P", List.of("Thing"));
        encoder.encode("Q", List.of("Thing"));
        encoder.encode("R", List.of("Thing"));
        encoder.encode("S", List.of("R"));
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        LOGGER.debug( encoder.toString() );

        BitSet b = parseBitSet( "111" );
        Collection<String> x = encoder.lowerDescendants(b);
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(3);
        assertThat(x).contains("C");
        assertThat(x).contains("N");
        assertThat(x).contains("T");


        b = parseBitSet( "10" );
        x = encoder.lowerDescendants(b);
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(5);
        assertThat(x).contains("C");
        assertThat(x).contains("N");
        assertThat(x).contains("T");
        assertThat(x).contains("Z");
        assertThat(x).contains("B");


        b = parseBitSet( "100000" );
        x = encoder.lowerDescendants(b);
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(4);
        assertThat(x).contains("Q");
        assertThat(x).contains("T");
        assertThat(x).contains("M");
        assertThat(x).contains("O");




        b = parseBitSet( "100010" );
        x = encoder.lowerDescendants(b);
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("T");

        b = parseBitSet( "1111" );
        x = encoder.lowerDescendants(b);
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("N");


        b = parseBitSet( "1" );
        x = encoder.lowerDescendants(b);
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(5);
        assertThat(x).contains("A");
        assertThat(x).contains("B");
        assertThat(x).contains("C");
        assertThat(x).contains("N");
        assertThat(x).contains("T");

        LOGGER.debug(" +*******************************+ ");

        x = encoder.lowerDescendants(new BitSet());
        LOGGER.debug( "DESC " + x );

        assertThat(x).hasSize(13);
        assertThat(x).contains("Z");
        assertThat(x).contains("Thing");

    }


    @Test
    public void testHierEncoderDecoderLower() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "Thing", Collections.emptyList());
        encoder.encode("A", List.of("Thing"));
        encoder.encode("Z", List.of("Thing"));
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode("P", List.of("Thing"));
        encoder.encode("Q", List.of("Thing"));
        encoder.encode("R", List.of("Thing"));
        encoder.encode("S", List.of("R"));
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        LOGGER.debug( encoder.toString() );

        Collection<String> x;

        x = encoder.lowerBorder( encoder.metMembersCode(List.of("B")));
        LOGGER.debug( "GCS " + x );
        assertThat(x).hasSize(1);
        assertThat(x).contains("B");

        x = encoder.immediateChildren( encoder.metMembersCode(List.of("B")));
        LOGGER.debug( "GCS " + x );
        assertThat(x).hasSize(1);
        assertThat(x).contains("N");



        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "Z", "Q" ) ) );
        LOGGER.debug( "GCS " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("T");

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "Z", "Q" ) ) );
        LOGGER.debug( "GCS " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("T");




        x = encoder.lowerBorder( encoder.metMembersCode( Arrays.asList( "A", "Z" ) ) );
        LOGGER.debug( "GCS " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("B");
        assertThat(x).contains("C");

        x = encoder.immediateChildren( encoder.metMembersCode( Arrays.asList( "A", "Z" ) ) );
        LOGGER.debug( "GCS " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("B");
        assertThat(x).contains("C");




        x = encoder.lowerBorder( encoder.metMembersCode(List.of("Thing")));
        LOGGER.debug( "GCS " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("Thing");

        x = encoder.immediateChildren( encoder.metMembersCode(List.of("Thing")));
        LOGGER.debug( "GCS " + x );

        assertThat(x).hasSize(5);
        assertThat(x).contains("A");
        assertThat(x).contains("Z");
        assertThat(x).contains("P");
        assertThat(x).contains("Q");
        assertThat(x).contains("R");

    }



    @Test
    public void testHierEncoderDecoderUpper() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<>();

        encoder.encode( "Thing", Collections.emptyList());
        encoder.encode("A", List.of("Thing"));
        encoder.encode("Z", List.of("Thing"));
        encoder.encode( "B", Arrays.asList( "A", "Z" ) );
        encoder.encode( "C", Arrays.asList( "A", "Z" ) );
        encoder.encode( "N", Arrays.asList( "B", "C" ) );
        encoder.encode("P", List.of("Thing"));
        encoder.encode("Q", List.of("Thing"));
        encoder.encode("R", List.of("Thing"));
        encoder.encode("S", List.of("R"));
        encoder.encode( "T", Arrays.asList( "C", "Q" ) );
        encoder.encode( "M", Arrays.asList( "R", "Q" ) );
        encoder.encode( "O", Arrays.asList( "M", "P" ) );

        LOGGER.debug( encoder.toString() );

        Collection<String> x = encoder.upperBorder( encoder.metMembersCode(List.of("B")));
        LOGGER.debug( "LCS " + x );
        assertThat(x).hasSize(1);
        assertThat(x).contains("B");

        x = encoder.immediateParents( encoder.metMembersCode(List.of("B")));
        LOGGER.debug( "LCS " + x );
        assertThat(x).hasSize(2);
        assertThat(x).contains("A");
        assertThat(x).contains("Z");



        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "Z", "Q" ) ) );
        LOGGER.debug( "LCS " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("Thing");

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "Z", "Q" ) ) );
        LOGGER.debug( "LCS " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("Thing");



        x = encoder.upperBorder( encoder.jointMembersCode( Arrays.asList( "B", "C" ) ) );
        LOGGER.debug( "LCS " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("A");
        assertThat(x).contains("Z");

        x = encoder.immediateParents( encoder.jointMembersCode( Arrays.asList( "B", "C" ) ) );
        LOGGER.debug( "LCS " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("A");
        assertThat(x).contains("Z");


        x = encoder.upperBorder( encoder.jointMembersCode(List.of("T")));
        LOGGER.debug( "LCS " + x );

        assertThat(x).hasSize(1);
        assertThat(x).contains("T");

        x = encoder.immediateParents( encoder.jointMembersCode(List.of("T")));
        LOGGER.debug( "LCS " + x );

        assertThat(x).hasSize(2);
        assertThat(x).contains("C");
        assertThat(x).contains("Q");


    }






    @Test
    public void testClassInstanceHierarchies() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        BitSet ak = encoder.encode( "A", Collections.emptyList());
        BitSet bk = encoder.encode("B", List.of("A"));
        BitSet ck = encoder.encode("C", List.of("A"));
        BitSet dk = encoder.encode("D", List.of("B"));
        BitSet ek = encoder.encode("E", List.of("B"));
        BitSet fk = encoder.encode("F", List.of("C"));
        BitSet gk = encoder.encode("G", List.of("C"));
        BitSet hk = encoder.encode("H", List.of("D"));
        BitSet ik = encoder.encode("I", List.of("D"));
        BitSet jk = encoder.encode( "J", Arrays.asList( "E", "F" ) );
        BitSet kk = encoder.encode( "K", Arrays.asList( "E", "F" ) );
        BitSet lk = encoder.encode("L", List.of("G"));
        BitSet mk = encoder.encode("M", List.of("G"));
        BitSet nk = encoder.encode( "N", Arrays.asList( "I", "L" ) );
        BitSet ok = encoder.encode( "O", Arrays.asList( "H", "M" ) );

        LOGGER.debug( encoder.toString() );

        CodedHierarchy<String> types = new IndexedTypeHierarchy<String>("A", new BitSet(), "ZZZZ", encoder.getBottom() );

        types.addMember( "A", ak );
        types.addMember( "c", ck );
        types.addMember( "f", fk );
        types.addMember( "j", jk );
        types.addMember( "k", kk );
        types.addMember( "n", nk );
        types.addMember( "o", ok );
        types.addMember( "h", hk );


        LOGGER.debug( types.toString() );

        assertThat(types.children("A")).isEqualTo(Arrays.asList("c", "h"));
        assertThat(types.children("c")).isEqualTo(Arrays.asList("f", "n", "o"));
        assertThat(types.children("f")).isEqualTo(Arrays.asList("j", "k"));
        assertThat(types.children("j")).isEqualTo(List.of("ZZZZ"));
        assertThat(types.children("k")).isEqualTo(List.of("ZZZZ"));
        assertThat(types.children("n")).isEqualTo(List.of("ZZZZ"));
        assertThat(types.children("o")).isEqualTo(List.of("ZZZZ"));
        assertThat(types.children("h")).isEqualTo(List.of("o"));
        assertThat(types.children("ZZZZ")).isEqualTo(List.of());

        assertThat(types.parents("a")).isEqualTo(List.of());
        assertThat(types.parents("c")).isEqualTo(List.of("A"));
        assertThat(types.parents("f")).isEqualTo(List.of("c"));
        assertThat(types.parents("j")).isEqualTo(List.of("f"));
        assertThat(types.parents("k")).isEqualTo(List.of("f"));
        assertThat(types.parents("n")).isEqualTo(List.of("c"));
        assertThat(types.parents("o")).isEqualTo(Arrays.asList("c", "h"));
        assertThat(types.parents("h")).isEqualTo(List.of("A"));
        assertThat(types.parents("ZZZZ")).isEqualTo(Arrays.asList("j", "k", "n", "o"));


        BitSet pk = encoder.encode("P", List.of("O"));
        types.addMember( "p", pk );

        LOGGER.debug( types.toString() );

        assertThat(types.parents("p")).isEqualTo(List.of("o"));
        assertThat(types.parents("ZZZZ")).isEqualTo(Arrays.asList("j", "k", "n", "p"));
        assertThat(types.children("p")).isEqualTo(List.of("ZZZZ"));


        types.removeMember( "o" );

        LOGGER.debug( types.toString() );

        assertThat(types.parents("p")).isEqualTo(Arrays.asList("c", "h"));
        assertThat(types.children("c")).isEqualTo(Arrays.asList("f", "n", "p"));
        assertThat(types.children("f")).isEqualTo(Arrays.asList("j", "k"));
        assertThat(types.children("n")).isEqualTo(List.of("ZZZZ"));
        assertThat(types.children("p")).isEqualTo(List.of("ZZZZ"));
        assertThat(types.children("h")).isEqualTo(List.of("p"));

    }



    @Test
    public void testUnwantedCodeOverriding() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        BitSet ak = encoder.encode( "A", Collections.emptyList());
        BitSet ck = encoder.encode("C", List.of("A"));
        BitSet dk = encoder.encode("D", List.of("A"));
        BitSet gk = encoder.encode( "G", Arrays.asList( "C", "D" ) );
        BitSet bk = encoder.encode("B", List.of("A"));
        BitSet ek = encoder.encode("E", List.of("B"));
        BitSet ik = encoder.encode( "I", Arrays.asList( "E", "C" ) );
        BitSet fk = encoder.encode( "F", Arrays.asList( "B", "C" ) );
        BitSet jk = encoder.encode( "J", Arrays.asList( "F", "D" ) );
        BitSet lk = encoder.encode("L", List.of("J"));

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet ok = encoder.encode("O", List.of("L"));

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet kk = encoder.encode( "K", Arrays.asList( "F", "G" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet mk = encoder.encode( "M", Arrays.asList( "J", "K" ) );

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet nk = encoder.encode("N", List.of("K"));

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet hk = encoder.encode("H", List.of("F"));

        assertThat(encoder.getCode("L")).isNotNull();

        BitSet pk = encoder.encode("P", List.of("A"));

        assertThat(encoder.getCode("L")).isNotNull();

        LOGGER.debug( encoder.toString() );
        assertThat(encoder.size()).isEqualTo(16);
    }



    @Test
    public void testDeepTree() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        encoder.encode( "A", Collections.emptyList());

        encoder.encode("B", List.of("A"));

        encoder.encode("C", List.of("A"));
        encoder.encode("D", List.of("C"));
        encoder.encode("E", List.of("D"));
        encoder.encode("F", List.of("D"));
        encoder.encode("G", List.of("C"));
        encoder.encode("H", List.of("G"));
        encoder.encode("I", List.of("G"));
        encoder.encode("J", List.of("C"));
        encoder.encode("K", List.of("C"));

        encoder.encode("L", List.of("B"));
        encoder.encode("M", List.of("B"));

        encoder.encode("N", List.of("A"));
        encoder.encode("O", List.of("N"));



        LOGGER.debug( encoder.toString() );

        checkHier( encoder, 'O' );
    }



    @Test
    public void testNestedTree() {
        HierarchyEncoder<String> encoder = new HierarchyEncoderImpl<String>();

        encoder.encode( "A", Collections.emptyList());
        encoder.encode("B", List.of("A"));
        encoder.encode("C", List.of("B"));
        encoder.encode("D", List.of("B"));
        encoder.encode("E", List.of("D"));
        encoder.encode("F", List.of("E"));
        encoder.encode("G", List.of("E"));
        encoder.encode("H", List.of("G"));
        encoder.encode("I", List.of("H"));
        encoder.encode("J", List.of("E"));
        encoder.encode("K", List.of("J"));
        encoder.encode("L", List.of("K"));
        encoder.encode("M", List.of("J"));
        encoder.encode("N", List.of("M"));
        encoder.encode("O", List.of("J"));
        encoder.encode("P", List.of("O"));
        encoder.encode("Q", List.of("J"));
        encoder.encode("R", List.of("Q"));
        encoder.encode("S", List.of("B"));
        encoder.encode("T", List.of("S"));
        encoder.encode("U", List.of("T"));
        encoder.encode("V", List.of("B"));
        encoder.encode("W", List.of("V"));
        encoder.encode("X", List.of("W"));

        LOGGER.debug( encoder.toString() );

        encoder.encode( "Y", Arrays.asList( "F", "W") );

        LOGGER.debug( encoder.toString() );

        checkHier( encoder, (char) ( 'A' + encoder.size() - 1 ) );

    }

}