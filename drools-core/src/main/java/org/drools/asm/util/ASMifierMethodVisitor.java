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

import java.util.HashMap;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;

/**
 * A {@link MethodVisitor} that prints the ASM code that generates the methods
 * it visits.
 * 
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
public class ASMifierMethodVisitor extends ASMifierAbstractVisitor
    implements
    MethodVisitor {

    /**
     * Constructs a new {@link ASMifierMethodVisitor} object.
     */
    public ASMifierMethodVisitor() {
        super( "mv" );
        this.labelNames = new HashMap();
    }

    public AnnotationVisitor visitAnnotationDefault() {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" ).append( "av0 = mv.visitAnnotationDefault();\n" );
        this.text.add( this.buf.toString() );
        final ASMifierAnnotationVisitor av = new ASMifierAnnotationVisitor( 0 );
        this.text.add( av.getText() );
        this.text.add( "}\n" );
        return av;
    }

    public AnnotationVisitor visitParameterAnnotation(final int parameter,
                                                      final String desc,
                                                      final boolean visible) {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" ).append( "av0 = mv.visitParameterAnnotation(" ).append( parameter ).append( ", " );
        appendConstant( desc );
        this.buf.append( ", " ).append( visible ).append( ");\n" );
        this.text.add( this.buf.toString() );
        final ASMifierAnnotationVisitor av = new ASMifierAnnotationVisitor( 0 );
        this.text.add( av.getText() );
        this.text.add( "}\n" );
        return av;
    }

    public void visitCode() {
        this.text.add( "mv.visitCode();\n" );
    }

    public void visitInsn(final int opcode) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitIntInsn(final int opcode,
                             final int operand) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitIntInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ", " ).append( opcode == Opcodes.NEWARRAY ? AbstractVisitor.TYPES[operand] : Integer.toString( operand ) ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitVarInsn(final int opcode,
                             final int var) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitVarInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ", " ).append( var ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitTypeInsn(final int opcode,
                              final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitTypeInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ", " );
        appendConstant( desc );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitFieldInsn(final int opcode,
                               final String owner,
                               final String name,
                               final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitFieldInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ", " );
        appendConstant( owner );
        this.buf.append( ", " );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( desc );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitMethodInsn(final int opcode,
                                final String owner,
                                final String name,
                                final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitMethodInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ", " );
        appendConstant( owner );
        this.buf.append( ", " );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( desc );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitJumpInsn(final int opcode,
                              final Label label) {
        this.buf.setLength( 0 );
        declareLabel( label );
        this.buf.append( "mv.visitJumpInsn(" ).append( AbstractVisitor.OPCODES[opcode] ).append( ", " );
        appendLabel( label );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitLabel(final Label label) {
        this.buf.setLength( 0 );
        declareLabel( label );
        this.buf.append( "mv.visitLabel(" );
        appendLabel( label );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitLdcInsn(final Object cst) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitLdcInsn(" );
        appendConstant( cst );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitIincInsn(final int var,
                              final int increment) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitIincInsn(" ).append( var ).append( ", " ).append( increment ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitTableSwitchInsn(final int min,
                                     final int max,
                                     final Label dflt,
                                     final Label labels[]) {
        this.buf.setLength( 0 );
        for ( int i = 0; i < labels.length; ++i ) {
            declareLabel( labels[i] );
        }
        declareLabel( dflt );

        this.buf.append( "mv.visitTableSwitchInsn(" ).append( min ).append( ", " ).append( max ).append( ", " );
        appendLabel( dflt );
        this.buf.append( ", new Label[] {" );
        for ( int i = 0; i < labels.length; ++i ) {
            this.buf.append( i == 0 ? " " : ", " );
            appendLabel( labels[i] );
        }
        this.buf.append( " });\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitLookupSwitchInsn(final Label dflt,
                                      final int keys[],
                                      final Label labels[]) {
        this.buf.setLength( 0 );
        for ( int i = 0; i < labels.length; ++i ) {
            declareLabel( labels[i] );
        }
        declareLabel( dflt );

        this.buf.append( "mv.visitLookupSwitchInsn(" );
        appendLabel( dflt );
        this.buf.append( ", new int[] {" );
        for ( int i = 0; i < keys.length; ++i ) {
            this.buf.append( i == 0 ? " " : ", " ).append( keys[i] );
        }
        this.buf.append( " }, new Label[] {" );
        for ( int i = 0; i < labels.length; ++i ) {
            this.buf.append( i == 0 ? " " : ", " );
            appendLabel( labels[i] );
        }
        this.buf.append( " });\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitMultiANewArrayInsn(final String desc,
                                        final int dims) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitMultiANewArrayInsn(" );
        appendConstant( desc );
        this.buf.append( ", " ).append( dims ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitTryCatchBlock(final Label start,
                                   final Label end,
                                   final Label handler,
                                   final String type) {
        this.buf.setLength( 0 );
        declareLabel( start );
        declareLabel( end );
        declareLabel( handler );
        this.buf.append( "mv.visitTryCatchBlock(" );
        appendLabel( start );
        this.buf.append( ", " );
        appendLabel( end );
        this.buf.append( ", " );
        appendLabel( handler );
        this.buf.append( ", " );
        appendConstant( type );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitLocalVariable(final String name,
                                   final String desc,
                                   final String signature,
                                   final Label start,
                                   final Label end,
                                   final int index) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitLocalVariable(" );
        appendConstant( name );
        this.buf.append( ", " );
        appendConstant( desc );
        this.buf.append( ", " );
        appendConstant( signature );
        this.buf.append( ", " );
        appendLabel( start );
        this.buf.append( ", " );
        appendLabel( end );
        this.buf.append( ", " ).append( index ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitLineNumber(final int line,
                                final Label start) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitLineNumber(" ).append( line ).append( ", " );
        appendLabel( start );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitMaxs(final int maxStack,
                          final int maxLocals) {
        this.buf.setLength( 0 );
        this.buf.append( "mv.visitMaxs(" ).append( maxStack ).append( ", " ).append( maxLocals ).append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    /**
     * Appends a declaration of the given label to {@link #buf buf}. This
     * declaration is of the form "Label lXXX = new Label();". Does nothing if
     * the given label has already been declared.
     * 
     * @param l a label.
     */
    private void declareLabel(final Label l) {
        String name = (String) this.labelNames.get( l );
        if ( name == null ) {
            name = "l" + this.labelNames.size();
            this.labelNames.put( l,
                                 name );
            this.buf.append( "Label " ).append( name ).append( " = new Label();\n" );
        }
    }

    /**
     * Appends the name of the given label to {@link #buf buf}. The given label
     * <i>must</i> already have a name. One way to ensure this is to always
     * call {@link #declareLabel declared} before calling this method.
     * 
     * @param l a label.
     */
    private void appendLabel(final Label l) {
        this.buf.append( (String) this.labelNames.get( l ) );
    }
}
