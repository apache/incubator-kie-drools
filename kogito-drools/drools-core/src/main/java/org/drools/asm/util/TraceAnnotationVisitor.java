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
import org.drools.asm.Type;

/**
 * An {@link AnnotationVisitor} that prints a disassembled view of the
 * annotations it visits.
 * 
 * @author Eric Bruneton
 */
public class TraceAnnotationVisitor extends TraceAbstractVisitor
    implements
    AnnotationVisitor {

    /**
     * The {@link AnnotationVisitor} to which this visitor delegates calls. May
     * be <tt>null</tt>.
     */
    protected AnnotationVisitor av;

    private int                 valueNumber = 0;

    /**
     * Constructs a new {@link TraceAnnotationVisitor}.
     */
    public TraceAnnotationVisitor() {
        // ignore
    }

    // ------------------------------------------------------------------------
    // Implementation of the AnnotationVisitor interface
    // ------------------------------------------------------------------------

    public void visit(final String name,
                      final Object value) {
        this.buf.setLength( 0 );
        appendComa( this.valueNumber++ );

        if ( name != null ) {
            this.buf.append( name ).append( '=' );
        }

        if ( value instanceof String ) {
            visitString( (String) value );
        } else if ( value instanceof Type ) {
            visitType( (Type) value );
        } else if ( value instanceof Byte ) {
            visitByte( ((Byte) value).byteValue() );
        } else if ( value instanceof Boolean ) {
            visitBoolean( ((Boolean) value).booleanValue() );
        } else if ( value instanceof Short ) {
            visitShort( ((Short) value).shortValue() );
        } else if ( value instanceof Character ) {
            visitChar( ((Character) value).charValue() );
        } else if ( value instanceof Integer ) {
            visitInt( ((Integer) value).intValue() );
        } else if ( value instanceof Float ) {
            visitFloat( ((Float) value).floatValue() );
        } else if ( value instanceof Long ) {
            visitLong( ((Long) value).longValue() );
        } else if ( value instanceof Double ) {
            visitDouble( ((Double) value).doubleValue() );
        } else if ( value.getClass().isArray() ) {
            this.buf.append( '{' );
            if ( value instanceof byte[] ) {
                final byte[] v = (byte[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitByte( v[i] );
                }
            } else if ( value instanceof boolean[] ) {
                final boolean[] v = (boolean[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitBoolean( v[i] );
                }
            } else if ( value instanceof short[] ) {
                final short[] v = (short[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitShort( v[i] );
                }
            } else if ( value instanceof char[] ) {
                final char[] v = (char[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitChar( v[i] );
                }
            } else if ( value instanceof int[] ) {
                final int[] v = (int[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitInt( v[i] );
                }
            } else if ( value instanceof long[] ) {
                final long[] v = (long[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitLong( v[i] );
                }
            } else if ( value instanceof float[] ) {
                final float[] v = (float[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitFloat( v[i] );
                }
            } else if ( value instanceof double[] ) {
                final double[] v = (double[]) value;
                for ( int i = 0; i < v.length; i++ ) {
                    appendComa( i );
                    visitDouble( v[i] );
                }
            }
            this.buf.append( '}' );
        } else {
            this.buf.append( value );
        }

        this.text.add( this.buf.toString() );

        if ( this.av != null ) {
            this.av.visit( name,
                           value );
        }
    }

    private void visitInt(final int value) {
        this.buf.append( value );
    }

    private void visitLong(final long value) {
        this.buf.append( value ).append( 'L' );
    }

    private void visitFloat(final float value) {
        this.buf.append( value ).append( 'F' );
    }

    private void visitDouble(final double value) {
        this.buf.append( value ).append( 'D' );
    }

    private void visitChar(final char value) {
        this.buf.append( "(char)" ).append( (int) value );
    }

    private void visitShort(final short value) {
        this.buf.append( "(short)" ).append( value );
    }

    private void visitByte(final byte value) {
        this.buf.append( "(byte)" ).append( value );
    }

    private void visitBoolean(final boolean value) {
        this.buf.append( value );
    }

    private void visitString(final String value) {
        appendString( this.buf,
                      value );
    }

    private void visitType(final Type value) {
        this.buf.append( value.getClassName() ).append( ".class" );
    }

    public void visitEnum(final String name,
                          final String desc,
                          final String value) {
        this.buf.setLength( 0 );
        appendComa( this.valueNumber++ );
        if ( name != null ) {
            this.buf.append( name ).append( '=' );
        }
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( '.' ).append( value );
        this.text.add( this.buf.toString() );

        if ( this.av != null ) {
            this.av.visitEnum( name,
                               desc,
                               value );
        }
    }

    public AnnotationVisitor visitAnnotation(final String name,
                                             final String desc) {
        this.buf.setLength( 0 );
        appendComa( this.valueNumber++ );
        if ( name != null ) {
            this.buf.append( name ).append( '=' );
        }
        this.buf.append( '@' );
        appendDescriptor( TraceAbstractVisitor.FIELD_DESCRIPTOR,
                          desc );
        this.buf.append( '(' );
        this.text.add( this.buf.toString() );
        final TraceAnnotationVisitor tav = createTraceAnnotationVisitor();
        this.text.add( tav.getText() );
        this.text.add( ")" );
        if ( this.av != null ) {
            tav.av = this.av.visitAnnotation( name,
                                              desc );
        }
        return tav;
    }

    public AnnotationVisitor visitArray(final String name) {
        this.buf.setLength( 0 );
        appendComa( this.valueNumber++ );
        if ( name != null ) {
            this.buf.append( name ).append( '=' );
        }
        this.buf.append( '{' );
        this.text.add( this.buf.toString() );
        final TraceAnnotationVisitor tav = createTraceAnnotationVisitor();
        this.text.add( tav.getText() );
        this.text.add( "}" );
        if ( this.av != null ) {
            tav.av = this.av.visitArray( name );
        }
        return tav;
    }

    public void visitEnd() {
        if ( this.av != null ) {
            this.av.visitEnd();
        }
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    protected TraceAnnotationVisitor createTraceAnnotationVisitor() {
        return new TraceAnnotationVisitor();
    }

    private void appendComa(final int i) {
        if ( i != 0 ) {
            this.buf.append( ", " );
        }
    }
}
