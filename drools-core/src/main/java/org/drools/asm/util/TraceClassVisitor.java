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
package org.drools.asm.util;

import java.io.FileInputStream;
import java.io.PrintWriter;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.Attribute;
import org.drools.asm.ClassReader;
import org.drools.asm.ClassVisitor;
import org.drools.asm.FieldVisitor;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.signature.SignatureReader;

/**
 * A {@link ClassVisitor} that prints a disassembled view of the classes it
 * visits. This class visitor can be used alone (see the {@link #main main}
 * method) to disassemble a class. It can also be used in the middle of class
 * visitor chain to trace the class that is visited at a given point in this
 * chain. This may be uselful for debugging purposes. <p> The trace printed when
 * visiting the <tt>Hello</tt> class is the following: <p> <blockquote>
 * 
 * <pre>
 * // class version 49.0 (49)
 * // access flags 33
 * public class Hello {
 *
 *  // compiled from: Hello.java
 *
 *   // access flags 1
 *   public &lt;init&gt; ()V
 *     ALOAD 0
 *     INVOKESPECIAL java/lang/Object &lt;init&gt; ()V
 *     RETURN
 *     MAXSTACK = 1
 *     MAXLOCALS = 1
 *
 *   // access flags 9
 *   public static main ([Ljava/lang/String;)V
 *     GETSTATIC java/lang/System out Ljava/io/PrintStream;
 *     LDC &quot;hello&quot;
 *     INVOKEVIRTUAL java/io/PrintStream println (Ljava/lang/String;)V
 *     RETURN
 *     MAXSTACK = 2
 *     MAXLOCALS = 1
 * }
 * </pre>
 * 
 * </blockquote> where <tt>Hello</tt> is defined by: <p> <blockquote>
 * 
 * <pre>
 * public class Hello {
 *
 *     public static void main(String[] args) {
 *         System.out.println(&quot;hello&quot;);
 *     }
 * }
 * </pre>
 * 
 * </blockquote>
 * 
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class TraceClassVisitor extends TraceAbstractVisitor
    implements
    ClassVisitor {

    /**
     * The {@link ClassVisitor} to which this visitor delegates calls. May be
     * <tt>null</tt>.
     */
    protected final ClassVisitor cv;

    /**
     * The print writer to be used to print the class.
     */
    protected final PrintWriter  pw;

    /**
     * Prints a disassembled view of the given class to the standard output. <p>
     * Usage: TraceClassVisitor [-debug] &lt;fully qualified class name or class
     * file name &gt;
     * 
     * @param args the command line arguments.
     * 
     * @throws Exception if the class cannot be found, or if an IO exception
     *         occurs.
     */
    public static void main(final String[] args) throws Exception {
        int i = 0;
        boolean skipDebug = true;

        boolean ok = true;
        if ( args.length < 1 || args.length > 2 ) {
            ok = false;
        }
        if ( ok && args[0].equals( "-debug" ) ) {
            i = 1;
            skipDebug = false;
            if ( args.length != 2 ) {
                ok = false;
            }
        }
        if ( !ok ) {
            System.err.println( "Prints a disassembled view of the given class." );
            System.err.println( "Usage: TraceClassVisitor [-debug] " + "<fully qualified class name or class file name>" );
            return;
        }
        ClassReader cr;
        if ( args[i].endsWith( ".class" ) || args[i].indexOf( '\\' ) > -1 || args[i].indexOf( '/' ) > -1 ) {
            cr = new ClassReader( new FileInputStream( args[i] ) );
        } else {
            cr = new ClassReader( args[i] );
        }
        cr.accept( new TraceClassVisitor( new PrintWriter( System.out ) ),
                   getDefaultAttributes(),
                   skipDebug );
    }

    /**
     * Constructs a new {@link TraceClassVisitor}.
     * 
     * @param pw the print writer to be used to print the class.
     */
    public TraceClassVisitor(final PrintWriter pw) {
        this( null,
              pw );
    }

    /**
     * Constructs a new {@link TraceClassVisitor}.
     * 
     * @param cv the {@link ClassVisitor} to which this visitor delegates calls.
     *        May be <tt>null</tt>.
     * @param pw the print writer to be used to print the class.
     */
    public TraceClassVisitor(final ClassVisitor cv,
                             final PrintWriter pw) {
        this.cv = cv;
        this.pw = pw;
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
        final int major = version & 0xFFFF;
        final int minor = version >>> 16;
        this.buf.setLength( 0 );
        this.buf.append( "// class version " ).append( major ).append( '.' ).append( minor ).append( " (" ).append( version ).append( ")\n" );
        if ( (access & Opcodes.ACC_DEPRECATED) != 0 ) {
            this.buf.append( "// DEPRECATED\n" );
        }
        this.buf.append( "// access flags " ).append( access ).append( '\n' );

        appendDescriptor( TraceAbstractVisitor.CLASS_SIGNATURE,
                          signature );
        if ( signature != null ) {
            final TraceSignatureVisitor sv = new TraceSignatureVisitor( access );
            final SignatureReader r = new SignatureReader( signature );
            r.accept( sv );
            this.buf.append( "// declaration: " ).append( name ).append( sv.getDeclaration() ).append( '\n' );
        }

        appendAccess( access & ~Opcodes.ACC_SUPER );
        if ( (access & Opcodes.ACC_ANNOTATION) != 0 ) {
            this.buf.append( "@interface " );
        } else if ( (access & Opcodes.ACC_INTERFACE) != 0 ) {
            this.buf.append( "interface " );
        } else if ( (access & Opcodes.ACC_ENUM) != 0 ) {
            this.buf.append( "enum " );
        } else {
            this.buf.append( "class " );
        }
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          name );

        if ( superName != null && !superName.equals( "java/lang/Object" ) ) {
            this.buf.append( " extends " );
            appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                              superName );
            this.buf.append( ' ' );
        }
        if ( interfaces != null && interfaces.length > 0 ) {
            this.buf.append( " implements " );
            for ( int i = 0; i < interfaces.length; ++i ) {
                appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                                  interfaces[i] );
                this.buf.append( ' ' );
            }
        }
        this.buf.append( " {\n\n" );

        this.text.add( this.buf.toString() );

        if ( this.cv != null ) {
            this.cv.visit( version,
                           access,
                           name,
                           signature,
                           superName,
                           interfaces );
        }
    }

    public void visitSource(final String file,
                            final String debug) {
        this.buf.setLength( 0 );
        if ( file != null ) {
            this.buf.append( this.tab ).append( "// compiled from: " ).append( file ).append( '\n' );
        }
        if ( debug != null ) {
            this.buf.append( this.tab ).append( "// debug info: " ).append( debug ).append( '\n' );
        }
        if ( this.buf.length() > 0 ) {
            this.text.add( this.buf.toString() );
        }

        if ( this.cv != null ) {
            this.cv.visitSource( file,
                                 debug );
        }
    }

    public void visitOuterClass(final String owner,
                                final String name,
                                final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab ).append( "OUTERCLASS " );
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          owner );
        // if enclosing name is null, so why should we show this info?
        if ( name != null ) {
            this.buf.append( ' ' ).append( name ).append( ' ' );
        } else {
            this.buf.append( ' ' );
        }
        appendDescriptor( TraceAbstractVisitor.METHOD_DESCRIPTOR,
                          desc );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.cv != null ) {
            this.cv.visitOuterClass( owner,
                                     name,
                                     desc );
        }
    }

    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        this.text.add( "\n" );
        final AnnotationVisitor tav = super.visitAnnotation( desc,
                                                             visible );
        if ( this.cv != null ) {
            ((TraceAnnotationVisitor) tav).av = this.cv.visitAnnotation( desc,
                                                                         visible );
        }
        return tav;
    }

    public void visitAttribute(final Attribute attr) {
        this.text.add( "\n" );
        super.visitAttribute( attr );

        if ( this.cv != null ) {
            this.cv.visitAttribute( attr );
        }
    }

    public void visitInnerClass(final String name,
                                final String outerName,
                                final String innerName,
                                final int access) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab ).append( "// access flags " ).append( access & ~Opcodes.ACC_SUPER ).append( '\n' );
        this.buf.append( this.tab );
        appendAccess( access );
        this.buf.append( "INNERCLASS " );
        if ( (access & Opcodes.ACC_ENUM) != 0 ) {
            this.buf.append( "enum " );
        }
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          name );
        this.buf.append( ' ' );
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          outerName );
        this.buf.append( ' ' );
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          innerName );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.cv != null ) {
            this.cv.visitInnerClass( name,
                                     outerName,
                                     innerName,
                                     access );
        }
    }

    public FieldVisitor visitField(final int access,
                                   final String name,
                                   final String desc,
                                   final String signature,
                                   final Object value) {
        this.buf.setLength( 0 );
        this.buf.append( '\n' );
        if ( (access & Opcodes.ACC_DEPRECATED) != 0 ) {
            this.buf.append( this.tab ).append( "// DEPRECATED\n" );
        }
        this.buf.append( this.tab ).append( "// access flags " ).append( access ).append( '\n' );
        if ( signature != null ) {
            this.buf.append( this.tab );
            appendDescriptor( TraceAbstractVisitor.FIELD_SIGNATURE,
                              signature );

            final TraceSignatureVisitor sv = new TraceSignatureVisitor( 0 );
            final SignatureReader r = new SignatureReader( signature );
            r.acceptType( sv );
            this.buf.append( this.tab ).append( "// declaration: " ).append( sv.getDeclaration() ).append( '\n' );
        }

        this.buf.append( this.tab );
        appendAccess( access );
        if ( (access & Opcodes.ACC_ENUM) != 0 ) {
            this.buf.append( "enum " );
        }

        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( ' ' ).append( name );
        if ( value != null ) {
            this.buf.append( " = " );
            if ( value instanceof String ) {
                this.buf.append( "\"" ).append( value ).append( "\"" );
            } else {
                this.buf.append( value );
            }
        }

        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        final TraceFieldVisitor tav = createTraceFieldVisitor();
        this.text.add( tav.getText() );

        if ( this.cv != null ) {
            tav.fv = this.cv.visitField( access,
                                         name,
                                         desc,
                                         signature,
                                         value );
        }

        return tav;
    }

    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        this.buf.setLength( 0 );
        this.buf.append( '\n' );
        if ( (access & Opcodes.ACC_DEPRECATED) != 0 ) {
            this.buf.append( this.tab ).append( "// DEPRECATED\n" );
        }
        this.buf.append( this.tab ).append( "// access flags " ).append( access ).append( '\n' );
        this.buf.append( this.tab );
        appendDescriptor( TraceAbstractVisitor.METHOD_SIGNATURE,
                          signature );

        if ( signature != null ) {
            final TraceSignatureVisitor v = new TraceSignatureVisitor( 0 );
            final SignatureReader r = new SignatureReader( signature );
            r.accept( v );
            final String genericDecl = v.getDeclaration();
            final String genericReturn = v.getReturnType();
            final String genericExceptions = v.getExceptions();

            this.buf.append( this.tab ).append( "// declaration: " ).append( genericReturn ).append( ' ' ).append( name ).append( genericDecl );
            if ( genericExceptions != null ) {
                this.buf.append( " throws " ).append( genericExceptions );
            }
            this.buf.append( '\n' );
        }

        appendAccess( access );
        if ( (access & Opcodes.ACC_NATIVE) != 0 ) {
            this.buf.append( "native " );
        }
        if ( (access & Opcodes.ACC_VARARGS) != 0 ) {
            this.buf.append( "varargs " );
        }
        if ( (access & Opcodes.ACC_BRIDGE) != 0 ) {
            this.buf.append( "bridge " );
        }

        this.buf.append( name );
        appendDescriptor( TraceAbstractVisitor.METHOD_DESCRIPTOR,
                          desc );
        if ( exceptions != null && exceptions.length > 0 ) {
            this.buf.append( " throws " );
            for ( int i = 0; i < exceptions.length; ++i ) {
                appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                                  exceptions[i] );
                this.buf.append( ' ' );
            }
        }

        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        final TraceMethodVisitor tcv = createTraceMethodVisitor();
        this.text.add( tcv.getText() );

        if ( this.cv != null ) {
            tcv.mv = this.cv.visitMethod( access,
                                          name,
                                          desc,
                                          signature,
                                          exceptions );
        }

        return tcv;
    }

    public void visitEnd() {
        this.text.add( "}\n" );

        printList( this.pw,
                   this.text );
        this.pw.flush();

        if ( this.cv != null ) {
            this.cv.visitEnd();
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    protected TraceFieldVisitor createTraceFieldVisitor() {
        return new TraceFieldVisitor();
    }

    protected TraceMethodVisitor createTraceMethodVisitor() {
        return new TraceMethodVisitor();
    }

    /**
     * Appends a string representation of the given access modifiers to {@link
     * #buf buf}.
     * 
     * @param access some access modifiers.
     */
    private void appendAccess(final int access) {
        if ( (access & Opcodes.ACC_PUBLIC) != 0 ) {
            this.buf.append( "public " );
        }
        if ( (access & Opcodes.ACC_PRIVATE) != 0 ) {
            this.buf.append( "private " );
        }
        if ( (access & Opcodes.ACC_PROTECTED) != 0 ) {
            this.buf.append( "protected " );
        }
        if ( (access & Opcodes.ACC_FINAL) != 0 ) {
            this.buf.append( "final " );
        }
        if ( (access & Opcodes.ACC_STATIC) != 0 ) {
            this.buf.append( "static " );
        }
        if ( (access & Opcodes.ACC_SYNCHRONIZED) != 0 ) {
            this.buf.append( "synchronized " );
        }
        if ( (access & Opcodes.ACC_VOLATILE) != 0 ) {
            this.buf.append( "volatile " );
        }
        if ( (access & Opcodes.ACC_TRANSIENT) != 0 ) {
            this.buf.append( "transient " );
        }
        // if ((access & Constants.ACC_NATIVE) != 0) {
        // buf.append("native ");
        // }
        if ( (access & Opcodes.ACC_ABSTRACT) != 0 ) {
            this.buf.append( "abstract " );
        }
        if ( (access & Opcodes.ACC_STRICT) != 0 ) {
            this.buf.append( "strictfp " );
        }
        if ( (access & Opcodes.ACC_SYNTHETIC) != 0 ) {
            this.buf.append( "synthetic " );
        }
    }
}
