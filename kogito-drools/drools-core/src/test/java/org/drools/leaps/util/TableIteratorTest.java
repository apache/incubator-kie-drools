package org.drools.leaps.util;

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

import java.util.Iterator;

import junit.framework.TestCase;

import org.drools.common.DefaultFactHandle;
import org.drools.leaps.conflict.LoadOrderConflictResolver;

/**
 * @author Alexander Bagerman
 */

public class TableIteratorTest extends TestCase {

    DefaultFactHandle h1;

    DefaultFactHandle h1000;

    DefaultFactHandle h100;

    DefaultFactHandle h10;

    Table  testTable;

    protected void setUp() {
        this.testTable = new Table( LoadOrderConflictResolver.getInstance() );
        this.h1 = new DefaultFactHandle( 1,
                              "1" );
        this.h1000 = new DefaultFactHandle( 1000,
                                 "1000" );
        this.h100 = new DefaultFactHandle( 100,
                                "100" );
        this.h10 = new DefaultFactHandle( 10,
                               "10" );
    }

    /*
     * Test method for
     * 'org.drools..util.TableIterator.TableIterator()'
     */
    public void testTableIterator() {
        final IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( null,
                                                            null );
        assertFalse( it.hasNext() );
        assertTrue( it.isEmpty() );
    }

    public void testGetDominantFactIterator() {
        final Iterator it = Table.singleItemIterator( this.h1000 );
        assertTrue( it.hasNext() );
        assertEquals( it.next(),
                      this.h1000 );
        assertFalse( it.hasNext() );
    }

    /*
     * Test method for
     * 'org.drools..util.TableIterator.TableIterator(TableRecord)'
     */
    public void testTableIteratorTableRecord() {
        final IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( new TableRecord( this.h1 ) );
        assertTrue( it.hasNext() );
        assertFalse( it.isEmpty() );
        assertEquals( this.h1,
                      it.next() );
        assertFalse( it.hasNext() );
        assertFalse( it.isEmpty() );

    }

    /*
     * Test method for
     * 'org.drools..util.TableIterator.TableIterator(TableRecord,
     * TableRecord, TableRecord)'
     */
    public void testTableIteratorTableRecordTableRecordTableRecord() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertTrue( it.hasNext() );
        assertFalse( it.isEmpty() );
        assertEquals( this.h1,
                      it.next() );
        assertFalse( it.hasNext() );
        assertFalse( it.isEmpty() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertFalse( it.isEmpty() );
        assertTrue( it.hasNext() );
        assertEquals( this.h1000,
                      it.next() );
        assertTrue( it.hasNext() );
        assertEquals( this.h100,
                      it.next() );
        assertTrue( it.hasNext() );
        assertEquals( this.h10,
                      it.next() );
        assertTrue( it.hasNext() );
        assertEquals( this.h1,
                      it.next() );
        assertFalse( it.hasNext() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.tailRecord );
        assertFalse( it.isEmpty() );
        assertTrue( it.hasNext() );
        assertTrue( it.hasNext() );
        assertEquals( this.h1,
                      it.next() );
        assertFalse( it.hasNext() );

    }

    /*
     * Test method for 'org.drools..util.TableIterator.isEmpty()'
     */
    public void testIsEmpty() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertFalse( it.isEmpty() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertFalse( it.isEmpty() );
        it = new IteratorFromPositionToTableStart( this.testTable.tailRecord,
                                    this.testTable.headRecord );
        assertFalse( it.isEmpty() );
        it = new IteratorFromPositionToTableStart( null,
                                    null );
        assertTrue( it.isEmpty() );

    }

    /*
     * Test method for 'org.drools..util.TableIterator.reset()'
     */
    public void testReset() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.next() );
        it.reset();
        assertEquals( this.h1,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertEquals( this.h1000,
                      it.next() );
        it.reset();
        assertEquals( this.h1000,
                      it.next() );
        it.next();
        it.next();
        it.reset();
        assertEquals( this.h1000,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.tailRecord );
        it.reset();
        assertEquals( this.h1000,
                      it.next() );
        it = new IteratorFromPositionToTableStart( new TableRecord( this.h1 ) );
        assertEquals( this.h1,
                      it.next() );
        it.reset();
        assertEquals( this.h1,
                      it.next() );

    }

    /*
     * Test method for 'org.drools..util.TableIterator.hasNext()'
     */
    public void testHasNext() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertTrue( it.hasNext() );
        assertFalse( it.isEmpty() );
        assertEquals( this.h1,
                      it.next() );
        assertFalse( it.hasNext() );
        assertFalse( it.isEmpty() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertTrue( it.hasNext() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.tailRecord );
        assertTrue( it.hasNext() );
        it = new IteratorFromPositionToTableStart( null,
                                    null );
        assertFalse( it.hasNext() );
        it = new IteratorFromPositionToTableStart( new TableRecord( this.h1 ) );
        assertTrue( it.hasNext() );

    }

    /*
     * Test method for 'org.drools..util.TableIterator.next()'
     */
    public void testNext() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertEquals( this.h1000,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.next() );
        it = new IteratorFromPositionToTableStart( new TableRecord( this.h1 ) );
        assertEquals( this.h1,
                      it.next() );
    }

    /*
     * Test method for 'org.drools..util.TableIterator.current()'
     */
    public void testCurrent() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertEquals( this.h1000,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.next() );
        it = new IteratorFromPositionToTableStart( new TableRecord( this.h1 ) );
        assertEquals( this.h1,
                      it.next() );
    }

    /*
     * Test method for 'org.drools..util.TableIterator.peekNext()'
     */
    public void testPeekNext() {
        this.testTable.add( this.h1 );
        IteratorFromPositionToTableStart it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                                      this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.peekNext() );
        assertEquals( this.h1,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.headRecord );
        assertEquals( this.h1000,
                      it.peekNext() );
        assertEquals( this.h1000,
                      it.next() );
        this.testTable.clear();
        this.testTable.add( this.h1 );
        this.testTable.add( this.h1000 );
        this.testTable.add( this.h10 );
        this.testTable.add( this.h100 );
        it = new IteratorFromPositionToTableStart( this.testTable.headRecord,
                                    this.testTable.tailRecord );
        assertEquals( this.h1,
                      it.peekNext() );
        assertEquals( this.h1,
                      it.next() );
        it = new IteratorFromPositionToTableStart( new TableRecord( this.h1 ) );
        assertEquals( this.h1,
                      it.peekNext() );
        assertEquals( this.h1,
                      it.next() );

    }

}