/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.asm;

import java.io.InputStream;

import org.mvel2.asm.AnnotationVisitor;
import org.mvel2.asm.Attribute;
import org.mvel2.asm.ClassReader;
import org.mvel2.asm.ClassVisitor;
import org.mvel2.asm.FieldVisitor;
import org.mvel2.asm.MethodVisitor;
import org.mvel2.asm.Opcodes;

import static org.drools.util.IoUtils.readBytesFromInputStream;

/**
 * The purpose of this utility it to check if 2 method implementations are equivalent, by comparing the bytecode.
 * This essentual for node sharing where java semantics are involved.
 */
public class MethodComparator {

    /**
     * This actually does the comparing.
     * Class1 and Class2 are class reader instances to the respective classes. method1 and method2 are looked up on the 
     * respective classes and their contents compared.
     * 
     * This is a convenience method.
     */
    public boolean equivalent(final String method1,
                              final ClassReader class1,
                              final String method2,
                              final ClassReader class2) {
        return getMethodBytecode( method1, class1 ).equals( getMethodBytecode( method2, class2 ) );
    }

    /**
     * This will return a series of bytecode instructions which can be used to compare one method with another.
     * debug info like local var declarations and line numbers are ignored, so the focus is on the content.
     */
    public String getMethodBytecode(final String methodName,
                                    final ClassReader classReader) {
        final Tracer visit = new Tracer( methodName );
        classReader.accept( visit, ClassReader.SKIP_DEBUG );
        return visit.getText();
    }

    /**
     * This will return a series of bytecode instructions which can be used to compare one method with another.
     * debug info like local var declarations and line numbers are ignored, so the focus is on the content.
     */
    public static String getMethodBytecode(final String methodName,
                                           final byte[] bytes) {
        final Tracer visit = new Tracer( methodName );
        final ClassReader classReader = new ClassReader( bytes );
        classReader.accept( visit,
                            ClassReader.SKIP_DEBUG );
        return visit.getText();
    }

    /**
     * Compares 2 bytecode listings.
     * Returns true if they are identical.
     */
    public static boolean compareBytecode(String b1, String b2) {
        return b1.equals( b2 );
    }

    public static class Tracer
        extends
        ClassVisitor {

        private String             methodName;
        private String             text;

        public Tracer(final String methodName) {
            super(Opcodes.ASM7);
            this.methodName = methodName;
        }

        public void visit(final int version,
                          final int access,
                          final String name,
                          final String signature,
                          final String superName,
                          final String[] interfaces) {
        }

        public AnnotationVisitor visitAnnotation(final String desc,
                                                 final boolean visible) {
            return new DummyAnnotationVisitor();
        }

        public void visitAttribute(final Attribute attr) {
        }

        public void visitEnd() {
        }

        public FieldVisitor visitField(final int access,
                                       final String name,
                                       final String desc,
                                       final String signature,
                                       final Object value) {
            return null;
        }

        public void visitInnerClass(final String name,
                                    final String outerName,
                                    final String innerName,
                                    final int access) {
        }

        public MethodVisitor visitMethod(final int access,
                                         final String name,
                                         final String desc,
                                         final String signature,
                                         final String[] exceptions) {

            return this.methodName.equals( name ) ? new DumpMethodVisitor(this::setText) : null;
        }

        public void visitOuterClass(final String owner,
                                    final String name,
                                    final String desc) {
        }

        public void visitSource(final String source,
                                final String debug) {
        }

        public String getText() {
            return text;
        }

        public void setText( String text ) {
            this.text = text;
        }
    }

    static class DummyAnnotationVisitor
        extends
        AnnotationVisitor {

        public DummyAnnotationVisitor() {
            super(Opcodes.ASM7);
        }

        public void visit(final String name,
                          final Object value) {
        }

        public AnnotationVisitor visitAnnotation(final String name,
                                                 final String desc) {
            return new DummyAnnotationVisitor();
        }

        public AnnotationVisitor visitArray(final String name) {
            return new DummyAnnotationVisitor();
        }

        public void visitEnd() {
        }

        public void visitEnum(final String name,
                              final String desc,
                              final String value) {
        }

    }

    public static String getMethodBytecode( Class cls, String ruleClassName, String packageName, String methodName, String resource ) {
        try (InputStream is = cls.getClassLoader().getResourceAsStream(resource)) {
            byte[] data = readBytesFromInputStream( is );
            MethodComparator.Tracer visit = new MethodComparator.Tracer(methodName);
            new org.mvel2.asm.ClassReader( data ).accept( visit, org.mvel2.asm.ClassReader.SKIP_DEBUG  );
            return visit.getText();
        } catch ( java.io.IOException e ) {
            throw new RuntimeException("Unable getResourceAsStream for Class '" + ruleClassName+ "' ");
        }
    }
}
