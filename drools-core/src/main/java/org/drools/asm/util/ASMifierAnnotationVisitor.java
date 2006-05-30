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

import org.drools.asm.AnnotationVisitor;

/**
 * An {@link AnnotationVisitor} that prints the ASM code that generates the
 * annotations it visits.
 * 
 * @author Eric Bruneton
 */
public class ASMifierAnnotationVisitor extends AbstractVisitor
    implements
    AnnotationVisitor {

    /**
     * Identifier of the annotation visitor variable in the produced code.
     */
    protected final int id;

    /**
     * Constructs a new {@link ASMifierAnnotationVisitor}.
     * 
     * @param id identifier of the annotation visitor variable in the produced
     *        code.
     */
    public ASMifierAnnotationVisitor(final int id) {
        this.id = id;
    }

    // ------------------------------------------------------------------------
    // Implementation of the AnnotationVisitor interface
    // ------------------------------------------------------------------------

    public void visit(final String name,
                      final Object value) {
        this.buf.setLength( 0 );
        this.buf.append( "av" ).append( this.id ).append( ".visit(" );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                name );
        this.buf.append( ", " );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                value );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public void visitEnum(final String name,
                          final String desc,
                          final String value) {
        this.buf.setLength( 0 );
        this.buf.append( "av" ).append( this.id ).append( ".visitEnum(" );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                name );
        this.buf.append( ", " );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                desc );
        this.buf.append( ", " );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                value );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
    }

    public AnnotationVisitor visitAnnotation(final String name,
                                             final String desc) {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" );
        this.buf.append( "AnnotationVisitor av" ).append( this.id + 1 ).append( " = av" );
        this.buf.append( this.id ).append( ".visitAnnotation(" );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                name );
        this.buf.append( ", " );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                desc );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
        final ASMifierAnnotationVisitor av = new ASMifierAnnotationVisitor( this.id + 1 );
        this.text.add( av.getText() );
        this.text.add( "}\n" );
        return av;
    }

    public AnnotationVisitor visitArray(final String name) {
        this.buf.setLength( 0 );
        this.buf.append( "{\n" );
        this.buf.append( "AnnotationVisitor av" ).append( this.id + 1 ).append( " = av" );
        this.buf.append( this.id ).append( ".visitArray(" );
        ASMifierAbstractVisitor.appendConstant( this.buf,
                                                name );
        this.buf.append( ");\n" );
        this.text.add( this.buf.toString() );
        final ASMifierAnnotationVisitor av = new ASMifierAnnotationVisitor( this.id + 1 );
        this.text.add( av.getText() );
        this.text.add( "}\n" );
        return av;
    }

    public void visitEnd() {
        this.buf.setLength( 0 );
        this.buf.append( "av" ).append( this.id ).append( ".visitEnd();\n" );
        this.text.add( this.buf.toString() );
    }
}
