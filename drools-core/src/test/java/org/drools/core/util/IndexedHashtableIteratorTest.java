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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.util.index.LeftTupleIndexHashTable;
import org.drools.core.util.index.LeftTupleList;
import org.drools.core.util.index.RightTupleIndexHashTable;
import org.drools.core.util.index.RightTupleList;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.RightTuple;
import org.junit.Test;

public class IndexedHashtableIteratorTest {

    @Test
    public void testCanReachAllEntriesInLastTableRowRightTupleIndexHashTable() {
        // Construct a table with one row, containing one list, containing three entries.
        int numEntries = 3;
        RightTupleList[] table = new RightTupleList[3];
        
        RightTupleList rtList = new RightTupleList();
        table[0] = rtList;        
        for ( int i = 0; i < numEntries; i++ ) {
            RightTuple rightTuple = new RightTuple();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        rtList = new RightTupleList();
        table[2] = rtList;
        for ( int i = 0; i < numEntries; i++ ) {
            RightTuple rightTuple = new RightTuple();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        rtList = new RightTupleList();
        table[2].setNext( rtList );
        for ( int i = 0; i < numEntries; i++ ) {
            RightTuple rightTuple = new RightTuple();
            rightTuple.setMemory( rtList );
            rtList.add( rightTuple );
        }

        // test fast
        RightTupleIndexHashTable.FullFastIterator iter = new RightTupleIndexHashTable.FullFastIterator( table );
        List<RightTuple> list = new ArrayList<RightTuple>();
        for ( RightTuple rightTuple = (RightTuple) iter.next( null ); rightTuple != null; rightTuple = (RightTuple) iter.next( rightTuple ) ) {
            assertFalse( contains( list, rightTuple ) ); // ensure no duplicate
            list.add( rightTuple );
        }
        
        // test normal
        RightTupleIndexHashTable rthTable = new RightTupleIndexHashTable();    
        rthTable.init( table, 3, numEntries * 3 );
        RightTupleIndexHashTable.FieldIndexHashTableFullIterator iter2 = new RightTupleIndexHashTable.FieldIndexHashTableFullIterator( rthTable );
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
        LeftTupleList[] table = new LeftTupleList[3];
        
        LeftTupleList rtList = new LeftTupleList();
        table[0] = rtList;        
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTupleImpl  leftTuple = new LeftTupleImpl();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        rtList = new LeftTupleList();
        table[2] = rtList;
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTupleImpl leftTuple = new LeftTupleImpl();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        rtList = new LeftTupleList();
        table[2].setNext( rtList );
        for ( int i = 0; i < numEntries; i++ ) {
            LeftTupleImpl leftTuple = new LeftTupleImpl();
            leftTuple.setMemory( rtList );
            rtList.add( leftTuple );
        }

        // test fast
        LeftTupleIndexHashTable.FullFastIterator iter = new LeftTupleIndexHashTable.FullFastIterator( table );
        List<LeftTupleImpl> list = new ArrayList<LeftTupleImpl>();
        for ( LeftTupleImpl leftTuple = (LeftTupleImpl) iter.next( null ); leftTuple != null; leftTuple = (LeftTupleImpl) iter.next( leftTuple ) ) {
            assertFalse( contains( list, leftTuple ) ); // ensure no duplicate
            list.add( leftTuple );
        }

        assertEquals( numEntries * 3, list.size() );
        
        // test normal
        LeftTupleIndexHashTable lthTable = new LeftTupleIndexHashTable();    
        lthTable.init( table, 3, numEntries * 3 );
        LeftTupleIndexHashTable.FieldIndexHashTableFullIterator iter2 = new LeftTupleIndexHashTable.FieldIndexHashTableFullIterator( lthTable );
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
