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
package org.drools.core.util;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexedHashtableIteratorTest {

    @Test
    public void testCanReachAllEntriesInLastTableRowRightTupleIndexHashTable() {
        // Construct a table with one row, containing one list, containing three entries.
        int numEntries = 3;
        TupleList[] table = new TupleList[3];
        
        TupleList rtList = new TupleList();
        table[0] = rtList;        
        for ( int i = 0; i < numEntries; i++ ) {
            TupleImpl rightTuple = new RightTuple();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        rtList = new TupleList();
        table[2] = rtList;
        for ( int i = 0; i < numEntries; i++ ) {
            TupleImpl rightTuple = new RightTuple();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        rtList = new TupleList();
        table[2].setNext( rtList );
        for ( int i = 0; i < numEntries; i++ ) {
            TupleImpl rightTuple = new RightTuple();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        // test fast
        TupleIndexHashTable.FullFastIterator iter = new TupleIndexHashTable.FullFastIterator( table );
        List<RightTuple>                     list = new ArrayList<>();
        for (RightTuple rightTuple = (RightTuple) iter.next(null); rightTuple != null; rightTuple = (RightTuple) iter.next(rightTuple) ) {
            assertThat(contains(list, rightTuple)).isFalse(); // ensure no duplicate
            list.add( rightTuple );
        }
        
        // test normal
        TupleIndexHashTable rthTable = new TupleIndexHashTable();
        rthTable.init( table, 3, numEntries * 3 );
        TupleIndexHashTable.FieldIndexHashTableFullIterator iter2 = new TupleIndexHashTable.FieldIndexHashTableFullIterator( rthTable );
        list = new ArrayList<>();
        for (RightTuple rightTuple = (RightTuple) iter2.next(); rightTuple != null; rightTuple = (RightTuple) iter2.next() ) {
            assertThat(contains(list, rightTuple)).isFalse(); // ensure no duplicate
            list.add( rightTuple );
        }

        assertThat(list).hasSize(numEntries * 3);
    }

    @Test
    public void testCanReachAllEntriesInLastTableRowLeftTupleIndexHashTable() {
        // Construct a table with one row, containing one list, containing three entries.
        int numEntries = 3;
        TupleList[] table = new TupleList[3];
        
        TupleList rtList = new TupleList();
        table[0] = rtList;        
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTuple  leftTuple = new LeftTuple();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        rtList = new TupleList();
        table[2] = rtList;
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTuple leftTuple = new LeftTuple();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        rtList = new TupleList();
        table[2].setNext( rtList );
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTuple leftTuple = new LeftTuple();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        // test fast
        TupleIndexHashTable.FullFastIterator iter = new TupleIndexHashTable.FullFastIterator( table );
        List<LeftTuple> list = new ArrayList<LeftTuple>();
        for ( LeftTuple leftTuple = (LeftTuple) iter.next( null ); leftTuple != null; leftTuple = (LeftTuple) iter.next( leftTuple ) ) {
            assertThat(contains(list, leftTuple)).isFalse(); // ensure no duplicate
            list.add( leftTuple );
        }

        assertThat(list).hasSize(numEntries * 3);
        
        // test normal
        TupleIndexHashTable lthTable = new TupleIndexHashTable();
        lthTable.init( table, 3, numEntries * 3 );
        TupleIndexHashTable.FieldIndexHashTableFullIterator iter2 = new TupleIndexHashTable.FieldIndexHashTableFullIterator( lthTable );
        list = new ArrayList<LeftTuple>();
        for ( LeftTuple leftTuple = (LeftTuple) iter2.next( ); leftTuple != null; leftTuple = (LeftTuple) iter2.next( ) ) {
            assertThat(contains(list, leftTuple)).isFalse(); // ensure no duplicate
            list.add( leftTuple );
        }

        assertThat(list).hasSize(numEntries * 3);        
    }
    
    public static boolean contains(List list,
                                   Object object) {
        for ( Object o : list ) {
            if ( o == object ) {
                return true;
            }
        }
        return false;
    }    
}
