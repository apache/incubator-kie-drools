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

import java.util.ArrayList;
import java.util.HashMap;

import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;

/**
 * A <code>MethodAdapter</code> to dispatch method body instruction
 * <p>
 * The behavior is like this:
 * <ol>
 * 
 * <li>as long as the INVOKESPECIAL for the object initialization has not been
 *     reached, every bytecode instruction is dispatched in the ctor code visitor</li>
 * 
 * <li>when this one is reached, it is only added in the ctor code visitor and
 *     a JP invoke is added</li>
 * <li>after that, only the other code visitor receives the instructions</li>
 * 
 * </ol>
 * 
 * @author Eugene Kuleshov
 * @author Eric Bruneton
 */
public abstract class AdviceAdapter extends GeneratorAdapter
    implements
    Opcodes {
    private static final Object THIS  = new Object();
    private static final Object OTHER = new Object();

    protected int               methodAccess;
    protected String            methodDesc;

    private boolean             constructor;
    private boolean             superInitialized;
    private ArrayList           stackFrame;
    private HashMap             branches;

    /**
     * Creates a new {@link AdviceAdapter}.
     * 
     * @param mv the method visitor to which this adapter delegates calls.
     * @param access the method's access flags (see {@link Opcodes}).
     * @param name the method's name.
     * @param desc the method's descriptor (see {@link Type Type}).
     */
    public AdviceAdapter(final MethodVisitor mv,
                         final int access,
                         final String name,
                         final String desc) {
        super( mv,
               access,
               name,
               desc );
        this.methodAccess = access;
        this.methodDesc = desc;

        this.constructor = "<init>".equals( name );
        if ( !this.constructor ) {
            this.superInitialized = true;
            onMethodEnter();
        } else {
            this.stackFrame = new ArrayList();
            this.branches = new HashMap();
        }
    }

    public void visitLabel(final Label label) {
        this.mv.visitLabel( label );

        if ( this.constructor && this.branches != null ) {
            final ArrayList frame = (ArrayList) this.branches.get( label );
            if ( frame != null ) {
                this.stackFrame = frame;
                this.branches.remove( label );
            }
        }
    }

    public void visitInsn(final int opcode) {
        if ( this.constructor ) {
            switch ( opcode ) {
                case RETURN : // empty stack
                    onMethodExit( opcode );
                    break;

                case IRETURN : // 1 before n/a after
                case FRETURN : // 1 before n/a after
                case ARETURN : // 1 before n/a after
                case ATHROW : // 1 before n/a after
                    popValue();
                    popValue();
                    onMethodExit( opcode );
                    break;

                case LRETURN : // 2 before n/a after
                case DRETURN : // 2 before n/a after
                    popValue();
                    popValue();
                    onMethodExit( opcode );
                    break;

                case NOP :
                case LALOAD : // remove 2 add 2
                case DALOAD : // remove 2 add 2
                case LNEG :
                case DNEG :
                case FNEG :
                case INEG :
                case L2D :
                case D2L :
                case F2I :
                case I2B :
                case I2C :
                case I2S :
                case I2F :
                case Opcodes.ARRAYLENGTH :
                    break;

                case ACONST_NULL :
                case ICONST_M1 :
                case ICONST_0 :
                case ICONST_1 :
                case ICONST_2 :
                case ICONST_3 :
                case ICONST_4 :
                case ICONST_5 :
                case FCONST_0 :
                case FCONST_1 :
                case FCONST_2 :
                case F2L : // 1 before 2 after
                case F2D :
                case I2L :
                case I2D :
                    pushValue( AdviceAdapter.OTHER );
                    break;

                case LCONST_0 :
                case LCONST_1 :
                case DCONST_0 :
                case DCONST_1 :
                    pushValue( AdviceAdapter.OTHER );
                    pushValue( AdviceAdapter.OTHER );
                    break;

                case IALOAD : // remove 2 add 1
                case FALOAD : // remove 2 add 1
                case AALOAD : // remove 2 add 1
                case BALOAD : // remove 2 add 1
                case CALOAD : // remove 2 add 1
                case SALOAD : // remove 2 add 1
                case POP :
                case IADD :
                case FADD :
                case ISUB :
                case LSHL : // 3 before 2 after
                case LSHR : // 3 before 2 after
                case LUSHR : // 3 before 2 after
                case L2I : // 2 before 1 after
                case L2F : // 2 before 1 after
                case D2I : // 2 before 1 after
                case D2F : // 2 before 1 after
                case FSUB :
                case FMUL :
                case FDIV :
                case FREM :
                case FCMPL : // 2 before 1 after
                case FCMPG : // 2 before 1 after
                case IMUL :
                case IDIV :
                case IREM :
                case ISHL :
                case ISHR :
                case IUSHR :
                case IAND :
                case IOR :
                case IXOR :
                case MONITORENTER :
                case MONITOREXIT :
                    popValue();
                    break;

                case POP2 :
                case LSUB :
                case LMUL :
                case LDIV :
                case LREM :
                case LADD :
                case LAND :
                case LOR :
                case LXOR :
                case DADD :
                case DMUL :
                case DSUB :
                case DDIV :
                case DREM :
                    popValue();
                    popValue();
                    break;

                case IASTORE :
                case FASTORE :
                case AASTORE :
                case BASTORE :
                case CASTORE :
                case SASTORE :
                case LCMP : // 4 before 1 after
                case DCMPL :
                case DCMPG :
                    popValue();
                    popValue();
                    popValue();
                    break;

                case LASTORE :
                case DASTORE :
                    popValue();
                    popValue();
                    popValue();
                    popValue();
                    break;

                case DUP :
                    pushValue( peekValue() );
                    break;

                case DUP_X1 :
                    // TODO optimize this
                {
                    final Object o1 = popValue();
                    final Object o2 = popValue();
                    pushValue( o1 );
                    pushValue( o2 );
                    pushValue( o1 );
                }
                    break;

                case DUP_X2 :
                    // TODO optimize this
                {
                    final Object o1 = popValue();
                    final Object o2 = popValue();
                    final Object o3 = popValue();
                    pushValue( o1 );
                    pushValue( o3 );
                    pushValue( o2 );
                    pushValue( o1 );
                }
                    break;

                case DUP2 :
                    // TODO optimize this
                {
                    final Object o1 = popValue();
                    final Object o2 = popValue();
                    pushValue( o2 );
                    pushValue( o1 );
                    pushValue( o2 );
                    pushValue( o1 );
                }
                    break;

                case DUP2_X1 :
                    // TODO optimize this
                {
                    final Object o1 = popValue();
                    final Object o2 = popValue();
                    final Object o3 = popValue();
                    pushValue( o2 );
                    pushValue( o1 );
                    pushValue( o3 );
                    pushValue( o2 );
                    pushValue( o1 );
                }
                    break;

                case DUP2_X2 :
                    // TODO optimize this
                {
                    final Object o1 = popValue();
                    final Object o2 = popValue();
                    final Object o3 = popValue();
                    final Object o4 = popValue();
                    pushValue( o2 );
                    pushValue( o1 );
                    pushValue( o4 );
                    pushValue( o3 );
                    pushValue( o2 );
                    pushValue( o1 );
                }
                    break;

                case SWAP : {
                    final Object o1 = popValue();
                    final Object o2 = popValue();
                    pushValue( o1 );
                    pushValue( o2 );
                }
                    break;
            }
        } else {
            switch ( opcode ) {
                case RETURN :
                case IRETURN :
                case FRETURN :
                case ARETURN :
                case LRETURN :
                case DRETURN :
                case ATHROW :
                    onMethodExit( opcode );
                    break;
            }
        }
        this.mv.visitInsn( opcode );
    }

    public void visitVarInsn(final int opcode,
                             final int var) {
        super.visitVarInsn( opcode,
                            var );

        if ( this.constructor ) {
            switch ( opcode ) {
                case ILOAD :
                case FLOAD :
                    pushValue( AdviceAdapter.OTHER );
                    break;
                case LLOAD :
                case DLOAD :
                    pushValue( AdviceAdapter.OTHER );
                    pushValue( AdviceAdapter.OTHER );
                    break;
                case ALOAD :
                    pushValue( var == 0 ? AdviceAdapter.THIS : AdviceAdapter.OTHER );
                    break;
                case ASTORE :
                case ISTORE :
                case FSTORE :
                    popValue();
                    break;
                case LSTORE :
                case DSTORE :
                    popValue();
                    popValue();
                    break;
            }
        }
    }

    public void visitFieldInsn(final int opcode,
                               final String owner,
                               final String name,
                               final String desc) {
        this.mv.visitFieldInsn( opcode,
                                owner,
                                name,
                                desc );

        if ( this.constructor ) {
            final char c = desc.charAt( 0 );
            final boolean longOrDouble = c == 'J' || c == 'D';
            switch ( opcode ) {
                case GETSTATIC :
                    pushValue( AdviceAdapter.OTHER );
                    if ( longOrDouble ) {
                        pushValue( AdviceAdapter.OTHER );
                    }
                    break;
                case PUTSTATIC :
                    popValue();
                    if ( longOrDouble ) {
                        popValue();
                    }
                    break;
                case PUTFIELD :
                    popValue();
                    if ( longOrDouble ) {
                        popValue();
                        popValue();
                    }
                    break;
                // case GETFIELD:
                default :
                    if ( longOrDouble ) {
                        pushValue( AdviceAdapter.OTHER );
                    }
            }
        }
    }

    public void visitIntInsn(final int opcode,
                             final int operand) {
        this.mv.visitIntInsn( opcode,
                              operand );

        if ( this.constructor ) {
            switch ( opcode ) {
                case BIPUSH :
                case SIPUSH :
                    pushValue( AdviceAdapter.OTHER );
            }
        }
    }

    public void visitLdcInsn(final Object cst) {
        this.mv.visitLdcInsn( cst );

        if ( this.constructor ) {
            pushValue( AdviceAdapter.OTHER );
            if ( cst instanceof Double || cst instanceof Long ) {
                pushValue( AdviceAdapter.OTHER );
            }
        }
    }

    public void visitMultiANewArrayInsn(final String desc,
                                        final int dims) {
        this.mv.visitMultiANewArrayInsn( desc,
                                         dims );

        if ( this.constructor ) {
            for ( int i = 0; i < dims; i++ ) {
                popValue();
            }
            pushValue( AdviceAdapter.OTHER );
        }
    }

    public void visitTypeInsn(final int opcode,
                              final String name) {
        this.mv.visitTypeInsn( opcode,
                               name );

        // ANEWARRAY, CHECKCAST or INSTANCEOF don't change stack
        if ( this.constructor && opcode == Opcodes.NEW ) {
            pushValue( AdviceAdapter.OTHER );
        }
    }

    public void visitMethodInsn(final int opcode,
                                final String owner,
                                final String name,
                                final String desc) {
        this.mv.visitMethodInsn( opcode,
                                 owner,
                                 name,
                                 desc );

        if ( this.constructor ) {
            final Type[] types = Type.getArgumentTypes( desc );
            for ( int i = 0; i < types.length; i++ ) {
                popValue();
                if ( types[i].getSize() == 2 ) {
                    popValue();
                }
            }
            switch ( opcode ) {
                // case INVOKESTATIC:
                // break;

                case INVOKEINTERFACE :
                case INVOKEVIRTUAL :
                    popValue(); // objectref
                    break;

                case INVOKESPECIAL :
                    final Object type = popValue(); // objectref
                    if ( type == AdviceAdapter.THIS && !this.superInitialized ) {
                        onMethodEnter();
                        this.superInitialized = true;
                        // once super has been initialized it is no longer 
                        // necessary to keep track of stack state                        
                        this.constructor = false;
                    }
                    break;
            }

            final Type returnType = Type.getReturnType( desc );
            if ( returnType != Type.VOID_TYPE ) {
                pushValue( AdviceAdapter.OTHER );
                if ( returnType.getSize() == 2 ) {
                    pushValue( AdviceAdapter.OTHER );
                }
            }
        }
    }

    public void visitJumpInsn(final int opcode,
                              final Label label) {
        this.mv.visitJumpInsn( opcode,
                               label );

        if ( this.constructor ) {
            switch ( opcode ) {
                case IFEQ :
                case IFNE :
                case IFLT :
                case IFGE :
                case IFGT :
                case IFLE :
                case IFNULL :
                case IFNONNULL :
                    popValue();
                    break;

                case IF_ICMPEQ :
                case IF_ICMPNE :
                case IF_ICMPLT :
                case IF_ICMPGE :
                case IF_ICMPGT :
                case IF_ICMPLE :
                case IF_ACMPEQ :
                case IF_ACMPNE :
                    popValue();
                    popValue();
                    break;

                case JSR :
                    pushValue( AdviceAdapter.OTHER );
                    break;
            }
            addBranch( label );
        }
    }

    public void visitLookupSwitchInsn(final Label dflt,
                                      final int[] keys,
                                      final Label[] labels) {
        this.mv.visitLookupSwitchInsn( dflt,
                                       keys,
                                       labels );

        if ( this.constructor ) {
            popValue();
            addBranches( dflt,
                         labels );
        }
    }

    public void visitTableSwitchInsn(final int min,
                                     final int max,
                                     final Label dflt,
                                     final Label[] labels) {
        this.mv.visitTableSwitchInsn( min,
                                      max,
                                      dflt,
                                      labels );

        if ( this.constructor ) {
            popValue();
            addBranches( dflt,
                         labels );
        }
    }

    private void addBranches(final Label dflt,
                             final Label[] labels) {
        addBranch( dflt );
        for ( int i = 0; i < labels.length; i++ ) {
            addBranch( labels[i] );
        }
    }

    private void addBranch(final Label label) {
        if ( this.branches.containsKey( label ) ) {
            return;
        }
        final ArrayList frame = new ArrayList();
        frame.addAll( this.stackFrame );
        this.branches.put( label,
                           frame );
    }

    private Object popValue() {
        return this.stackFrame.remove( this.stackFrame.size() - 1 );
    }

    private Object peekValue() {
        return this.stackFrame.get( this.stackFrame.size() - 1 );
    }

    private void pushValue(final Object o) {
        this.stackFrame.add( o );
    }

    /**
     * Called at the beginning of the method or after super 
     * class class call in the constructor.
     * <br><br>
     * 
     * <i>Custom code can use or change all the local variables,
     * but should not change state of the stack.</i>
     */
    protected abstract void onMethodEnter();

    /**
     * Called before explicit exit from the method using either
     * return or throw. Top element on the stack contains the 
     * return value or exception instance. For example:
     * 
     * <pre>
     *   public void onMethodExit(int opcode) {
     *     if(opcode==RETURN) {
     *         visitInsn(ACONST_NULL);
     *     } else if(opcode==ARETURN || opcode==ATHROW) {
     *         dup();
     *     } else {
     *         if(opcode==LRETURN || opcode==DRETURN) {
     *             dup2();
     *         } else {
     *             dup();
     *         }
     *         box(Type.getReturnType(this.methodDesc));
     *     }
     *     visitIntInsn(SIPUSH, opcode);
     *     visitMethodInsn(INVOKESTATIC, owner, "onExit", "(Ljava/lang/Object;I)V");
     *   }
     *
     *   // an actual call back method
     *   public static void onExit(int opcode, Object param) {
     *     ...
     * </pre>
     * 
     * <br><br>
     * 
     * <i>Custom code can use or change all the local variables,
     * but should not change state of the stack.</i>
     * 
     * @param opcode one of the RETURN, IRETURN, FRETURN, 
     *   ARETURN, LRETURN, DRETURN or ATHROW
     * 
     */
    protected abstract void onMethodExit(int opcode);

    // TODO onException, onMethodCall

}
