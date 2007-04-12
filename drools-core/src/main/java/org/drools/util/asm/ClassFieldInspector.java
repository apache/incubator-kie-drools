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

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    private final List methods     = new ArrayList();
    private final Map  fieldNames  = new HashMap();
    private final Map  fieldTypes  = new HashMap();
    private final Map  methodNames = new HashMap();
    private final Set  nonGetters  = new HashSet();

    /**
     * @param clazz The class that the fields to be shadowed are extracted for.
     * @throws IOException
     */
    public ClassFieldInspector(final Class clazz) throws IOException {
        this( clazz,
              true );
    }

    public ClassFieldInspector(final Class clazz,
                               final boolean includeFinalMethods) throws IOException {
        final String name = getResourcePath( clazz );
        final InputStream stream = clazz.getResourceAsStream( name );

        if ( stream != null ) {
            processClassWithByteCode( clazz,
                                      stream,
                                      includeFinalMethods );
        } else {
            processClassWithoutByteCode( clazz,
                                         includeFinalMethods );
        }
    }

    /** Walk up the inheritance hierarchy recursively, reading in fields */
    private void processClassWithByteCode(final Class clazz,
                                          final InputStream stream,
                                          final boolean includeFinalMethods) throws IOException {

        final ClassReader reader = new ClassReader( stream );
        final ClassFieldVisitor visitor = new ClassFieldVisitor( clazz,
                                                                 includeFinalMethods,
                                                                 this );
        reader.accept( visitor,
                       false );
        if ( clazz.getSuperclass() != null ) {
            final String name = getResourcePath( clazz.getSuperclass() );
            final InputStream parentStream = clazz.getResourceAsStream( name );
            if ( parentStream != null ) {
                processClassWithByteCode( clazz.getSuperclass(),
                                          parentStream,
                                          includeFinalMethods );
            } else {
                processClassWithoutByteCode( clazz.getSuperclass(),
                                             includeFinalMethods );
            }
        }
        if ( clazz.isInterface() ) {
            final Class[] interfaces = clazz.getInterfaces();
            for ( int i = 0; i < interfaces.length; i++ ) {
                final String name = getResourcePath( interfaces[i] );
                final InputStream parentStream = clazz.getResourceAsStream( name );
                if ( parentStream != null ) {
                    processClassWithByteCode( interfaces[i],
                                              parentStream,
                                              includeFinalMethods );
                } else {
                    processClassWithoutByteCode( interfaces[i],
                                                 includeFinalMethods );
                }
            }
        }
    }

    private void processClassWithoutByteCode(final Class clazz,
                                             final boolean includeFinalMethods) {
        final Method[] methods = clazz.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {

            //only want public methods that start with 'get' or 'is'
            //and have no args, and return a value
            final int mask = includeFinalMethods ? Modifier.PUBLIC : Modifier.PUBLIC | Modifier.FINAL;
            if ( ((methods[i].getModifiers() & mask) == Modifier.PUBLIC) && (methods[i].getParameterTypes().length == 0) && (!methods[i].getName().equals( "<init>" )) &&
            //( !methods[i].getName().equals( "<clinit>" )) && 
                 (methods[i].getReturnType() != void.class) ) {
                final int fieldIndex = this.methods.size();
                addToMapping( methods[i],
                              fieldIndex );
            }
        }
    }

    /**
     * Convert it to a form so we can load the bytes from the classpath.
     */
    private String getResourcePath(final Class clazz) {
        return "/" + clazz.getName().replace( '.',
                                              '/' ) + ".class";
    }

    /** 
     * Return a list in order of which the getters (and "is") methods were found.
     * This should only be done once when compiling a rulebase ideally.
     */
    public List getPropertyGetters() {
        return this.methods;
    }

    /**
     * Return a mapping of the field "names" (ie bean property name convention)
     * to the numerical index by which they can be accessed.
     */
    public Map getFieldNames() {
        return this.fieldNames;
    }

    /**
     * @return A mapping of field types (unboxed).
     */
    public Map getFieldTypes() {
        return this.fieldTypes;
    }

    /** 
     * @return A mapping of methods for the getters. 
     */
    public Map getGetterMethods() {
        return this.methodNames;
    }

    private void addToMapping(final Method method,
                              final int index) {
        final String name = method.getName();
        int offset;
        if ( name.startsWith( "is" ) ) {
            offset = 2;
        } else if ( name.startsWith( "get" ) ) {
            offset = 3;
        } else {
            offset = 0;
        }
        final String fieldName = calcFieldName( name,
                                                offset );
        if ( this.fieldNames.containsKey( fieldName ) ) {
            //only want it once, the first one thats found
            if ( offset != 0 && this.nonGetters.contains( fieldName ) ) {
                //replace the non getter method with the getter one
                removeOldField( fieldName );
                storeField( method,
                            index,
                            fieldName );
                this.nonGetters.remove( fieldName );
            }
        } else {
            storeField( method,
                        index,
                        fieldName );
            if ( offset == 0 ) {
                this.nonGetters.add( fieldName );
            }
        }
    }

    private void removeOldField(final String fieldName) {
        this.fieldNames.remove( fieldName );
        this.fieldTypes.remove( fieldName );
        this.methods.remove( this.methodNames.get( fieldName ) );
        this.methodNames.remove( fieldName );

    }

    private void storeField(final Method method,
                            final int index,
                            final String fieldName) {
        this.fieldNames.put( fieldName,
                             new Integer( index ) );
        this.fieldTypes.put( fieldName,
                             method.getReturnType() );
        this.methodNames.put( fieldName,
                              method );
        this.methods.add( method );
    }

    private String calcFieldName(String name,
                                 final int offset) {
        name = name.substring( offset );
        return Introspector.decapitalize( name );
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
        private boolean             includeFinalMethods;

        ClassFieldVisitor(final Class cls,
                          final boolean includeFinalMethods,
                          final ClassFieldInspector inspector) {
            this.clazz = cls;
            this.includeFinalMethods = includeFinalMethods;
            this.inspector = inspector;
        }

        public MethodVisitor visitMethod(final int access,
                                         final String name,
                                         final String desc,
                                         final String signature,
                                         final String[] exceptions) {
            //only want public methods
            //and have no args, and return a value
            final int mask = this.includeFinalMethods ? Opcodes.ACC_PUBLIC : Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL;
            if ( (access & mask) == Opcodes.ACC_PUBLIC ) {
                if ( desc.startsWith( "()" ) && (!name.equals( "<init>" )) /*&&
                 ( ! name.equals( "<clinit>" ) ) */) {// && ( name.startsWith("get") || name.startsWith("is") ) ) {
                    try {
                        final Method method = this.clazz.getMethod( name,
                                                                    (Class[]) null );
                        if ( method.getReturnType() != void.class ) {
                            final int fieldIndex = this.inspector.methods.size();
                            this.inspector.addToMapping( method,
                                                         fieldIndex );
                        }
                    } catch ( final NoSuchMethodException e ) {
                        throw new IllegalStateException( "Error in getting field access method." );
                    }
                }
            }
            return null;
        }

        public void visit(final int arg0,
                          final int arg1,
                          final String arg2,
                          final String arg3,
                          final String[] arg4,
                          final String arg5) {
        }

        public void visitInnerClass(final String arg0,
                                    final String arg1,
                                    final String arg2,
                                    final int arg3) {
        }

        public void visitField(final int access,
                               final String arg1,
                               final String arg2,
                               final Object arg3,
                               final Attribute arg4) {
        }

        public void visitAttribute(final Attribute arg0) {
        }

        public void visitEnd() {
        }

        public void visit(final int arg0,
                          final int arg1,
                          final String arg2,
                          final String arg3,
                          final String arg4,
                          final String[] arg5) {

        }

        public void visitSource(final String arg0,
                                final String arg1) {

        }

        public void visitOuterClass(final String arg0,
                                    final String arg1,
                                    final String arg2) {

        }

        public AnnotationVisitor visitAnnotation(final String arg0,
                                                 final boolean arg1) {

            return new ClassFieldAnnotationVisitor();
        }

        public FieldVisitor visitField(final int arg0,
                                       final String arg1,
                                       final String arg2,
                                       final String arg3,
                                       final Object arg4) {

            return null;
        }

    }

    /** 
     * This is required for POJOs that have annotations. 
     * It may also come in handy if we want to allow custom annotations for marking field numbers etc.
     */
    static class ClassFieldAnnotationVisitor
        implements
        AnnotationVisitor {

        public void visit(final String arg0,
                          final Object arg1) {
        }

        public void visitEnum(final String arg0,
                              final String arg1,
                              final String arg2) {
        }

        public AnnotationVisitor visitAnnotation(final String arg0,
                                                 final String arg1) {
            return new ClassFieldAnnotationVisitor();
        }

        public AnnotationVisitor visitArray(final String arg0) {
            return new ClassFieldAnnotationVisitor();
        }

        public void visitEnd() {

        }

    }

}