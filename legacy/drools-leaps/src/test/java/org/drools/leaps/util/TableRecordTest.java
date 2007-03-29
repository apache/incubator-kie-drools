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

import junit.framework.TestCase;

/**
 * @author Alexander Bagerman
 */

public class TableRecordTest extends TestCase {

    public void testConstractor() {
        final String object = new String( "test object" );
        final TableRecord record = new TableRecord( object );
        assertEquals( object,
                      record.object );
    }

    public void testLeft() {
        final String object1 = new String( "test object1" );
        final String object2 = new String( "test object2" );
        final TableRecord record1 = new TableRecord( object1 );
        final TableRecord record2 = new TableRecord( object2 );
        record1.left = record2;
        assertEquals( object2,
                      record1.left.object );
    }

    public void testRight() {
        final String object1 = new String( "test object1" );
        final String object2 = new String( "test object2" );
        final TableRecord record1 = new TableRecord( object1 );
        final TableRecord record2 = new TableRecord( object2 );
        record1.right = record2;
        assertEquals( object2,
                      record1.right.object );
    }

}