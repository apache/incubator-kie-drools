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
import org.drools.asm.Attribute;
import org.drools.asm.util.attrs.Traceable;

/**
 * An abstract trace visitor.
 * 
 * @author Eric Bruneton
 */
public abstract class TraceAbstractVisitor extends AbstractVisitor {

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for internal
     * type names in bytecode notation.
     */
    public final static int INTERNAL_NAME          = 0;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for field
     * descriptors, formatted in bytecode notation
     */
    public final static int FIELD_DESCRIPTOR       = 1;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for field
     * signatures, formatted in bytecode notation
     */
    public final static int FIELD_SIGNATURE        = 2;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for method
     * descriptors, formatted in bytecode notation
     */
    public final static int METHOD_DESCRIPTOR      = 3;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for method
     * signatures, formatted in bytecode notation
     */
    public final static int METHOD_SIGNATURE       = 4;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for class
     * signatures, formatted in bytecode notation
     */
    public final static int CLASS_SIGNATURE        = 5;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for field or
     * method return value signatures, formatted in default Java notation
     * (non-bytecode)
     */
    public final static int TYPE_DECLARATION       = 6;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for class
     * signatures, formatted in default Java notation (non-bytecode)
     */
    public final static int CLASS_DECLARATION      = 7;

    /**
     * Constant used in {@link #appendDescriptor appendDescriptor} for method
     * parameter signatures, formatted in default Java notation (non-bytecode)
     */
    public final static int PARAMETERS_DECLARATION = 8;

    /**
     * Tab for class members.
     */
    protected String        tab                    = "  ";

    /**
     * Prints a disassembled view of the given annotation.
     * 
     * @param desc the class descriptor of the annotation class.
     * @param visible <tt>true</tt> if the annotation is visible at runtime.
     * @return a visitor to visit the annotation values.
     */
    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab ).append( '@' );
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( '(' );
        this.text.add( this.buf.toString() );
        final TraceAnnotationVisitor tav = createTraceAnnotationVisitor();
        this.text.add( tav.getText() );
        this.text.add( visible ? ")\n" : ") // invisible\n" );
        return tav;
    }

    /**
     * Prints a disassembled view of the given attribute.
     * 
     * @param attr an attribute.
     */
    public void visitAttribute(final Attribute attr) {
        this.buf.setLength( 0 );
        this.buf.append( this.tab ).append( "ATTRIBUTE " );
        appendDescriptor( -1,
                          attr.type );

        if ( attr instanceof Traceable ) {
            ((Traceable) attr).trace( this.buf,
                                      null );
        } else {
            this.buf.append( " : " ).append( attr.toString() ).append( "\n" );
        }

        this.text.add( this.buf.toString() );
    }

    /**
     * Does nothing.
     */
    public void visitEnd() {
        // does nothing
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    protected TraceAnnotationVisitor createTraceAnnotationVisitor() {
        return new TraceAnnotationVisitor();
    }

    /**
     * Appends an internal name, a type descriptor or a type signature to
     * {@link #buf buf}.
     * 
     * @param type indicates if desc is an internal name, a field descriptor, a
     *        method descriptor, a class signature, ...
     * @param desc an internal name, type descriptor, or type signature. May be
     *        <tt>null</tt>.
     */
    protected void appendDescriptor(final int type,
                                    final String desc) {
        if ( type == TraceAbstractVisitor.CLASS_SIGNATURE || type == TraceAbstractVisitor.FIELD_SIGNATURE || type == TraceAbstractVisitor.METHOD_SIGNATURE ) {
            if ( desc != null ) {
                this.buf.append( "// signature " ).append( desc ).append( '\n' );
            }
        } else {
            this.buf.append( desc );
        }
    }

}
