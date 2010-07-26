/**
 * Copyright 2010 JBoss Inc
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

import org.drools.core.util.ObjectHashMap;

import junit.framework.TestCase;

public class ObjectHashMapTest2 extends TestCase {

    public ObjectHashMapTest2() {
        super();
    }

    public void testJUHashmap() {
        final java.util.HashMap map = new java.util.HashMap();
        assertNotNull( map );
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val );
            assertEquals( val,
                          map.get( key ) );
        }
    }

    public void testStringData() {
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val );
            assertEquals( val,
                          map.get( key ) );
        }
    }

    public void testStringDataDupFalse() {
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final int count = 10000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val,
                     false );
            assertEquals( val,
                          map.get( key ) );
        }
    }

    public void testIntegerData() {
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final Integer key = new Integer( idx );
            final Integer val = new Integer( idx );
            map.put( key,
                     val );
            assertEquals( val,
                          map.get( key ) );
        }
    }

    public void testJUHashMap1() {
        final int count = 100000;
        final java.util.HashMap map = new java.util.HashMap();
        assertNotNull( map );
        final long start = System.currentTimeMillis();
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval );
        }
        final long end = System.currentTimeMillis();
        System.out.println( "java.util.HashMap put(key,value) ET - " + ((end - start)) );
    }

    public void testStringData2() {
        final int count = 100000;
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final long start = System.currentTimeMillis();
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval );
        }
        final long end = System.currentTimeMillis();
        System.out.println( "Custom ObjectHashMap put(key,value) ET - " + ((end - start)) );
    }

    public void testStringData3() {
        final int count = 100000;
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval );
        }
        final long start = System.currentTimeMillis();
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            map.get( key );
        }
        final long end = System.currentTimeMillis();
        System.out.println( "Custom ObjectHashMap get(key) ET - " + ((end - start)) );
    }

    public void testJUHashMap2() {
        final int count = 100000;
        final java.util.HashMap map = new java.util.HashMap();
        assertNotNull( map );
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval );
        }
        final long start = System.currentTimeMillis();
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            map.get( key );
        }
        final long end = System.currentTimeMillis();
        System.out.println( "java.util.HashMap get(key) ET - " + ((end - start)) );
    }

    public void testStringData4() {
        final int count = 100000;
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval );
        }
        final long start = System.currentTimeMillis();
        final org.drools.core.util.Iterator itr = map.iterator();
        Object val = null;
        while ( (val = itr.next()) != null ) {
            val.hashCode();
        }
        final long end = System.currentTimeMillis();
        System.out.println( "Custom ObjectHashMap iterate ET - " + ((end - start)) );
    }

    public void testJUHashMap3() {
        final int count = 100000;
        final java.util.HashMap map = new java.util.HashMap();
        assertNotNull( map );
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval );
        }
        final long start = System.currentTimeMillis();
        final java.util.Iterator itr = map.values().iterator();
        while ( itr.hasNext() ) {
            itr.next().hashCode();
        }
        final long end = System.currentTimeMillis();
        System.out.println( "java.util.HashMap iterate ET - " + ((end - start)) );
    }

    public void testStringData5() {
        final int count = 100000;
        final ObjectHashMap map = new ObjectHashMap();
        assertNotNull( map );
        final long start = System.currentTimeMillis();
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String strval = "value" + idx;
            map.put( key,
                     strval,
                     false );
        }
        final long end = System.currentTimeMillis();
        System.out.println( "Custom ObjectHashMap dup false ET - " + ((end - start)) );
    }

    public static void main(final String[] args) {
        final ObjectHashMapTest2 test = new ObjectHashMapTest2();
        final int loop = 5;
        for ( int idx = 0; idx < loop; idx++ ) {
            test.testIntegerData();
            test.testStringData();
            test.testJUHashmap();
            test.testStringData2();
            test.testJUHashMap1();
            test.testStringData3();
            test.testJUHashMap2();
            test.testStringData4();
            test.testJUHashMap3();
            test.testStringData5();
            test.testStringDataDupFalse();
            System.out.println( " --------------- " );
        }
    }
}