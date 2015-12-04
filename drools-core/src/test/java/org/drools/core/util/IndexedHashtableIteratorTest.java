/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util;

import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleList;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class IndexedHashtableIteratorTest {

    @Test
    public void testCanReachAllEntriesInLastTableRowRightTupleIndexHashTable() {
        // Construct a table with one row, containing one list, containing three entries.
        int numEntries = 3;
        TupleList[] table = new TupleList[3];
        
        TupleList rtList = new TupleList();
        table[0] = rtList;        
        for ( int i = 0; i < numEntries; i++ ) {
            RightTuple rightTuple = new RightTupleImpl();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        rtList = new TupleList();
        table[2] = rtList;
        for ( int i = 0; i < numEntries; i++ ) {
            RightTuple rightTuple = new RightTupleImpl();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        rtList = new TupleList();
        table[2].setNext( rtList );
        for ( int i = 0; i < numEntries; i++ ) {
            RightTuple rightTuple = new RightTupleImpl();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        // test fast
        TupleIndexHashTable.FullFastIterator iter = new TupleIndexHashTable.FullFastIterator( table );
        List<RightTuple> list = new ArrayList<RightTuple>();
        for ( RightTuple rightTuple = (RightTuple) iter.next( null ); rightTuple != null; rightTuple = (RightTuple) iter.next( rightTuple ) ) {
            assertFalse( contains( list, rightTuple ) ); // ensure no duplicate
            list.add( rightTuple );
        }
        
        // test normal
        TupleIndexHashTable rthTable = new TupleIndexHashTable();
        rthTable.init( table, 3, numEntries * 3 );
        TupleIndexHashTable.FieldIndexHashTableFullIterator iter2 = new TupleIndexHashTable.FieldIndexHashTableFullIterator( rthTable );
        list = new ArrayList<RightTuple>();
        for ( RightTuple rightTuple = (RightTuple) iter2.next( ); rightTuple != null; rightTuple = (RightTuple) iter2.next( ) ) {
            assertFalse( contains( list, rightTuple ) ); // ensure no duplicate
            list.add( rightTuple );
        }
        
        assertEquals( numEntries * 3, list.size() );
    }

    @Test
    public void testCanReachAllEntriesInLastTableRowLeftTupleIndexHashTable() {
        // Construct a table with one row, containing one list, containing three entries.
        int numEntries = 3;
        TupleList[] table = new TupleList[3];
        
        TupleList rtList = new TupleList();
        table[0] = rtList;        
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTupleImpl  leftTuple = new LeftTupleImpl();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        rtList = new TupleList();
        table[2] = rtList;
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTupleImpl leftTuple = new LeftTupleImpl();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        rtList = new TupleList();
        table[2].setNext( rtList );
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTupleImpl leftTuple = new LeftTupleImpl();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        // test fast
        TupleIndexHashTable.FullFastIterator iter = new TupleIndexHashTable.FullFastIterator( table );
        List<LeftTupleImpl> list = new ArrayList<LeftTupleImpl>();
        for ( LeftTupleImpl leftTuple = (LeftTupleImpl) iter.next( null ); leftTuple != null; leftTuple = (LeftTupleImpl) iter.next( leftTuple ) ) {
            assertFalse( contains( list, leftTuple ) ); // ensure no duplicate
            list.add( leftTuple );
        }

        assertEquals( numEntries * 3, list.size() );
        
        // test normal
        TupleIndexHashTable lthTable = new TupleIndexHashTable();
        lthTable.init( table, 3, numEntries * 3 );
        TupleIndexHashTable.FieldIndexHashTableFullIterator iter2 = new TupleIndexHashTable.FieldIndexHashTableFullIterator( lthTable );
        list = new ArrayList<LeftTupleImpl>();
        for ( LeftTupleImpl leftTuple = (LeftTupleImpl) iter2.next( ); leftTuple != null; leftTuple = (LeftTupleImpl) iter2.next( ) ) {
            assertFalse( contains( list, leftTuple ) ); // ensure no duplicate
            list.add( leftTuple );
        }
        
        assertEquals( numEntries * 3, list.size() );        
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
