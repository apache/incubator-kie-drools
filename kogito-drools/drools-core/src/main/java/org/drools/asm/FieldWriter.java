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
package org.drools.asm;

/**
 * An {@link FieldVisitor} that generates Java fields in bytecode form.
 * 
 * @author Eric Bruneton
 */
final class FieldWriter
    implements
    FieldVisitor {

    /**
     * Next field writer (see {@link ClassWriter#firstField firstField}).
     */
    FieldWriter              next;

    /**
     * The class writer to which this field must be added.
     */
    private ClassWriter      cw;

    /**
     * Access flags of this field.
     */
    private int              access;

    /**
     * The index of the constant pool item that contains the name of this
     * method.
     */
    private int              name;

    /**
     * The index of the constant pool item that contains the descriptor of this
     * field.
     */
    private int              desc;

    /**
     * The index of the constant pool item that contains the signature of this
     * field.
     */
    private int              signature;

    /**
     * The index of the constant pool item that contains the constant value of
     * this field.
     */
    private int              value;

    /**
     * The runtime visible annotations of this field. May be <tt>null</tt>.
     */
    private AnnotationWriter anns;

    /**
     * The runtime invisible annotations of this field. May be <tt>null</tt>.
     */
    private AnnotationWriter ianns;

    /**
     * The non standard attributes of this field. May be <tt>null</tt>.
     */
    private Attribute        attrs;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link FieldWriter}.
     * 
     * @param cw the class writer to which this field must be added.
     * @param access the field's access flags (see {@link Opcodes}).
     * @param name the field's name.
     * @param desc the field's descriptor (see {@link Type}).
     * @param signature the field's signature. May be <tt>null</tt>.
     * @param value the field's constant value. May be <tt>null</tt>.
     */
    protected FieldWriter(final ClassWriter cw,
                          final int access,
                          final String name,
                          final String desc,
                          final String signature,
                          final Object value) {
        if ( cw.firstField == null ) {
            cw.firstField = this;
        } else {
            cw.lastField.next = this;
        }
        cw.lastField = this;
        this.cw = cw;
        this.access = access;
        this.name = cw.newUTF8( name );
        this.desc = cw.newUTF8( desc );
        if ( signature != null ) {
            this.signature = cw.newUTF8( signature );
        }
        if ( value != null ) {
            this.value = cw.newConstItem( value ).index;
        }
    }

    // ------------------------------------------------------------------------
    // Implementation of the FieldVisitor interface
    // ------------------------------------------------------------------------

    public AnnotationVisitor visitAnnotation(final String desc,
                                             final boolean visible) {
        final ByteVector bv = new ByteVector();
        // write type, and reserve space for values count
        bv.putShort( this.cw.newUTF8( desc ) ).putShort( 0 );
        final AnnotationWriter aw = new AnnotationWriter( this.cw,
                                                          true,
                                                          bv,
                                                          bv,
                                                          2 );
        if ( visible ) {
            aw.next = this.anns;
            this.anns = aw;
        } else {
            aw.next = this.ianns;
            this.ianns = aw;
        }
        return aw;
    }

    public void visitAttribute(final Attribute attr) {
        attr.next = this.attrs;
        this.attrs = attr;
    }

    public void visitEnd() {
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Returns the size of this field.
     * 
     * @return the size of this field.
     */
    int getSize() {
        int size = 8;
        if ( this.value != 0 ) {
            this.cw.newUTF8( "ConstantValue" );
            size += 8;
        }
        if ( (this.access & Opcodes.ACC_SYNTHETIC) != 0 && (this.cw.version & 0xffff) < Opcodes.V1_5 ) {
            this.cw.newUTF8( "Synthetic" );
            size += 6;
        }
        if ( (this.access & Opcodes.ACC_DEPRECATED) != 0 ) {
            this.cw.newUTF8( "Deprecated" );
            size += 6;
        }
        if ( this.cw.version == Opcodes.V1_4 && (this.access & Opcodes.ACC_ENUM) != 0 ) {
            this.cw.newUTF8( "Enum" );
            size += 6;
        }
        if ( this.signature != 0 ) {
            this.cw.newUTF8( "Signature" );
            size += 8;
        }
        if ( this.anns != null ) {
            this.cw.newUTF8( "RuntimeVisibleAnnotations" );
            size += 8 + this.anns.getSize();
        }
        if ( this.ianns != null ) {
            this.cw.newUTF8( "RuntimeInvisibleAnnotations" );
            size += 8 + this.ianns.getSize();
        }
        if ( this.attrs != null ) {
            size += this.attrs.getSize( this.cw,
                                        null,
                                        0,
                                        -1,
                                        -1 );
        }
        return size;
    }

    /**
     * Puts the content of this field into the given byte vector.
     * 
     * @param out where the content of this field must be put.
     */
    void put(final ByteVector out) {
        out.putShort( this.access ).putShort( this.name ).putShort( this.desc );
        int attributeCount = 0;
        if ( this.value != 0 ) {
            ++attributeCount;
        }
        if ( (this.access & Opcodes.ACC_SYNTHETIC) != 0 && (this.cw.version & 0xffff) < Opcodes.V1_5 ) {
            ++attributeCount;
        }
        if ( (this.access & Opcodes.ACC_DEPRECATED) != 0 ) {
            ++attributeCount;
        }
        if ( this.cw.version == Opcodes.V1_4 && (this.access & Opcodes.ACC_ENUM) != 0 ) {
            ++attributeCount;
        }
        if ( this.signature != 0 ) {
            ++attributeCount;
        }
        if ( this.anns != null ) {
            ++attributeCount;
        }
        if ( this.ianns != null ) {
            ++attributeCount;
        }
        if ( this.attrs != null ) {
            attributeCount += this.attrs.getCount();
        }
        out.putShort( attributeCount );
        if ( this.value != 0 ) {
            out.putShort( this.cw.newUTF8( "ConstantValue" ) );
            out.putInt( 2 ).putShort( this.value );
        }
        if ( (this.access & Opcodes.ACC_SYNTHETIC) != 0 && (this.cw.version & 0xffff) < Opcodes.V1_5 ) {
            out.putShort( this.cw.newUTF8( "Synthetic" ) ).putInt( 0 );
        }
        if ( (this.access & Opcodes.ACC_DEPRECATED) != 0 ) {
            out.putShort( this.cw.newUTF8( "Deprecated" ) ).putInt( 0 );
        }
        if ( this.cw.version == Opcodes.V1_4 && (this.access & Opcodes.ACC_ENUM) != 0 ) {
            out.putShort( this.cw.newUTF8( "Enum" ) ).putInt( 0 );
        }
        if ( this.signature != 0 ) {
            out.putShort( this.cw.newUTF8( "Signature" ) );
            out.putInt( 2 ).putShort( this.signature );
        }
        if ( this.anns != null ) {
            out.putShort( this.cw.newUTF8( "RuntimeVisibleAnnotations" ) );
            this.anns.put( out );
        }
        if ( this.ianns != null ) {
            out.putShort( this.cw.newUTF8( "RuntimeInvisibleAnnotations" ) );
            this.ianns.put( out );
        }
        if ( this.attrs != null ) {
            this.attrs.put( this.cw,
                            null,
                            0,
                            -1,
                            -1,
                            out );
        }
    }
}
