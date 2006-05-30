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
package org.drools.asm.signature;

/**
 * A signature visitor that generates signatures in string format.
 * 
 * @author Thomas Hallgren
 * @author Eric Bruneton
 */
public class SignatureWriter
    implements
    SignatureVisitor {

    /**
     * Buffer used to construct the signature.
     */
    private final StringBuffer buf = new StringBuffer();

    /**
     * Indicates if the signature contains formal type parameters.
     */
    private boolean            hasFormals;

    /**
     * Indicates if the signature contains method parameter types.
     */
    private boolean            hasParameters;

    /**
     * Stack used to keep track of class types that have arguments. Each element
     * of this stack is a boolean encoded in one bit. The top of the stack is
     * the lowest order bit. Pushing false = *2, pushing true = *2+1, popping =
     * /2.
     */
    private int                argumentStack;

    /**
     * Constructs a new {@link SignatureWriter} object.
     */
    public SignatureWriter() {
    }

    // ------------------------------------------------------------------------
    // Implementation of the SignatureVisitor interface
    // ------------------------------------------------------------------------

    public void visitFormalTypeParameter(final String name) {
        if ( !this.hasFormals ) {
            this.hasFormals = true;
            this.buf.append( '<' );
        }
        this.buf.append( name );
        this.buf.append( ':' );
    }

    public SignatureVisitor visitClassBound() {
        return this;
    }

    public SignatureVisitor visitInterfaceBound() {
        this.buf.append( ':' );
        return this;
    }

    public SignatureVisitor visitSuperclass() {
        endFormals();
        return this;
    }

    public SignatureVisitor visitInterface() {
        return this;
    }

    public SignatureVisitor visitParameterType() {
        endFormals();
        if ( !this.hasParameters ) {
            this.hasParameters = true;
            this.buf.append( '(' );
        }
        return this;
    }

    public SignatureVisitor visitReturnType() {
        endFormals();
        if ( !this.hasParameters ) {
            this.buf.append( '(' );
        }
        this.buf.append( ')' );
        return this;
    }

    public SignatureVisitor visitExceptionType() {
        this.buf.append( '^' );
        return this;
    }

    public void visitBaseType(final char descriptor) {
        this.buf.append( descriptor );
    }

    public void visitTypeVariable(final String name) {
        this.buf.append( 'T' );
        this.buf.append( name );
        this.buf.append( ';' );
    }

    public SignatureVisitor visitArrayType() {
        this.buf.append( '[' );
        return this;
    }

    public void visitClassType(final String name) {
        this.buf.append( 'L' );
        this.buf.append( name );
        this.argumentStack *= 2;
    }

    public void visitInnerClassType(final String name) {
        endArguments();
        this.buf.append( '.' );
        this.buf.append( name );
        this.argumentStack *= 2;
    }

    public void visitTypeArgument() {
        if ( this.argumentStack % 2 == 0 ) {
            ++this.argumentStack;
            this.buf.append( '<' );
        }
        this.buf.append( '*' );
    }

    public SignatureVisitor visitTypeArgument(final char wildcard) {
        if ( this.argumentStack % 2 == 0 ) {
            ++this.argumentStack;
            this.buf.append( '<' );
        }
        if ( wildcard != '=' ) {
            this.buf.append( wildcard );
        }
        return this;
    }

    public void visitEnd() {
        endArguments();
        this.buf.append( ';' );
    }

    /**
     * Returns the signature that was built by this signature writer.
     * 
     * @return the signature that was built by this signature writer.
     */
    public String toString() {
        return this.buf.toString();
    }

    // ------------------------------------------------------------------------
    // Utility methods
    // ------------------------------------------------------------------------

    /**
     * Ends the formal type parameters section of the signature.
     */
    private void endFormals() {
        if ( this.hasFormals ) {
            this.hasFormals = false;
            this.buf.append( '>' );
        }
    }

    /**
     * Ends the type arguments of a class or inner class type.
     */
    private void endArguments() {
        if ( this.argumentStack % 2 == 1 ) {
            this.buf.append( '>' );
        }
        this.argumentStack /= 2;
    }
}