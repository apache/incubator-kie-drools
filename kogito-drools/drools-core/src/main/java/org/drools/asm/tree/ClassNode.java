/***
 * ASM: a very small and fast Java bytecode manipulation framework
 * Copyright (c) 2000-2005 INRIA, France Telecom
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.drools.asm.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.asm.Attribute;
import org.drools.asm.ClassVisitor;
import org.drools.asm.FieldVisitor;
import org.drools.asm.MethodVisitor;

/**
 * A node that represents a class.
 * 
 * @author Eric Bruneton
 */
public class ClassNode extends MemberNode
    implements
    ClassVisitor {

    /**
     * The class version.
     */
    public int    version;

    /**
     * The class's access flags (see {@link org.drools.asm.Opcodes}). This
     * field also indicates if the class is deprecated.
     */
    public int    access;

    /**
     * The internal name of the class (see
     * {@link org.drools.asm.Type#getInternalName() getInternalName}).
     */
    public String name;

    /**
     * The signature of the class. Mayt be <tt>null</tt>.
     */
    public String signature;

    /**
     * The internal of name of the super class (see
     * {@link org.drools.asm.Type#getInternalName() getInternalName}). For
     * interfaces, the super class is {@link Object}. May be <tt>null</tt>,
     * but only for the {@link Object} class.
     */
    public String superName;

    /**
     * The internal names of the class's interfaces (see
     * {@link org.drools.asm.Type#getInternalName() getInternalName}). This
     * list is a list of {@link String} objects.
     */
    public List   interfaces;

    /**
     * The name of the source file from which this class was compiled. May be
     * <tt>null</tt>.
     */
    public String sourceFile;

    /**
     * Debug information to compute the correspondance between source and
     * compiled elements of the class. May be <tt>null</tt>.
     */
    public String sourceDebug;

    /**
     * The internal name of the enclosing class of the class. May be
     * <tt>null</tt>.
     */
    public String outerClass;

    /**
     * The name of the method that contains the class, or <tt>null</tt> if the
     * class is not enclosed in a method.
     */
    public String outerMethod;

    /**
     * The descriptor of the method that contains the class, or <tt>null</tt>
     * if the class is not enclosed in a method.
     */
    public String outerMethodDesc;

    /**
     * Informations about the inner classes of this class. This list is a list
     * of {@link InnerClassNode} objects.
     * 
     * @associates org.objectweb.asm.tree.InnerClassNode
     */
    public List   innerClasses;

    /**
     * The fields of this class. This list is a list of {@link FieldNode}
     * objects.
     * 
     * @associates org.objectweb.asm.tree.FieldNode
     */
    public List   fields;

    /**
     * The methods of this class. This list is a list of {@link MethodNode}
     * objects.
     * 
     * @associates org.objectweb.asm.tree.MethodNode
     */
    public List   methods;

    /**
     * Constructs a new {@link ClassNode}.
     */
    public ClassNode() {
        this.interfaces = new ArrayList();
        this.innerClasses = new ArrayList();
        this.fields = new ArrayList();
        this.methods = new ArrayList();
    }

    // ------------------------------------------------------------------------
    // Implementation of the ClassVisitor interface
    // ------------------------------------------------------------------------

    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        if ( interfaces != null ) {
            this.interfaces.addAll( Arrays.asList( interfaces ) );
        }
    }

    public void visitSource(final String file,
                            final String debug) {
        this.sourceFile = file;
        this.sourceDebug = debug;
    }

    public void visitOuterClass(final String owner,
                                final String name,
                                final String desc) {
        this.outerClass = owner;
        this.outerMethod = name;
        this.outerMethodDesc = desc;
    }

    public void visitInnerClass(final String name,
                                final String outerName,
                                final String innerName,
                                final int access) {
        final InnerClassNode icn = new InnerClassNode( name,
                                                       outerName,
                                                       innerName,
                                                       access );
        this.innerClasses.add( icn );
    }

    public FieldVisitor visitField(final int access,
                                   final String name,
                                   final String desc,
                                   final String signature,
                                   final Object value) {
        final FieldNode fn = new FieldNode( access,
                                            name,
                                            desc,
                                            signature,
                                            value );
        this.fields.add( fn );
        return fn;
    }

    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        final MethodNode mn = new MethodNode( access,
                                              name,
                                              desc,
                                              signature,
                                              exceptions );
        this.methods.add( mn );
        return mn;
    }

    public void visitEnd() {
    }

    // ------------------------------------------------------------------------
    // Accept method
    // ------------------------------------------------------------------------

    /**
     * Makes the given class visitor visit this class.
     * 
     * @param cv a class visitor.
     */
    public void accept(final ClassVisitor cv) {
        // visits header
        final String[] interfaces = new String[this.interfaces.size()];
        this.interfaces.toArray( interfaces );
        cv.visit( this.version,
                  this.access,
                  this.name,
                  this.signature,
                  this.superName,
                  interfaces );
        // visits source
        if ( this.sourceFile != null || this.sourceDebug != null ) {
            cv.visitSource( this.sourceFile,
                            this.sourceDebug );
        }
        // visits outer class
        if ( this.outerClass != null ) {
            cv.visitOuterClass( this.outerClass,
                                this.outerMethod,
                                this.outerMethodDesc );
        }
        // visits attributes
        int i, n;
        n = this.visibleAnnotations == null ? 0 : this.visibleAnnotations.size();
        for ( i = 0; i < n; ++i ) {
            final AnnotationNode an = (AnnotationNode) this.visibleAnnotations.get( i );
            an.accept( cv.visitAnnotation( an.desc,
                                           true ) );
        }
        n = this.invisibleAnnotations == null ? 0 : this.invisibleAnnotations.size();
        for ( i = 0; i < n; ++i ) {
            final AnnotationNode an = (AnnotationNode) this.invisibleAnnotations.get( i );
            an.accept( cv.visitAnnotation( an.desc,
                                           false ) );
        }
        n = this.attrs == null ? 0 : this.attrs.size();
        for ( i = 0; i < n; ++i ) {
            cv.visitAttribute( (Attribute) this.attrs.get( i ) );
        }
        // visits inner classes
        for ( i = 0; i < this.innerClasses.size(); ++i ) {
            ((InnerClassNode) this.innerClasses.get( i )).accept( cv );
        }
        // visits fields
        for ( i = 0; i < this.fields.size(); ++i ) {
            ((FieldNode) this.fields.get( i )).accept( cv );
        }
        // visits methods
        for ( i = 0; i < this.methods.size(); ++i ) {
            ((MethodNode) this.methods.get( i )).accept( cv );
        }
        // visits end
        cv.visitEnd();
    }
}
