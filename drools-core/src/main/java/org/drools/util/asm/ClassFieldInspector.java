package org.drools.util.asm;
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



import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.Attribute;
import org.drools.asm.ClassReader;
import org.drools.asm.ClassVisitor;
import org.drools.asm.FieldVisitor;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;

/**
 * Visit a POJO user class, and extract the property getter methods that are public, in the 
 * order in which they are declared actually in the class itself (not using introspection).
 * 
 * This may be enhanced in the future to allow annotations or perhaps external meta data
 * configure the order of the indexes, as this may provide fine tuning options in special cases.
 * 
 * @author Michael Neale
 */
public class ClassFieldInspector {

    private List methods     = new ArrayList();
    private Map  fieldNames  = new HashMap();
    private Map  fieldTypes  = new HashMap();
    private Map  methodNames = new HashMap();
    private Set  nonGetters  = new HashSet();

    /**
     * @param clazz The class that the fields to be shadowed are extracted for.
     * @throws IOException
     */
    public ClassFieldInspector(Class clazz) throws IOException {
        processClass( clazz );
    }

    /** Walk up the inheritance hierarchy recursively, reading in fields */
    private void processClass(Class clazz) throws IOException {
        String name = getResourcePath( clazz );
        InputStream stream = clazz.getResourceAsStream( name );
        ClassReader reader = new ClassReader( stream );
        ClassFieldVisitor visitor = new ClassFieldVisitor( clazz,
                                                           this );
        reader.accept( visitor,
                       false );
        if ( clazz.getSuperclass() != null ) {
            processClass( clazz.getSuperclass() );
        }
        if ( clazz.isInterface() ) {
            Class[] interfaces = clazz.getInterfaces();
            for ( int i = 0; i < interfaces.length; i++ ) {
                processClass( interfaces[i] );
            }
        }
    }

    /**
     * Convert it to a form so we can load the bytes from the classpath.
     */
    private String getResourcePath(Class clazz) {
        return "/" + clazz.getName().replace( '.',
                                                 '/' ) + ".class";
    }

    /** 
     * Return a list in order of which the getters (and "is") methods were found.
     * This should only be done once when compiling a rulebase ideally.
     */
    public List getPropertyGetters() {
        return methods;
    }

    /**
     * Return a mapping of the field "names" (ie bean property name convention)
     * to the numerical index by which they can be accessed.
     */
    public Map getFieldNames() {
        return fieldNames;
    }

    /**
     * @return A mapping of field types (unboxed).
     */
    public Map getFieldTypes() {
        return fieldTypes;
    }

    /** 
     * @return A mapping of methods for the getters. 
     */
    public Map getGetterMethods() {
        return methodNames;
    }

    /**
     * Using the ASM classfield extractor to pluck it out in the order they appear in the class file.
     * @author Michael Neale
     */
    static class ClassFieldVisitor
        implements
        ClassVisitor {

        private Class               clazz;
        private ClassFieldInspector inspector;

        ClassFieldVisitor(Class cls,
                          ClassFieldInspector inspector) {
            this.clazz = cls;
            this.inspector = inspector;
        }

        public MethodVisitor visitMethod(int access,
                                         String name,
                                         String desc,
                                         String signature,
                                         String[] exceptions) {
            //only want public methods that start with 'get' or 'is'
            //and have no args, and return a value
            if ( (access & Opcodes.ACC_PUBLIC) > 0 ) {
                if ( desc.startsWith( "()" ) && !(name.equals( "<init>" )) ) {// && ( name.startsWith("get") || name.startsWith("is") ) ) {
                    try {
                        Method method = clazz.getMethod( name,
                                                         (Class[]) null );
                        if ( method.getReturnType() != void.class ) {
                            int fieldIndex = inspector.methods.size();
                            addToMapping( method,
                                          fieldIndex );
                        }
                    } catch ( NoSuchMethodException e ) {
                        throw new IllegalStateException( "Error in getting field access method." );
                    }
                }
            }
            return null;
        }

        public void visit(int arg0,
                          int arg1,
                          String arg2,
                          String arg3,
                          String[] arg4,
                          String arg5) {
        }

        public void visitInnerClass(String arg0,
                                    String arg1,
                                    String arg2,
                                    int arg3) {
        }

        public void visitField(int access,
                               String arg1,
                               String arg2,
                               Object arg3,
                               Attribute arg4) {
        }

        public void visitAttribute(Attribute arg0) {
        }

        public void visitEnd() {
        }

        public void visit(int arg0,
                          int arg1,
                          String arg2,
                          String arg3,
                          String arg4,
                          String[] arg5) {

        }

        public void visitSource(String arg0,
                                String arg1) {

        }

        public void visitOuterClass(String arg0,
                                    String arg1,
                                    String arg2) {

        }

        public AnnotationVisitor visitAnnotation(String arg0,
                                                 boolean arg1) {

            return new ClassFieldAnnotationVisitor();
        }

        public FieldVisitor visitField(int arg0,
                                       String arg1,
                                       String arg2,
                                       String arg3,
                                       Object arg4) {

            return null;
        }

        private void addToMapping(Method method,
                                  int index) {
            String name = method.getName();
            int offset;
            if ( name.startsWith( "is" ) ) {
                offset = 2;
            } else if ( name.startsWith( "get" ) ) {
                offset = 3;
            } else {
                offset = 0;
            }
            String fieldName = calcFieldName( name,
                                              offset );
            if ( inspector.fieldNames.containsKey( fieldName ) ) {
                //only want it once, the first one thats found
                if ( offset != 0 && inspector.nonGetters.contains( fieldName ) ) {
                    //replace the non getter method with the getter one
                    removeOldField( fieldName );
                    storeField( method,
                                index,
                                fieldName );
                    inspector.nonGetters.remove( fieldName );
                }
            } else {
                storeField( method,
                            index,
                            fieldName );
                if ( offset == 0 ) {
                    inspector.nonGetters.add( fieldName );
                }
            }
        }

        private void removeOldField(String fieldName) {
            inspector.fieldNames.remove( fieldName );
            inspector.fieldTypes.remove( fieldName );
            inspector.methods.remove( inspector.methodNames.get( fieldName ) );
            inspector.methodNames.remove( fieldName );

        }

        private void storeField(Method method,
                                int index,
                                String fieldName) {
            inspector.fieldNames.put( fieldName,
                                      new Integer( index ) );
            inspector.fieldTypes.put( fieldName,
                                      method.getReturnType() );
            inspector.methodNames.put( fieldName,
                                       method );
            inspector.methods.add( method );
        }

        private String calcFieldName(String name,
                                     int offset) {
            name = name.substring( offset );
            char first = Character.toLowerCase( name.charAt( 0 ) );
            name = first + name.substring( 1 );
            return name;
        }

    }

    /** 
     * This is required for POJOs that have annotations. 
     * It may also come in handy if we want to allow custom annotations for marking field numbers etc.
     */
    static class ClassFieldAnnotationVisitor
        implements
        AnnotationVisitor {

        public void visit(String arg0,
                          Object arg1) {
        }

        public void visitEnum(String arg0,
                              String arg1,
                              String arg2) {
        }

        public AnnotationVisitor visitAnnotation(String arg0,
                                                 String arg1) {
            return new ClassFieldAnnotationVisitor();
        }

        public AnnotationVisitor visitArray(String arg0) {
            return new ClassFieldAnnotationVisitor();
        }

        public void visitEnd() {

        }

    }
}