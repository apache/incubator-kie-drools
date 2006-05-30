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

import org.drools.asm.ClassAdapter;
import org.drools.asm.ClassVisitor;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;

/**
 * A {@link ClassAdapter} that merges clinit methods into a single one.
 * 
 * @author Eric Bruneton
 */
public class StaticInitMerger extends ClassAdapter {

    private String        name;

    private MethodVisitor clinit;

    private String        prefix;

    private int           counter;

    public StaticInitMerger(final String prefix,
                            final ClassVisitor cv) {
        super( cv );
        this.prefix = prefix;
    }

    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {
        this.cv.visit( version,
                       access,
                       name,
                       signature,
                       superName,
                       interfaces );
        this.name = name;
    }

    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {
        MethodVisitor mv;
        if ( name.equals( "<clinit>" ) ) {
            final int a = Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC;
            final String n = this.prefix + this.counter++;
            mv = this.cv.visitMethod( a,
                                      n,
                                      desc,
                                      signature,
                                      exceptions );

            if ( this.clinit == null ) {
                this.clinit = this.cv.visitMethod( a,
                                                   name,
                                                   desc,
                                                   null,
                                                   null );
            }
            this.clinit.visitMethodInsn( Opcodes.INVOKESTATIC,
                                         this.name,
                                         n,
                                         desc );
        } else {
            mv = this.cv.visitMethod( access,
                                      name,
                                      desc,
                                      signature,
                                      exceptions );
        }
        return mv;
    }

    public void visitEnd() {
        if ( this.clinit != null ) {
            this.clinit.visitInsn( Opcodes.RETURN );
            this.clinit.visitMaxs( 0,
                                   0 );
        }
        this.cv.visitEnd();
    }
}
