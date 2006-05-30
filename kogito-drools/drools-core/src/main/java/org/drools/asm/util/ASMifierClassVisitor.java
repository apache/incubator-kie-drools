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
import org.drools.asm.ClassReader;
import org.drools.asm.ClassVisitor;
import org.drools.asm.FieldVisitor;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;

/**
 * A {@link ClassVisitor} that prints the ASM code that generates the classes it
 * visits. This class visitor can be used to quickly write ASM code to generate
 * some given bytecode: <ul> <li>write the Java source code equivalent to the
 * bytecode you want to generate;</li> <li>compile it with <tt>javac</tt>;</li>
 * <li>make a {@link ASMifierClassVisitor} visit this compiled class (see the
 * {@link #main main} method);</li> <li>edit the generated source code, if
 * necessary.</li> </ul> The source code printed when visiting the
 * <tt>Hello</tt> class is the following: <p> <blockquote>
 * 
 * <pre>
 * import org.objectweb.asm.*;
 * 
 * public class HelloDump implements Opcodes {
 * 
 *     public static byte[] dump() throws Exception {
 * 
 *         ClassWriter cw = new ClassWriter(false);
 *         FieldVisitor fv;
 *         MethodVisitor mv;
 *         AnnotationVisitor av0;
 * 
 *         cw.visit(49,
 *                 ACC_PUBLIC + ACC_SUPER,
 *                 &quot;Hello&quot;,
 *                 null,
 *                 &quot;java/lang/Object&quot;,
 *                 null);
 * 
 *         cw.visitSource(&quot;Hello.java&quot;, null);
 * 
 *         {
 *             mv = cw.visitMethod(ACC_PUBLIC, &quot;&lt;init&gt;&quot;, &quot;()V&quot;, null, null);
 *             mv.visitVarInsn(ALOAD, 0);
 *             mv.visitMethodInsn(INVOKESPECIAL,
 *                     &quot;java/lang/Object&quot;,
 *                     &quot;&lt;init&gt;&quot;,
 *                     &quot;()V&quot;);
 *             mv.visitInsn(RETURN);
 *             mv.visitMaxs(1, 1);
 *             mv.visitEnd();
 *         }
 *         {
 *             mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC,
 *                     &quot;main&quot;,
 *                     &quot;([Ljava/lang/String;)V&quot;,
 *                     null,
 *                     null);
 *             mv.visitFieldInsn(GETSTATIC,
 *                     &quot;java/lang/System&quot;,
 *                     &quot;out&quot;,
 *                     &quot;Ljava/io/PrintStream;&quot;);
 *             mv.visitLdcInsn(&quot;hello&quot;);
 *             mv.visitMethodInsn(INVOKEVIRTUAL,
 *                     &quot;java/io/PrintStream&quot;,
 *                     &quot;println&quot;,
 *                     &quot;(Ljava/lang/String;)V&quot;);
 *             mv.visitInsn(RETURN);
 *             mv.visitMaxs(2, 1);
 *             mv.visitEnd();
 *         }
 *         cw.visitEnd();
 * 
 *         return cw.toByteArray();
 *     }
 * }
 * 
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
public class ASMifierClassVisitor extends ASMifierAbstractVisitor
    implements
    ClassVisitor {

    /**
     * Pseudo access flag used to distinguish class access flags.
     */
    private final static int    ACCESS_CLASS = 262144;

    /**
     * Pseudo access flag used to distinguish field access flags.
     */
    private final static int    ACCESS_FIELD = 524288;

    /**
     * Pseudo access flag used to distinguish inner class flags.
     */
    private static final int    ACCESS_INNER = 1048576;

    /**
     * The print writer to be used to print the class.
     */
    protected final PrintWriter pw;

    /**
     * Prints the ASM source code to generate the given class to the standard
     * output. <p> Usage: ASMifierClassVisitor [-debug] &lt;fully qualified
     * class name or class file name&gt;
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
            System.err.println( "Prints the ASM code to generate the given class." );
            System.err.println( "Usage: ASMifierClassVisitor [-debug] " + "<fully qualified class name or class file name>" );
            return;
        }
        ClassReader cr;
        if ( args[i].endsWith( ".class" ) || args[i].indexOf( '\\' ) > -1 || args[i].indexOf( '/' ) > -1 ) {
            cr = new ClassReader( new FileInputStream( args[i] ) );
        } else {
            cr = new ClassReader( args[i] );
        }
        cr.accept( new ASMifierClassVisitor( new PrintWriter( System.out ) ),
                   getDefaultAttributes(),
                   skipDebug );
    }

    /**
     * Constructs a new {@link ASMifierClassVisitor} object.
     * 
     * @param pw the print writer to be used to print the class.
     */
    public ASMifierClassVisitor(final PrintWriter pw) {
        super( "cw" );
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
        String simpleName;
        final int n = name.lastIndexOf( '/' );
        if ( n != -1 ) {
            this.text.add( "package asm." + name.substring( 0,
                                                            n ).replace( '/',
                                                                         '.' ) + ";\n" );
            simpleName = name.substring( n + 1 );
        } else {
            simpleName = name;
        }
        this.text.add( "import java.util.*;\n" );
        this.text.add( "import org.objectweb.asm.*;\n" );
        this.text.add( "import org.objectweb.asm.attrs.*;\n" );
        this.text.add( "public class " + simpleName + "Dump implements Opcodes {\n\n" );
        this.text.add( "public static byte[] dump () throws Exception {\n\n" );
        this.text.add( "ClassWriter cw = new ClassWriter(false);\n" );
        this.text.add( "FieldVisitor fv;\n" );
        this.text.add( "MethodVisitor mv;\n" );
        this.text.add( "AnnotationVisitor av0;\n\n" );

        this.buf.setLength( 0 );
        this.buf.append( "cw.visit(" );
        switch ( version ) {
            case Opcodes.V1_1 :
                this.buf.append( "V1_1" );
                break;
            case Opcodes.V1_2 :
                this.buf.append( "V1_2" );
                break;
            case Opcodes.V1_3 :
                this.buf.append( "V1_3" );
                break;
            case Opcodes.V1_4 :
                this.buf.append( "V1_4" );
                break;
            case Opcodes.V1_5 :
                this.buf.append( "V1_5" );
                break;
            case Opcodes.V1_6 :
                this.buf.append( "V1_6" );
                break;
            default :
                this.buf.append( version );
                break;
        }
        this.buf.append( ", " );
        appendAccess( access | ASMifierClassVisitor.ACCESS_CLASS );
        this.buf.append( ", " );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( signature );
        this.buf.append( ", " );
        appendConstant( superName );
        this.buf.append( ", " );
        if ( interfaces != null && interfaces.length > 0 ) {
            this.buf.append( "new String[] {" );
            for ( int i = 0; i < interfaces.length; ++i ) {
                this.buf.append( i == 0 ? " " : ", " );
                appendConstant( interfaces[i] );
            }
            this.buf.append( " }" );
        } else {
            this.buf.append( "null" );
        }
        this.buf.append( ");\n\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitSource(final String file,
                            final String debug) {
        this.buf.setLength( 0 );
        this.buf.append( "cw.visitSource(" );
        appendConstant( file );
        this.buf.append( ", " );
        appendConstant( debug );
        this.buf.append( ");\n\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitOuterClass(final String owner,
                                final String name,
                                final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( "cw.visitOuterClass(" );
        appendConstant( owner );
        this.buf.append( ", " );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( desc );
        this.buf.append( ");\n\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitInnerClass(final String name,
                                final String outerName,
                                final String innerName,
                                final int access) {
        this.buf.setLength( 0 );
        this.buf.append( "cw.visitInnerClass(" );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( outerName );
        this.buf.append( ", " );
        appendConstant( innerName );
        this.buf.append( ", " );
        appendAccess( access | ASMifierClassVisitor.ACCESS_INNER );
        this.buf.append( ");\n\n" );
        this.text.add( this.buf.toString() );
    }

    public FieldVisitor visitField(final int access,
                                   final String name,
                                   final String desc,
                                   final String signature,
                                   final Object value) {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" );
        this.buf.append( "fv = cw.visitField(" );
        appendAccess( access | ASMifierClassVisitor.ACCESS_FIELD );
        this.buf.append( ", " );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( desc );
        this.buf.append( ", " );
        appendConstant( signature );
        this.buf.append( ", " );
        appendConstant( value );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
        final ASMifierFieldVisitor aav = new ASMifierFieldVisitor();
        this.text.add( aav.getText() );
        this.text.add( "}\n" );
        return aav;
    }

    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" );
        this.buf.append( "mv = cw.visitMethod(" );
        appendAccess( access );
        this.buf.append( ", " );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( desc );
        this.buf.append( ", " );
        appendConstant( signature );
        this.buf.append( ", " );
        if ( exceptions != null && exceptions.length > 0 ) {
            this.buf.append( "new String[] {" );
            for ( int i = 0; i < exceptions.length; ++i ) {
                this.buf.append( i == 0 ? " " : ", " );
                appendConstant( exceptions[i] );
            }
            this.buf.append( " }" );
        } else {
            this.buf.append( "null" );
        }
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
        final ASMifierMethodVisitor acv = new ASMifierMethodVisitor();
        this.text.add( acv.getText() );
        this.text.add( "}\n" );
        return acv;
    }

    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" );
        this.buf.append( "av0 = cw.visitAnnotation(" );
        appendConstant( desc );
        this.buf.append( ", " );
        this.buf.append( visible );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
        final ASMifierAnnotationVisitor av = new ASMifierAnnotationVisitor( 0 );
        this.text.add( av.getText() );
        this.text.add( "}\n" );
        return av;
    }

    public void visitEnd() {
        this.text.add( "cw.visitEnd();\n\n" );
        this.text.add( "return cw.toByteArray();\n" );
        this.text.add( "}\n" );
        this.text.add( "}\n" );
        printList( this.pw,
                   this.text );
        this.pw.flush();
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Appends a string representation of the given access modifiers to {@link
     * #buf buf}.
     * 
     * @param access some access modifiers.
     */
    void appendAccess(final int access) {
        boolean first = true;
        if ( (access & Opcodes.ACC_PUBLIC) != 0 ) {
            this.buf.append( "ACC_PUBLIC" );
            first = false;
        }
        if ( (access & Opcodes.ACC_PRIVATE) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_PRIVATE" );
            first = false;
        }
        if ( (access & Opcodes.ACC_PROTECTED) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_PROTECTED" );
            first = false;
        }
        if ( (access & Opcodes.ACC_FINAL) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_FINAL" );
            first = false;
        }
        if ( (access & Opcodes.ACC_STATIC) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_STATIC" );
            first = false;
        }
        if ( (access & Opcodes.ACC_SYNCHRONIZED) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            if ( (access & ASMifierClassVisitor.ACCESS_CLASS) != 0 ) {
                this.buf.append( "ACC_SUPER" );
            } else {
                this.buf.append( "ACC_SYNCHRONIZED" );
            }
            first = false;
        }
        if ( (access & Opcodes.ACC_VOLATILE) != 0 && (access & ASMifierClassVisitor.ACCESS_FIELD) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_VOLATILE" );
            first = false;
        }
        if ( (access & Opcodes.ACC_BRIDGE) != 0 && (access & ASMifierClassVisitor.ACCESS_CLASS) == 0 && (access & ASMifierClassVisitor.ACCESS_FIELD) == 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_BRIDGE" );
            first = false;
        }
        if ( (access & Opcodes.ACC_VARARGS) != 0 && (access & ASMifierClassVisitor.ACCESS_CLASS) == 0 && (access & ASMifierClassVisitor.ACCESS_FIELD) == 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_VARARGS" );
            first = false;
        }
        if ( (access & Opcodes.ACC_TRANSIENT) != 0 && (access & ASMifierClassVisitor.ACCESS_FIELD) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_TRANSIENT" );
            first = false;
        }
        if ( (access & Opcodes.ACC_NATIVE) != 0 && (access & ASMifierClassVisitor.ACCESS_CLASS) == 0 && (access & ASMifierClassVisitor.ACCESS_FIELD) == 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_NATIVE" );
            first = false;
        }
        if ( (access & Opcodes.ACC_ENUM) != 0 && ((access & ASMifierClassVisitor.ACCESS_CLASS) != 0 || (access & ASMifierClassVisitor.ACCESS_FIELD) != 0 || (access & ASMifierClassVisitor.ACCESS_INNER) != 0) ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_ENUM" );
            first = false;
        }
        if ( (access & Opcodes.ACC_ANNOTATION) != 0 && ((access & ASMifierClassVisitor.ACCESS_CLASS) != 0) ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_ANNOTATION" );
            first = false;
        }
        if ( (access & Opcodes.ACC_ABSTRACT) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_ABSTRACT" );
            first = false;
        }
        if ( (access & Opcodes.ACC_INTERFACE) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_INTERFACE" );
            first = false;
        }
        if ( (access & Opcodes.ACC_STRICT) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_STRICT" );
            first = false;
        }
        if ( (access & Opcodes.ACC_SYNTHETIC) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_SYNTHETIC" );
            first = false;
        }
        if ( (access & Opcodes.ACC_DEPRECATED) != 0 ) {
            if ( !first ) {
                this.buf.append( " + " );
            }
            this.buf.append( "ACC_DEPRECATED" );
            first = false;
        }
        if ( first ) {
            this.buf.append( "0" );
        }
    }

    /**
     * Appends a string representation of the given constant to the given
     * buffer.
     * 
     * @param buf a string buffer.
     * @param cst an {@link java.lang.Integer Integer}, {@link java.lang.Float
     *        Float}, {@link java.lang.Long Long},
     *        {@link java.lang.Double Double} or {@link String String} object.
     *        May be <tt>null</tt>.
     */
    static void appendConstant(final StringBuffer buf,
                               final Object cst) {
        if ( cst == null ) {
            buf.append( "null" );
        } else if ( cst instanceof String ) {
            AbstractVisitor.appendString( buf,
                                          (String) cst );
        } else if ( cst instanceof Type ) {
            buf.append( "Type.getType(\"" ).append( ((Type) cst).getDescriptor() ).append( "\")" );
        } else if ( cst instanceof Integer ) {
            buf.append( "new Integer(" ).append( cst ).append( ")" );
        } else if ( cst instanceof Float ) {
            buf.append( "new Float(\"" ).append( cst ).append( "\")" );
        } else if ( cst instanceof Long ) {
            buf.append( "new Long(" ).append( cst ).append( "L)" );
        } else if ( cst instanceof Double ) {
            buf.append( "new Double(\"" ).append( cst ).append( "\")" );
        }
    }
}
