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
 * An {@link AnnotationVisitor} that generates annotations in bytecode form.
 * 
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
final class AnnotationWriter
    implements
    AnnotationVisitor {

    /**
     * The class writer to which this annotation must be added.
     */
    private final ClassWriter cw;

    /**
     * The number of values in this annotation.
     */
    private int               size;

    /**
     * <tt>true<tt> if values are named, <tt>false</tt> otherwise. Annotation 
     * writers used for annotation default and annotation arrays use unnamed
     * values.
     */
    private final boolean     named;

    /**
     * The annotation values in bytecode form. This byte vector only contains
     * the values themselves, i.e. the number of values must be stored as a
     * unsigned short just before these bytes.
     */
    private final ByteVector  bv;

    /**
     * The byte vector to be used to store the number of values of this
     * annotation. See {@link #bv}.
     */
    private final ByteVector  parent;

    /**
     * Where the number of values of this annotation must be stored in
     * {@link #parent}.
     */
    private final int         offset;

    /**
     * Next annotation writer. This field is used to store annotation lists.
     */
    AnnotationWriter          next;

    /**
     * Previous annotation writer. This field is used to store annotation lists.
     */
    AnnotationWriter          prev;

    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Constructs a new {@link AnnotationWriter}.
     * 
     * @param cw the class writer to which this annotation must be added.
     * @param named <tt>true<tt> if values are named, <tt>false</tt> otherwise.
     * @param bv where the annotation values must be stored.
     * @param parent where the number of annotation values must be stored.
     * @param offset where in <tt>parent</tt> the number of annotation values must 
     *      be stored.
     */
    AnnotationWriter(final ClassWriter cw,
                     final boolean named,
                     final ByteVector bv,
                     final ByteVector parent,
                     final int offset) {
        this.cw = cw;
        this.named = named;
        this.bv = bv;
        this.parent = parent;
        this.offset = offset;
    }

    // ------------------------------------------------------------------------
    // Implementation of the AnnotationVisitor interface
    // ------------------------------------------------------------------------

    public void visit(final String name,
                      final Object value) {
        ++this.size;
        if ( this.named ) {
            this.bv.putShort( this.cw.newUTF8( name ) );
        }
        if ( value instanceof String ) {
            this.bv.put12( 's',
                           this.cw.newUTF8( (String) value ) );
        } else if ( value instanceof Byte ) {
            this.bv.put12( 'B',
                           this.cw.newInteger( ((Byte) value).byteValue() ).index );
        } else if ( value instanceof Boolean ) {
            final int v = ((Boolean) value).booleanValue() ? 1 : 0;
            this.bv.put12( 'Z',
                           this.cw.newInteger( v ).index );
        } else if ( value instanceof Character ) {
            this.bv.put12( 'C',
                           this.cw.newInteger( ((Character) value).charValue() ).index );
        } else if ( value instanceof Short ) {
            this.bv.put12( 'S',
                           this.cw.newInteger( ((Short) value).shortValue() ).index );
        } else if ( value instanceof Type ) {
            this.bv.put12( 'c',
                           this.cw.newUTF8( ((Type) value).getDescriptor() ) );
        } else if ( value instanceof byte[] ) {
            final byte[] v = (byte[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'B',
                               this.cw.newInteger( v[i] ).index );
            }
        } else if ( value instanceof boolean[] ) {
            final boolean[] v = (boolean[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'Z',
                               this.cw.newInteger( v[i] ? 1 : 0 ).index );
            }
        } else if ( value instanceof short[] ) {
            final short[] v = (short[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'S',
                               this.cw.newInteger( v[i] ).index );
            }
        } else if ( value instanceof char[] ) {
            final char[] v = (char[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'C',
                               this.cw.newInteger( v[i] ).index );
            }
        } else if ( value instanceof int[] ) {
            final int[] v = (int[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'I',
                               this.cw.newInteger( v[i] ).index );
            }
        } else if ( value instanceof long[] ) {
            final long[] v = (long[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'J',
                               this.cw.newLong( v[i] ).index );
            }
        } else if ( value instanceof float[] ) {
            final float[] v = (float[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'F',
                               this.cw.newFloat( v[i] ).index );
            }
        } else if ( value instanceof double[] ) {
            final double[] v = (double[]) value;
            this.bv.put12( '[',
                           v.length );
            for ( int i = 0; i < v.length; i++ ) {
                this.bv.put12( 'D',
                               this.cw.newDouble( v[i] ).index );
            }
        } else {
            final Item i = this.cw.newConstItem( value );
            this.bv.put12( ".s.IFJDCS".charAt( i.type ),
                           i.index );
        }
    }

    public void visitEnum(final String name,
                          final String desc,
                          final String value) {
        ++this.size;
        if ( this.named ) {
            this.bv.putShort( this.cw.newUTF8( name ) );
        }
        this.bv.put12( 'e',
                       this.cw.newUTF8( desc ) ).putShort( this.cw.newUTF8( value ) );
    }

    public AnnotationVisitor visitAnnotation(final String name,
                                             final String desc) {
        ++this.size;
        if ( this.named ) {
            this.bv.putShort( this.cw.newUTF8( name ) );
        }
        // write tag and type, and reserve space for values count
        this.bv.put12( '@',
                       this.cw.newUTF8( desc ) ).putShort( 0 );
        return new AnnotationWriter( this.cw,
                                     true,
                                     this.bv,
                                     this.bv,
                                     this.bv.length - 2 );
    }

    public AnnotationVisitor visitArray(final String name) {
        ++this.size;
        if ( this.named ) {
            this.bv.putShort( this.cw.newUTF8( name ) );
        }
        // write tag, and reserve space for array size
        this.bv.put12( '[',
                       0 );
        return new AnnotationWriter( this.cw,
                                     false,
                                     this.bv,
                                     this.bv,
                                     this.bv.length - 2 );
    }

    public void visitEnd() {
        if ( this.parent != null ) {
            final byte[] data = this.parent.data;
            data[this.offset] = (byte) (this.size >>> 8);
            data[this.offset + 1] = (byte) this.size;
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Returns the size of this annotation writer list.
     * 
     * @return the size of this annotation writer list.
     */
    int getSize() {
        int size = 0;
        AnnotationWriter aw = this;
        while ( aw != null ) {
            size += aw.bv.length;
            aw = aw.next;
        }
        return size;
    }

    /**
     * Puts the annotations of this annotation writer list into the given byte
     * vector.
     * 
     * @param out where the annotations must be put.
     */
    void put(final ByteVector out) {
        int n = 0;
        int size = 2;
        AnnotationWriter aw = this;
        AnnotationWriter last = null;
        while ( aw != null ) {
            ++n;
            size += aw.bv.length;
            aw.visitEnd(); // in case user forgot to call visitEnd
            aw.prev = last;
            last = aw;
            aw = aw.next;
        }
        out.putInt( size );
        out.putShort( n );
        aw = last;
        while ( aw != null ) {
            out.putByteArray( aw.bv.data,
                              0,
                              aw.bv.length );
            aw = aw.prev;
        }
    }

    /**
     * Puts the given annotation lists into the given byte vector.
     * 
     * @param panns an array of annotation writer lists.
     * @param out where the annotations must be put.
     */
    static void put(final AnnotationWriter[] panns,
                    final ByteVector out) {
        int size = 1 + 2 * panns.length;
        for ( int i = 0; i < panns.length; ++i ) {
            size += panns[i] == null ? 0 : panns[i].getSize();
        }
        out.putInt( size ).putByte( panns.length );
        for ( int i = 0; i < panns.length; ++i ) {
            AnnotationWriter aw = panns[i];
            AnnotationWriter last = null;
            int n = 0;
            while ( aw != null ) {
                ++n;
                aw.visitEnd(); // in case user forgot to call visitEnd
                aw.prev = last;
                last = aw;
                aw = aw.next;
            }
            out.putShort( n );
            aw = last;
            while ( aw != null ) {
                out.putByteArray( aw.bv.data,
                                  0,
                                  aw.bv.length );
                aw = aw.prev;
            }
        }
    }
}
