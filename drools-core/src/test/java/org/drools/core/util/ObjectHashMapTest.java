/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ObjectHashMapTest {

    @Test
    public void testEqualityWithResize() {        
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EqualityBehaviorOption.EQUALITY );
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        KieSession ksession = kbase.newKieSession();
        
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
            assertThat(ksession.getObject(handle)).isEqualTo(s);
            assertThat(ksession.getFactHandle(s)).isSameAs(handle);
            
            // now check with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            assertThat(ksession.getObject(handle)).isEqualTo(s);
        }
        
        for ( int i = 0; i < length; i++) { 
            FactHandle handle = handles.get( i );         
            
            // now retract with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            ksession.retract( handle );
            assertThat(ksession.getObjects().size()).isEqualTo(length - i - 1);
            assertThat(ksession.getFactHandles().size()).isEqualTo(length - i - 1);            
        }

        assertThat(ksession.getObjects().size()).isEqualTo(0);
        assertThat(ksession.getFactHandles().size()).isEqualTo(0);        
    }
    
    @Test
    public void testIdentityWithResize() {        
        KieBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kconf.setOption( EqualityBehaviorOption.IDENTITY );
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        KieSession ksession = kbase.newKieSession();
        
        int length = 1 * 300;
        
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
            assertThat(ksession.getObject(handle)).isEqualTo(s);
            assertThat(ksession.getFactHandle(s)).isSameAs(handle);
            
            // now check with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            assertThat(ksession.getObject(handle)).isEqualTo(s);
        }
        
        for ( int i = 0; i < length; i++) { 
            FactHandle handle = handles.get( i );         
            
            // now retract with disconnected facthandle
            handle = DefaultFactHandle.createFromExternalFormat(((DefaultFactHandle)handle).toExternalForm());
            ksession.retract( handle );
            assertThat(ksession.getObjects().size()).isEqualTo(length - i - 1);
            assertThat(ksession.getFactHandles().size()).isEqualTo(length - i - 1);            
        }

        assertThat(ksession.getObjects().size()).isEqualTo(0);
        assertThat(ksession.getFactHandles().size()).isEqualTo(0);         
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
        assertThat(map).isNotNull();
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val );
            assertThat(map.get(key)).isEqualTo(val);
        }
    }

    @Test
    public void testIntegerData() {
        final ObjectHashMap map = new ObjectHashMap();
        assertThat(map).isNotNull();
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final Integer key = new Integer( idx );
            final Integer val = new Integer( idx );
            map.put( key,
                     val );
            assertThat(map.get(key)).isEqualTo(val);
        }
    }

    @Test
    public void testJUHashmap() {
        final java.util.HashMap map = new java.util.HashMap();
        assertThat(map).isNotNull();
        final int count = 1000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val );
            assertThat(map.get(key)).isEqualTo(val);
        }
    }

    @Test
    public void testStringDataDupFalse() {
        final ObjectHashMap map = new ObjectHashMap();
        assertThat(map).isNotNull();
        final int count = 10000;
        for ( int idx = 0; idx < count; idx++ ) {
            final String key = "key" + idx;
            final String val = "value" + idx;
            map.put( key,
                     val,
                     false );
            assertThat(map.get(key)).isEqualTo(val);
        }
    }

    @Test
    public void testJUHashMap1() {
        final int count = 100000;
        final java.util.HashMap map = new java.util.HashMap();
        assertThat(map).isNotNull();
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
        assertThat(map).isNotNull();
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
        assertThat(map).isNotNull();
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
        assertThat(map).isNotNull();
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
        assertThat(map).isNotNull();
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
        assertThat(map).isNotNull();
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
        assertThat(map).isNotNull();
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
