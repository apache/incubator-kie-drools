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
package org.drools.asm.commons;

import java.util.HashMap;
import java.util.Map;

import org.drools.asm.Type;

/**
 * A named method descriptor.
 * 
 * @author Juozas Baliuka
 * @author Chris Nokleberg
 * @author Eric Bruneton
 */
public class Method {

    /**
     * The method name.
     */
    private final String     name;

    /**
     * The method descriptor.
     */
    private final String     desc;

    /**
     * Maps primitive Java type names to their descriptors.
     */
    private final static Map DESCRIPTORS;

    static {
        DESCRIPTORS = new HashMap();
        Method.DESCRIPTORS.put( "void",
                                "V" );
        Method.DESCRIPTORS.put( "byte",
                                "B" );
        Method.DESCRIPTORS.put( "char",
                                "C" );
        Method.DESCRIPTORS.put( "double",
                                "D" );
        Method.DESCRIPTORS.put( "float",
                                "F" );
        Method.DESCRIPTORS.put( "int",
                                "I" );
        Method.DESCRIPTORS.put( "long",
                                "J" );
        Method.DESCRIPTORS.put( "short",
                                "S" );
        Method.DESCRIPTORS.put( "boolean",
                                "Z" );
    }

    /**
     * Creates a new {@link Method}.
     * 
     * @param name the method's name.
     * @param desc the method's descriptor.
     */
    public Method(final String name,
                  final String desc) {
        this.name = name;
        this.desc = desc;
    }

    /**
     * Creates a new {@link Method}.
     * 
     * @param name the method's name.
     * @param returnType the method's return type.
     * @param argumentTypes the method's argument types.
     */
    public Method(final String name,
                  final Type returnType,
                  final Type[] argumentTypes) {
        this( name,
              Type.getMethodDescriptor( returnType,
                                        argumentTypes ) );
    }

    /**
     * Returns a {@link Method} corresponding to the given Java method
     * declaration.
     * 
     * @param method a Java method declaration, without argument names, of the
     *        form "returnType name (argumentType1, ... argumentTypeN)", where
     *        the types are in plain Java (e.g. "int", "float",
     *        "java.util.List", ...).
     * @return a {@link Method} corresponding to the given Java method
     *         declaration.
     * @throws IllegalArgumentException if <code>method</code> could not get
     *         parsed.
     */
    public static Method getMethod(final String method) throws IllegalArgumentException {
        final int space = method.indexOf( ' ' );
        int start = method.indexOf( '(',
                                    space ) + 1;
        final int end = method.indexOf( ')',
                                        start );
        if ( space == -1 || start == -1 || end == -1 ) {
            throw new IllegalArgumentException();
        }
        // TODO: Check validity of returnType, methodName and arguments.
        final String returnType = method.substring( 0,
                                                    space );
        final String methodName = method.substring( space + 1,
                                                    start - 1 ).trim();
        final StringBuffer sb = new StringBuffer();
        sb.append( '(' );
        int p;
        do {
            p = method.indexOf( ',',
                                start );
            if ( p == -1 ) {
                sb.append( map( method.substring( start,
                                                  end ).trim() ) );
            } else {
                sb.append( map( method.substring( start,
                                                  p ).trim() ) );
                start = p + 1;
            }
        } while ( p != -1 );
        sb.append( ')' );
        sb.append( map( returnType ) );
        return new Method( methodName,
                           sb.toString() );
    }

    private static String map(final String type) {
        if ( type.equals( "" ) ) {
            return type;
        }

        final StringBuffer sb = new StringBuffer();
        int index = 0;
        while ( (index = type.indexOf( "[]",
                                       index ) + 1) > 0 ) {
            sb.append( '[' );
        }

        final String t = type.substring( 0,
                                         type.length() - sb.length() * 2 );
        final String desc = (String) Method.DESCRIPTORS.get( t );
        if ( desc != null ) {
            sb.append( desc );
        } else {
            sb.append( 'L' );
            if ( t.indexOf( '.' ) < 0 ) {
                sb.append( "java/lang/" + t );
            } else {
                sb.append( t.replace( '.',
                                      '/' ) );
            }
            sb.append( ';' );
        }
        return sb.toString();
    }

    /**
     * Returns the name of the method described by this object.
     * 
     * @return the name of the method described by this object.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the descriptor of the method described by this object.
     * 
     * @return the descriptor of the method described by this object.
     */
    public String getDescriptor() {
        return this.desc;
    }

    /**
     * Returns the return type of the method described by this object.
     * 
     * @return the return type of the method described by this object.
     */
    public Type getReturnType() {
        return Type.getReturnType( this.desc );
    }

    /**
     * Returns the argument types of the method described by this object.
     * 
     * @return the argument types of the method described by this object.
     */
    public Type[] getArgumentTypes() {
        return Type.getArgumentTypes( this.desc );
    }

    public String toString() {
        return this.name + this.desc;
    }

    public boolean equals(final Object o) {
        if ( !(o instanceof Method) ) {
            return false;
        }
        final Method other = (Method) o;
        return this.name.equals( other.name ) && this.desc.equals( other.desc );
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.desc.hashCode();
    }
}