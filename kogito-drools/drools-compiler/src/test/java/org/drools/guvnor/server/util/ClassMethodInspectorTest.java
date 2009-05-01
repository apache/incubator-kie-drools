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

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    public void testMoreThanOneMethodWithTheSameName() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( MoreThanOneMethodWithTheSameName.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }

    }

    public void testCollection() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( Collection.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    public void testArrayList() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( ArrayList.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    public void testList() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( List.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    public void testSet() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( Set.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    public void testMap() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( Map.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    public void testMyMap() throws Exception {
        final ClassMethodInspector ext = new ClassMethodInspector( MyMap.class );

        for ( String s : ext.getMethodNames() ) {
            assertFalse( "Method " + s + " is not allowed.",
                         allowedMethod( s ) );
        }
    }

    private boolean allowedMethod(String methodName) {
        return ("hashCode".equals( methodName ) || "equals".equals( methodName ) || "listIterator".equals( methodName ) || "lastIndexOf".equals( methodName ) || "indexOf".equals( methodName ) || "subList".equals( methodName )
                || "get".equals( methodName ) || "isEmpty".equals( methodName ) || "containsKey".equals( methodName ) || "values".equals( methodName ) || "entrySet".equals( methodName ) || "containsValue".equals( methodName )
                || "keySet".equals( methodName ) || "size".equals( methodName ) || "toArray".equals( methodName ) || "iterator".equals( methodName ) || "contains".equals( methodName ) || "isEmpty".equals( methodName )
                || "containsAll".equals( methodName ) || "size".equals( methodName ));
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