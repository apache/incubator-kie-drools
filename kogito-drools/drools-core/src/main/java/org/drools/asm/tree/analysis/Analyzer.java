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
package org.drools.asm.tree.analysis;

import java.util.ArrayList;
import java.util.List;

import org.drools.asm.Label;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;
import org.drools.asm.tree.AbstractInsnNode;
import org.drools.asm.tree.IincInsnNode;
import org.drools.asm.tree.JumpInsnNode;
import org.drools.asm.tree.LabelNode;
import org.drools.asm.tree.LookupSwitchInsnNode;
import org.drools.asm.tree.MethodNode;
import org.drools.asm.tree.TableSwitchInsnNode;
import org.drools.asm.tree.TryCatchBlockNode;
import org.drools.asm.tree.VarInsnNode;

/**
 * A semantic bytecode analyzer.
 * 
 * @author Eric Bruneton
 */
public class Analyzer
    implements
    Opcodes {

    private Interpreter  interpreter;

    private int          n;

    private IntMap       indexes;

    private List[]       handlers;

    private Frame[]      frames;

    private Subroutine[] subroutines;

    private boolean[]    queued;

    private int[]        queue;

    private int          top;

    private boolean      jsr;

    /**
     * Constructs a new {@link Analyzer}.
     * 
     * @param interpreter the interpreter to be used to symbolically interpret
     *        the bytecode instructions.
     */
    public Analyzer(final Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * Analyzes the given method.
     * 
     * @param owner the internal name of the class to which the method belongs.
     * @param m the method to be analyzed.
     * @return the symbolic state of the execution stack frame at each bytecode
     *         instruction of the method. The size of the returned array is
     *         equal to the number of instructions (and labels) of the method. A
     *         given frame is <tt>null</tt> if and only if the corresponding
     *         instruction cannot be reached (dead code).
     * @throws AnalyzerException if a problem occurs during the analysis.
     */
    public Frame[] analyze(final String owner,
                           final MethodNode m) throws AnalyzerException {
        this.n = m.instructions.size();
        this.indexes = new IntMap( 2 * this.n );
        this.handlers = new List[this.n];
        this.frames = new Frame[this.n];
        this.subroutines = new Subroutine[this.n];
        this.queued = new boolean[this.n];
        this.queue = new int[this.n];
        this.top = 0;

        // computes instruction indexes
        for ( int i = 0; i < this.n; ++i ) {
            Object insn = m.instructions.get( i );
            if ( insn instanceof LabelNode ) {
                insn = ((LabelNode) insn).label;
            }
            this.indexes.put( insn,
                              i );
        }

        // computes exception handlers for each instruction
        for ( int i = 0; i < m.tryCatchBlocks.size(); ++i ) {
            final TryCatchBlockNode tcb = (TryCatchBlockNode) m.tryCatchBlocks.get( i );
            final int begin = this.indexes.get( tcb.start );
            final int end = this.indexes.get( tcb.end );
            for ( int j = begin; j < end; ++j ) {
                List insnHandlers = this.handlers[j];
                if ( insnHandlers == null ) {
                    insnHandlers = new ArrayList();
                    this.handlers[j] = insnHandlers;
                }
                insnHandlers.add( tcb );
            }
        }

        // initializes the data structures for the control flow analysis
        // algorithm
        final Frame current = newFrame( m.maxLocals,
                                        m.maxStack );
        final Frame handler = newFrame( m.maxLocals,
                                        m.maxStack );
        final Type[] args = Type.getArgumentTypes( m.desc );
        int local = 0;
        if ( (m.access & Opcodes.ACC_STATIC) == 0 ) {
            final Type ctype = Type.getType( "L" + owner + ";" );
            current.setLocal( local++,
                              this.interpreter.newValue( ctype ) );
        }
        for ( int i = 0; i < args.length; ++i ) {
            current.setLocal( local++,
                              this.interpreter.newValue( args[i] ) );
            if ( args[i].getSize() == 2 ) {
                current.setLocal( local++,
                                  this.interpreter.newValue( null ) );
            }
        }
        while ( local < m.maxLocals ) {
            current.setLocal( local++,
                              this.interpreter.newValue( null ) );
        }
        merge( 0,
               current,
               null );

        // control flow analysis
        while ( this.top > 0 ) {
            final int insn = this.queue[--this.top];
            final Frame f = this.frames[insn];
            Subroutine subroutine = this.subroutines[insn];
            this.queued[insn] = false;

            try {
                final Object o = m.instructions.get( insn );
                this.jsr = false;

                if ( o instanceof LabelNode ) {
                    merge( insn + 1,
                           f,
                           subroutine );
                } else {
                    final AbstractInsnNode insnNode = (AbstractInsnNode) o;
                    final int insnOpcode = insnNode.getOpcode();

                    current.init( f ).execute( insnNode,
                                               this.interpreter );
                    subroutine = subroutine == null ? null : subroutine.copy();

                    if ( insnNode instanceof JumpInsnNode ) {
                        final JumpInsnNode j = (JumpInsnNode) insnNode;
                        if ( insnOpcode != Opcodes.GOTO && insnOpcode != Opcodes.JSR ) {
                            merge( insn + 1,
                                   current,
                                   subroutine );
                        }
                        if ( insnOpcode == Opcodes.JSR ) {
                            this.jsr = true;
                            merge( this.indexes.get( j.label ),
                                   current,
                                   new Subroutine( j.label,
                                                   m.maxLocals,
                                                   j ) );
                        } else {
                            merge( this.indexes.get( j.label ),
                                   current,
                                   subroutine );
                        }
                    } else if ( insnNode instanceof LookupSwitchInsnNode ) {
                        final LookupSwitchInsnNode lsi = (LookupSwitchInsnNode) insnNode;
                        merge( this.indexes.get( lsi.dflt ),
                               current,
                               subroutine );
                        for ( int j = 0; j < lsi.labels.size(); ++j ) {
                            final Label label = (Label) lsi.labels.get( j );
                            merge( this.indexes.get( label ),
                                   current,
                                   subroutine );
                        }
                    } else if ( insnNode instanceof TableSwitchInsnNode ) {
                        final TableSwitchInsnNode tsi = (TableSwitchInsnNode) insnNode;
                        merge( this.indexes.get( tsi.dflt ),
                               current,
                               subroutine );
                        for ( int j = 0; j < tsi.labels.size(); ++j ) {
                            final Label label = (Label) tsi.labels.get( j );
                            merge( this.indexes.get( label ),
                                   current,
                                   subroutine );
                        }
                    } else if ( insnOpcode == Opcodes.RET ) {
                        if ( subroutine == null ) {
                            throw new AnalyzerException( "RET instruction outside of a sub routine" );
                        }
                        for ( int i = 0; i < subroutine.callers.size(); ++i ) {
                            final int caller = this.indexes.get( subroutine.callers.get( i ) );
                            merge( caller + 1,
                                   this.frames[caller],
                                   current,
                                   this.subroutines[caller],
                                   subroutine.access );
                        }
                    } else if ( insnOpcode != Opcodes.ATHROW && (insnOpcode < Opcodes.IRETURN || insnOpcode > Opcodes.RETURN) ) {
                        if ( subroutine != null ) {
                            if ( insnNode instanceof VarInsnNode ) {
                                final int var = ((VarInsnNode) insnNode).var;
                                subroutine.access[var] = true;
                                if ( insnOpcode == Opcodes.LLOAD || insnOpcode == Opcodes.DLOAD || insnOpcode == Opcodes.LSTORE || insnOpcode == Opcodes.DSTORE ) {
                                    subroutine.access[var + 1] = true;
                                }
                            } else if ( insnNode instanceof IincInsnNode ) {
                                final int var = ((IincInsnNode) insnNode).var;
                                subroutine.access[var] = true;
                            }
                        }
                        merge( insn + 1,
                               current,
                               subroutine );
                    }
                }

                final List insnHandlers = this.handlers[insn];
                if ( insnHandlers != null ) {
                    for ( int i = 0; i < insnHandlers.size(); ++i ) {
                        final TryCatchBlockNode tcb = (TryCatchBlockNode) insnHandlers.get( i );
                        Type type;
                        if ( tcb.type == null ) {
                            type = Type.getType( "Ljava/lang/Throwable;" );
                        } else {
                            type = Type.getType( "L" + tcb.type + ";" );
                        }
                        handler.init( f );
                        handler.clearStack();
                        handler.push( this.interpreter.newValue( type ) );
                        merge( this.indexes.get( tcb.handler ),
                               handler,
                               subroutine );
                    }
                }
            } catch ( final AnalyzerException e ) {
                throw new AnalyzerException( "Error at instruction " + insn + ": " + e.getMessage(),
                                             e );
            } catch ( final Exception e ) {
                throw new AnalyzerException( "Error at instruction " + insn + ": " + e.getMessage(),
                                             e );
            }
        }

        return this.frames;
    }

    /**
     * Returns the symbolic stack frame for each instruction of the last
     * recently analyzed method.
     * 
     * @return the symbolic state of the execution stack frame at each bytecode
     *         instruction of the method. The size of the returned array is
     *         equal to the number of instructions (and labels) of the method. A
     *         given frame is <tt>null</tt> if the corresponding instruction
     *         cannot be reached, or if an error occured during the analysis of
     *         the method.
     */
    public Frame[] getFrames() {
        return this.frames;
    }

    /**
     * Returns the index of the given instruction.
     * 
     * @param insn a {@link Label} or {@link AbstractInsnNode} of the last
     *        recently analyzed method.
     * @return the index of the given instruction of the last recently analyzed
     *         method.
     */
    public int getIndex(final Object insn) {
        return this.indexes.get( insn );
    }

    /**
     * Returns the exception handlers for the given instruction.
     * 
     * @param insn the index of an instruction of the last recently analyzed
     *        method.
     * @return a list of {@link TryCatchBlockNode} objects.
     */
    public List getHandlers(final int insn) {
        return this.handlers[insn];
    }

    /**
     * Constructs a new frame with the given size.
     * 
     * @param nLocals the maximum number of local variables of the frame.
     * @param nStack the maximum stack size of the frame.
     * @return the created frame.
     */
    protected Frame newFrame(final int nLocals,
                             final int nStack) {
        return new Frame( nLocals,
                          nStack );
    }

    /**
     * Constructs a new frame that is identical to the given frame.
     * 
     * @param src a frame.
     * @return the created frame.
     */
    protected Frame newFrame(final Frame src) {
        return new Frame( src );
    }

    /**
     * Creates a control flow graph edge. The default implementation of this
     * method does nothing. It can be overriden in order to construct the
     * control flow graph of a method (this method is called by the
     * {@link #analyze analyze} method during its visit of the method's code).
     * 
     * @param frame the frame corresponding to an instruction.
     * @param successor the frame corresponding to a successor instruction.
     */
    protected void newControlFlowEdge(final Frame frame,
                                      final Frame successor) {
    }

    // -------------------------------------------------------------------------

    private void merge(final int insn,
                       final Frame frame,
                       final Subroutine subroutine) throws AnalyzerException {
        if ( insn > this.n - 1 ) {
            throw new AnalyzerException( "Execution can fall off end of the code" );
        }

        final Frame oldFrame = this.frames[insn];
        final Subroutine oldSubroutine = this.subroutines[insn];
        boolean changes = false;

        if ( oldFrame == null ) {
            this.frames[insn] = newFrame( frame );
            changes = true;
        } else {
            changes |= oldFrame.merge( frame,
                                       this.interpreter );
        }

        newControlFlowEdge( frame,
                            oldFrame );

        if ( oldSubroutine == null ) {
            if ( subroutine != null ) {
                this.subroutines[insn] = subroutine.copy();
                changes = true;
            }
        } else {
            if ( subroutine != null ) {
                changes |= oldSubroutine.merge( subroutine,
                                                !this.jsr );
            }
        }
        if ( changes && !this.queued[insn] ) {
            this.queued[insn] = true;
            this.queue[this.top++] = insn;
        }
    }

    private void merge(final int insn,
                       final Frame beforeJSR,
                       final Frame afterRET,
                       final Subroutine subroutineBeforeJSR,
                       final boolean[] access) throws AnalyzerException {
        if ( insn > this.n - 1 ) {
            throw new AnalyzerException( "Execution can fall off end of the code" );
        }

        final Frame oldFrame = this.frames[insn];
        final Subroutine oldSubroutine = this.subroutines[insn];
        boolean changes = false;

        afterRET.merge( beforeJSR,
                        access );

        if ( oldFrame == null ) {
            this.frames[insn] = newFrame( afterRET );
            changes = true;
        } else {
            changes |= oldFrame.merge( afterRET,
                                       access );
        }

        newControlFlowEdge( afterRET,
                            oldFrame );

        if ( oldSubroutine == null ) {
            if ( subroutineBeforeJSR != null ) {
                this.subroutines[insn] = subroutineBeforeJSR.copy();
                changes = true;
            }
        } else {
            if ( subroutineBeforeJSR != null ) {
                changes |= oldSubroutine.merge( subroutineBeforeJSR,
                                                !this.jsr );
            }
        }
        if ( changes && !this.queued[insn] ) {
            this.queued[insn] = true;
            this.queue[this.top++] = insn;
        }
    }
}
