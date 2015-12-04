/*
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

package org.drools.core.util;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ObjectHashMapTest {

    @Test
    public void testEqualityWithResize() {        
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EqualityBehaviorOption.EQUALITY );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        int length = 1 * 300 * 1000 ;
        
        List<FactHandle> handles = new ArrayList<FactHandle>(1000);
        List<String> objects = new ArrayList<String>(1000);
        for ( int i = 0; i < length; i++) { 
            String s = getPropertyName(i);
            FactHandle handle = ksession.insert( s );
            objects.add( s );
            handles.add( handle );
        }
        
        for ( int i = 0; i < length; i++) { 
            String s = objects.get(i);
            FactHandle handle = handles.get( i );
            assertEquals( s, ksession.getObject( handle ) );            
            assertSame( handle, ksession.getFactHandle( s ) );
            
            // now check with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            assertEquals( s, ksession.getObject( handle ) );
        }
        
        for ( int i = 0; i < length; i++) { 
            FactHandle handle = handles.get( i );         
            
            // now retract with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            ksession.retract( handle );
            assertEquals( length - i -1, ksession.getObjects().size() );
            assertEquals( length - i -1, ksession.getFactHandles().size() );            
        }        
        
        assertEquals( 0, ksession.getObjects().size() );
        assertEquals( 0, ksession.getFactHandles().size() );        
    }
    
    @Test
    public void testIdentityWithResize() {        
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EqualityBehaviorOption.IDENTITY );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        int length = 1 * 300 * 1000 ;
        
        List<FactHandle> handles = new ArrayList<FactHandle>(1000);
        List<String> objects = new ArrayList<String>(1000);
        for ( int i = 0; i < length; i++) { 
            String s = getPropertyName(i);
            FactHandle handle = ksession.insert( s );
            objects.add( s );
            handles.add( handle );
        }
        
        for ( int i = 0; i < length; i++) { 
            String s = objects.get(i);
            FactHandle handle = handles.get( i );
            assertEquals( s, ksession.getObject( handle ) );            
            assertSame( handle, ksession.getFactHandle( s ) );
            
            // now check with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            assertEquals( s, ksession.getObject( handle ) );            
        }
        
        for ( int i = 0; i < length; i++) { 
            FactHandle handle = handles.get( i );         
            
            // now retract with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            ksession.retract( handle );
            assertEquals( length - i -1, ksession.getObjects().size() );
            assertEquals( length - i -1, ksession.getFactHandles().size() );            
        }        
        
        assertEquals( 0, ksession.getObjects().size() );
        assertEquals( 0, ksession.getFactHandles().size() );         
    }    
    
    public String getPropertyName(int i) {
        char c1 = (char) (65+(i/3));
        char c2 = (char) (97+(i/3));        
        return c1 + "bl" + i + "" + c2 + "blah";
    }    

    @Test
    public void testEmptyIterator() {
        final ObjectHashMap map = new ObjectHashMap();
        final Iterator it = map.iterator();
        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            fail( "Map is empty, there should be no iteration" );
        }
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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
}
