package org.drools.guvnor.server.util;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * 
 * @author Toni Rikkola
 * 
 */
public class ClassMethodInspectorTest extends TestCase {

    public void testSimpleMethods() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( SimpleMethods.class );

        assertEquals( 3,
                      ext.getMethodNames().size() );
    }

    public void testMoreThanOneMethodWithTheSameName() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( MoreThanOneMethodWithTheSameName.class );

        assertEquals( 5,
                      ext.getMethodNames().size() );

    }

    public void testCollection() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( Collection.class );

        assertEquals( 6,
                      ext.getMethodNames().size() );
    }

    public void testArrayList() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( ArrayList.class );

        assertEquals( 12,
                      ext.getMethodNames().size() );
    }

    public void testList() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( List.class );

        assertEquals( 11,
                      ext.getMethodNames().size() );
    }

    public void testSet() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( Set.class );

        assertEquals( 6,
                      ext.getMethodNames().size() );
    }

    public void testMap() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( Map.class );

        assertEquals( 4,
                      ext.getMethodNames().size() );
    }

    public void testMyMap() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( MyMap.class );

        assertEquals( 5,
                      ext.getMethodNames().size() );
    }

    public static class SimpleMethods {
        public void cleanOrSimilar() {

        }

        public void addOrSimilar(int i) {

        }

        public boolean methodThatReturnsIfItWasSuccesful() {
            return true;
        }
    }

    public static class MoreThanOneMethodWithTheSameName {

        public void justAMethod() {

        }

        public void justAMethod(int x) {

        }

        public void justAMethod(Object x) {

        }

        public void justAMethod(int x,
                                Object y) {

        }

        public Object justAMethod(int x,
                                  int y) {
            return null;
        }
    }

    public static class MyMap
        implements
        Map {

        public void magicMethod() {

        }

        public void clear() {
        }

        public boolean containsKey(Object arg0) {
            return false;
        }

        public boolean containsValue(Object arg0) {
            return false;
        }

        public Set entrySet() {
            return null;
        }

        public Object get(Object arg0) {
            return null;
        }

        public boolean isEmpty() {
            return false;
        }

        public Set keySet() {
            return null;
        }

        public Object put(Object arg0,
                          Object arg1) {
            return null;
        }

        public void putAll(Map arg0) {
        }

        public Object remove(Object arg0) {
            return null;
        }

        public int size() {
            return 0;
        }

        public Collection values() {
            return null;
        }

    }
}