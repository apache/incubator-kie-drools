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

import org.drools.asm.Opcodes;
import org.drools.asm.signature.SignatureVisitor;

public class TraceSignatureVisitor
    implements
    SignatureVisitor {

    private StringBuffer declaration;

    private boolean      isInterface;

    private boolean      seenFormalParameter;

    private boolean      seenInterfaceBound;

    private boolean      seenParameter;

    private boolean      seenInterface;

    private StringBuffer returnType;

    private StringBuffer exceptions;

    /**
     * Stack used to keep track of class types that have arguments. Each element
     * of this stack is a boolean encoded in one bit. The top of the stack is
     * the lowest order bit. Pushing false = *2, pushing true = *2+1, popping =
     * /2.
     */
    private int          argumentStack;

    /**
     * Stack used to keep track of array class types. Each element of this stack
     * is a boolean encoded in one bit. The top of the stack is the lowest order
     * bit. Pushing false = *2, pushing true = *2+1, popping = /2.
     */
    private int          arrayStack;

    private String       separator = "";

    public TraceSignatureVisitor(final int access) {
        this.isInterface = (access & Opcodes.ACC_INTERFACE) != 0;
        this.declaration = new StringBuffer();
    }

    private TraceSignatureVisitor(final StringBuffer buf) {
        this.declaration = buf;
    }

    public void visitFormalTypeParameter(final String name) {
        this.declaration.append( this.seenFormalParameter ? ", " : "<" ).append( name );
        this.seenFormalParameter = true;
        this.seenInterfaceBound = false;
    }

    public SignatureVisitor visitClassBound() {
        this.separator = " extends ";
        startType();
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        this.separator = this.seenInterfaceBound ? ", " : " extends ";
        this.seenInterfaceBound = true;
        startType();
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        endFormals();
        this.separator = " extends ";
        startType();
        return this;
    }

    public SignatureVisitor visitInterface() {
        this.separator = this.seenInterface ? ", " : (this.isInterface ? " extends " : " implements ");
        this.seenInterface = true;
        startType();
        return this;
    }

    public SignatureVisitor visitParameterType() {
        endFormals();
        if ( !this.seenParameter ) {
            this.seenParameter = true;
            this.declaration.append( '(' );
        } else {
            this.declaration.append( ", " );
        }
        startType();
        return this;
    }

    public SignatureVisitor visitReturnType() {
        endFormals();
        if ( !this.seenParameter ) {
            this.declaration.append( '(' );
        } else {
            this.seenParameter = false;
        }
        this.declaration.append( ')' );
        this.returnType = new StringBuffer();
        return new TraceSignatureVisitor( this.returnType );
    }

    public SignatureVisitor visitExceptionType() {
        if ( this.exceptions == null ) {
            this.exceptions = new StringBuffer();
        } else {
            this.exceptions.append( ", " );
        }
        // startType();
        return new TraceSignatureVisitor( this.exceptions );
    }

    public void visitBaseType(final char descriptor) {
        switch ( descriptor ) {
            case 'V' :
                this.declaration.append( "void" );
                break;
            case 'B' :
                this.declaration.append( "byte" );
                break;
            case 'J' :
                this.declaration.append( "long" );
                break;
            case 'Z' :
                this.declaration.append( "boolean" );
                break;
            case 'I' :
                this.declaration.append( "int" );
                break;
            case 'S' :
                this.declaration.append( "short" );
                break;
            case 'C' :
                this.declaration.append( "char" );
                break;
            case 'F' :
                this.declaration.append( "float" );
                break;
            case 'D' :
                this.declaration.append( "double" );
                break;
            default :
                throw new IllegalArgumentException( "Invalid descriptor " + descriptor );
        }
        endType();
    }

    public void visitTypeVariable(final String name) {
        this.declaration.append( name );
        endType();
    }

    public SignatureVisitor visitArrayType() {
        startType();
        this.arrayStack |= 1;
        return this;
    }

    public void visitClassType(final String name) {
        if ( !"java/lang/Object".equals( name ) ) {
            this.declaration.append( this.separator ).append( name.replace( '/',
                                                                            '.' ) );
        } else {
            // Map<java.lang.Object,java.util.List>
            // or
            // abstract public V get(Object key); (seen in Dictionary.class)
            // should have Object
            // but java.lang.String extends java.lang.Object is unnecessary
            final boolean needObjectClass = this.argumentStack % 2 == 1 || this.seenParameter;
            if ( needObjectClass ) {
                this.declaration.append( this.separator ).append( name.replace( '/',
                                                                                '.' ) );
            }
        }
        this.separator = "";
        this.argumentStack *= 2;
    }

    public void visitInnerClassType(final String name) {
        // TODO tests
        this.declaration.append( this.separator ).append( name.replace( '/',
                                                                        '.' ) );
        this.separator = "";
        this.argumentStack *= 2;
    }

    public void visitTypeArgument() {
        if ( this.argumentStack % 2 == 0 ) {
            ++this.argumentStack;
            this.declaration.append( "<" );
        } else {
            this.declaration.append( ", " );
        }
        this.declaration.append( "?" );
    }

    public SignatureVisitor visitTypeArgument(final char tag) {
        if ( this.argumentStack % 2 == 0 ) {
            ++this.argumentStack;
            this.declaration.append( "<" );
        } else {
            this.declaration.append( ", " );
        }

        if ( tag == SignatureVisitor.EXTENDS ) {
            this.declaration.append( "? extends " );
        } else if ( tag == SignatureVisitor.SUPER ) {
            this.declaration.append( "? super " );
        }

        startType();
        return this;
    }

    public void visitEnd() {
        if ( this.argumentStack % 2 == 1 ) {
            this.declaration.append( ">" );
        }
        this.argumentStack /= 2;
        endType();
    }

    public String getDeclaration() {
        return this.declaration.toString();
    }

    public String getReturnType() {
        return this.returnType == null ? null : this.returnType.toString();
    }

    public String getExceptions() {
        return this.exceptions == null ? null : this.exceptions.toString();
    }

    // -----------------------------------------------

    private void endFormals() {
        if ( this.seenFormalParameter ) {
            this.declaration.append( ">" );
            this.seenFormalParameter = false;
        }
    }

    private void startType() {
        this.arrayStack *= 2;
    }

    private void endType() {
        if ( this.arrayStack % 2 == 1 ) {
            while ( this.arrayStack % 2 == 1 ) {
                this.arrayStack /= 2;
                this.declaration.append( "[]" );
            }
        } else {
            this.arrayStack /= 2;
        }
    }
}
