/***
 * ASM XML Adapter
 * Copyright (c) 2004, Eugene Kuleshov
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
package org.drools.asm.xml;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.Type;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.AttributesImpl;

/**
 * SAXAnnotationAdapter
 * 
 * @author Eugene Kuleshov
 */
public class SAXAnnotationAdapter extends SAXAdapter
    implements
    AnnotationVisitor {
    private final String elementName;

    public SAXAnnotationAdapter(final ContentHandler h,
                                final String elementName,
                                final int visible,
                                final String name,
                                final String desc) {
        this( h,
              elementName,
              visible,
              desc,
              name,
              -1 );
    }

    public SAXAnnotationAdapter(final ContentHandler h,
                                final String elementName,
                                final int visible,
                                final int parameter,
                                final String desc) {
        this( h,
              elementName,
              visible,
              desc,
              null,
              parameter );
    }

    private SAXAnnotationAdapter(final ContentHandler h,
                                 final String elementName,
                                 final int visible,
                                 final String desc,
                                 final String name,
                                 final int parameter) {
        super( h );
        this.elementName = elementName;

        final AttributesImpl att = new AttributesImpl();
        if ( name != null ) {
            att.addAttribute( "",
                              "name",
                              "name",
                              "",
                              name );
        }
        if ( visible != 0 ) {
            att.addAttribute( "",
                              "visible",
                              "visible",
                              "",
                              visible > 0 ? "true" : "false" );
        }
        if ( parameter != -1 ) {
            att.addAttribute( "",
                              "parameter",
                              "parameter",
                              "",
                              Integer.toString( parameter ) );
        }
        if ( desc != null ) {
            att.addAttribute( "",
                              "desc",
                              "desc",
                              "",
                              desc );
        }

        addStart( elementName,
                  att );
    }

    public void visit(final String name,
                      final Object value) {
        final Class c = value.getClass();
        if ( c.isArray() ) {
            final AnnotationVisitor av = visitArray( name );
            if ( value instanceof byte[] ) {
                final byte[] b = (byte[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              new Byte( b[i] ) );
                }

            } else if ( value instanceof char[] ) {
                final char[] b = (char[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              new Character( b[i] ) );
                }

            } else if ( value instanceof boolean[] ) {
                final boolean[] b = (boolean[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              Boolean.valueOf( b[i] ) );
                }

            } else if ( value instanceof int[] ) {
                final int[] b = (int[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              new Integer( b[i] ) );
                }

            } else if ( value instanceof long[] ) {
                final long[] b = (long[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              new Long( b[i] ) );
                }

            } else if ( value instanceof float[] ) {
                final float[] b = (float[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              new Float( b[i] ) );
                }

            } else if ( value instanceof double[] ) {
                final double[] b = (double[]) value;
                for ( int i = 0; i < b.length; i++ ) {
                    av.visit( null,
                              new Double( b[i] ) );
                }

            }
            av.visitEnd();
        } else {
            addValueElement( "annotationValue",
                             name,
                             Type.getDescriptor( c ),
                             value.toString() );
        }
    }

    public void visitEnum(final String name,
                          final String desc,
                          final String value) {
        addValueElement( "annotationValueEnum",
                         name,
                         desc,
                         value );
    }

    public AnnotationVisitor visitAnnotation(final String name,
                                             final String desc) {
        return new SAXAnnotationAdapter( getContentHandler(),
                                         "annotationValueAnnotation",
                                         0,
                                         name,
                                         desc );
    }

    public AnnotationVisitor visitArray(final String name) {
        return new SAXAnnotationAdapter( getContentHandler(),
                                         "annotationValueArray",
                                         0,
                                         name,
                                         null );
    }

    public void visitEnd() {
        addEnd( this.elementName );
    }

    private void addValueElement(final String element,
                                 final String name,
                                 final String desc,
                                 final String value) {
        final AttributesImpl att = new AttributesImpl();
        if ( name != null ) {
            att.addAttribute( "",
                              "name",
                              "name",
                              "",
                              name );
        }
        if ( desc != null ) {
            att.addAttribute( "",
                              "desc",
                              "desc",
                              "",
                              desc );
        }
        if ( value != null ) {
            att.addAttribute( "",
                              "value",
                              "value",
                              "",
                              SAXClassAdapter.encode( value ) );
        }

        addElement( element,
                    att );
    }

}
