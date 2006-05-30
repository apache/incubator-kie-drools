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
import org.drools.asm.Attribute;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;
import org.drools.asm.signature.SignatureReader;
import org.drools.asm.util.attrs.Traceable;

/**
 * A {@link MethodVisitor} that prints a disassembled view of the methods it
 * visits.
 * 
 * @author Eric Bruneton
 */
public class TraceMethodVisitor extends TraceAbstractVisitor
    implements
    MethodVisitor {

    /**
     * The {@link MethodVisitor} to which this visitor delegates calls. May be
     * <tt>null</tt>.
     */
    protected MethodVisitor mv;

    /**
     * Tab for bytecode instructions.
     */
    protected String        tab2 = "    ";

    /**
     * Tab for table and lookup switch instructions.
     */
    protected String        tab3 = "      ";

    /**
     * Tab for labels.
     */
    protected String        ltab = "   ";

    /**
     * The label names. This map associate String values to Label keys.
     */
    protected final HashMap labelNames;

    /**
     * Constructs a new {@link TraceMethodVisitor}.
     */
    public TraceMethodVisitor() {
        this( null );
    }

    /**
     * Constructs a new {@link TraceMethodVisitor}.
     * 
     * @param mv the {@link MethodVisitor} to which this visitor delegates
     *        calls. May be <tt>null</tt>.
     */
    public TraceMethodVisitor(final MethodVisitor mv) {
        this.labelNames = new HashMap();
        this.mv = mv;
    }

    // ------------------------------------------------------------------------
    // Implementation of the MethodVisitor interface
    // ------------------------------------------------------------------------

    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        final AnnotationVisitor av = super.visitAnnotation( desc,
                                                            visible );
        if ( this.mv != null ) {
            ((TraceAnnotationVisitor) av).av = this.mv.visitAnnotation( desc,
                                                                        visible );
        }
        return av;
    }

    public void visitAttribute(final Attribute attr) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab ).append( "ATTRIBUTE " );
        appendDescriptor( -1,
                          attr.type );

        if ( attr instanceof Traceable ) {
            ((Traceable) attr).trace( this.buf,
                                      this.labelNames );
        } else {
            this.buf.append( " : " ).append( attr.toString() ).append( "\n" );
        }

        this.text.add( this.buf.toString() );
        if ( this.mv != null ) {
            this.mv.visitAttribute( attr );
        }
    }

    public AnnotationVisitor visitAnnotationDefault() {
        this.text.add( this.tab2 + "default=" );
        final TraceAnnotationVisitor tav = new TraceAnnotationVisitor();
        this.text.add( tav.getText() );
        this.text.add( "\n" );
        if ( this.mv != null ) {
            tav.av = this.mv.visitAnnotationDefault();
        }
        return tav;
    }

    public AnnotationVisitor visitParameterAnnotation(final int parameter,
                                                      final String desc,
                                                      final boolean visible) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( '@' );
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( '(' );
        this.text.add( this.buf.toString() );
        final TraceAnnotationVisitor tav = new TraceAnnotationVisitor();
        this.text.add( tav.getText() );
        this.text.add( visible ? ") // parameter " : ") // invisible, parameter " );
        this.text.add( new Integer( parameter ) );
        this.text.add( "\n" );
        if ( this.mv != null ) {
            tav.av = this.mv.visitParameterAnnotation( parameter,
                                                       desc,
                                                       visible );
        }
        return tav;
    }

    public void visitCode() {
        if ( this.mv != null ) {
            this.mv.visitCode();
        }
    }

    public void visitInsn(final int opcode) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitInsn( opcode );
        }
    }

    public void visitIntInsn(final int opcode,
                             final int operand) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( ' ' ).append( opcode == Opcodes.NEWARRAY ? AbstractVisitor.TYPES[operand] : Integer.toString( operand ) ).append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitIntInsn( opcode,
                                  operand );
        }
    }

    public void visitVarInsn(final int opcode,
                             final int var) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( ' ' ).append( var ).append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitVarInsn( opcode,
                                  var );
        }
    }

    public void visitTypeInsn(final int opcode,
                              final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( ' ' );
        if ( desc.startsWith( "[" ) ) {
            appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                              desc );
        } else {
            appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                              desc );
        }
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitTypeInsn( opcode,
                                   desc );
        }
    }

    public void visitFieldInsn(final int opcode,
                               final String owner,
                               final String name,
                               final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( ' ' );
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          owner );
        this.buf.append( '.' ).append( name ).append( " : " );
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitFieldInsn( opcode,
                                    owner,
                                    name,
                                    desc );
        }
    }

    public void visitMethodInsn(final int opcode,
                                final String owner,
                                final String name,
                                final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( ' ' );
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          owner );
        this.buf.append( '.' ).append( name ).append( ' ' );
        appendDescriptor( TraceAbstractVisitor.METHOD_DESCRIPTOR,
                          desc );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitMethodInsn( opcode,
                                     owner,
                                     name,
                                     desc );
        }
    }

    public void visitJumpInsn(final int opcode,
                              final Label label) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( AbstractVisitor.OPCODES[opcode] ).append( ' ' );
        appendLabel( label );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitJumpInsn( opcode,
                                   label );
        }
    }

    public void visitLabel(final Label label) {
        this.buf.setLength( 0 );
        this.buf.append( this.ltab );
        appendLabel( label );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitLabel( label );
        }
    }

    public void visitLdcInsn(final Object cst) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "LDC " );
        if ( cst instanceof String ) {
            AbstractVisitor.appendString( this.buf,
                                          (String) cst );
        } else if ( cst instanceof Type ) {
            this.buf.append( ((Type) cst).getDescriptor() + ".class" );
        } else {
            this.buf.append( cst );
        }
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitLdcInsn( cst );
        }
    }

    public void visitIincInsn(final int var,
                              final int increment) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "IINC " ).append( var ).append( ' ' ).append( increment ).append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitIincInsn( var,
                                   increment );
        }
    }

    public void visitTableSwitchInsn(final int min,
                                     final int max,
                                     final Label dflt,
                                     final Label labels[]) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "TABLESWITCH\n" );
        for ( int i = 0; i < labels.length; ++i ) {
            this.buf.append( this.tab3 ).append( min + i ).append( ": " );
            appendLabel( labels[i] );
            this.buf.append( '\n' );
        }
        this.buf.append( this.tab3 ).append( "default: " );
        appendLabel( dflt );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitTableSwitchInsn( min,
                                          max,
                                          dflt,
                                          labels );
        }
    }

    public void visitLookupSwitchInsn(final Label dflt,
                                      final int keys[],
                                      final Label labels[]) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "LOOKUPSWITCH\n" );
        for ( int i = 0; i < labels.length; ++i ) {
            this.buf.append( this.tab3 ).append( keys[i] ).append( ": " );
            appendLabel( labels[i] );
            this.buf.append( '\n' );
        }
        this.buf.append( this.tab3 ).append( "default: " );
        appendLabel( dflt );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitLookupSwitchInsn( dflt,
                                           keys,
                                           labels );
        }
    }

    public void visitMultiANewArrayInsn(final String desc,
                                        final int dims) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "MULTIANEWARRAY " );
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( ' ' ).append( dims ).append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitMultiANewArrayInsn( desc,
                                             dims );
        }
    }

    public void visitTryCatchBlock(final Label start,
                                   final Label end,
                                   final Label handler,
                                   final String type) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "TRYCATCHBLOCK " );
        appendLabel( start );
        this.buf.append( ' ' );
        appendLabel( end );
        this.buf.append( ' ' );
        appendLabel( handler );
        this.buf.append( ' ' );
        appendDescriptor( TraceAbstractVisitor.INTERNAL_NAME,
                          type );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitTryCatchBlock( start,
                                        end,
                                        handler,
                                        type );
        }
    }

    public void visitLocalVariable(final String name,
                                   final String desc,
                                   final String signature,
                                   final Label start,
                                   final Label end,
                                   final int index) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "LOCALVARIABLE " ).append( name ).append( ' ' );
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( ' ' );
        appendLabel( start );
        this.buf.append( ' ' );
        appendLabel( end );
        this.buf.append( ' ' ).append( index ).append( '\n' );

        if ( signature != null ) {
            this.buf.append( this.tab2 );
            appendDescriptor( TraceAbstractVisitor.FIELD_SIGNATURE,
                              signature );

            final TraceSignatureVisitor sv = new TraceSignatureVisitor( 0 );
            final SignatureReader r = new SignatureReader( signature );
            r.acceptType( sv );
            this.buf.append( this.tab2 ).append( "// declaration: " ).append( sv.getDeclaration() ).append( '\n' );
        }
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitLocalVariable( name,
                                        desc,
                                        signature,
                                        start,
                                        end,
                                        index );
        }
    }

    public void visitLineNumber(final int line,
                                final Label start) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "LINENUMBER " ).append( line ).append( ' ' );
        appendLabel( start );
        this.buf.append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitLineNumber( line,
                                     start );
        }
    }

    public void visitMaxs(final int maxStack,
                          final int maxLocals) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "MAXSTACK = " ).append( maxStack ).append( '\n' );
        this.text.add( this.buf.toString() );

        this.buf.setLength( 0 );
        this.buf.append( this.tab2 ).append( "MAXLOCALS = " ).append( maxLocals ).append( '\n' );
        this.text.add( this.buf.toString() );

        if ( this.mv != null ) {
            this.mv.visitMaxs( maxStack,
                               maxLocals );
        }
    }

    public void visitEnd() {
        super.visitEnd();

        if ( this.mv != null ) {
            this.mv.visitEnd();
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Appends the name of the given label to {@link #buf buf}. Creates a new
     * label name if the given label does not yet have one.
     * 
     * @param l a label.
     */
    public void appendLabel(final Label l) {
        String name = (String) this.labelNames.get( l );
        if ( name == null ) {
            name = "L" + this.labelNames.size();
            this.labelNames.put( l,
                                 name );
        }
        this.buf.append( name );
    }
}
