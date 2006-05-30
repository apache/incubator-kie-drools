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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.asm.AnnotationVisitor;
import org.drools.asm.ClassVisitor;
import org.drools.asm.ClassWriter;
import org.drools.asm.FieldVisitor;
import org.drools.asm.Label;
import org.drools.asm.MethodVisitor;
import org.drools.asm.Opcodes;
import org.drools.asm.Type;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A {@link org.xml.sax.ContentHandler ContentHandler} that transforms XML
 * document into Java class file. This class can be feeded by any kind of SAX
 * 2.0 event producers, e.g. XML parser, XSLT or XPath engines, or custom code.
 * 
 * @see org.drools.asm.xml.SAXClassAdapter
 * @see org.drools.asm.xml.Processor
 * 
 * @author Eugene Kuleshov
 */
public class ASMContentHandler extends DefaultHandler
    implements
    Opcodes {
    /**
     * Stack of the intermediate processing contexts.
     */
    private final List          stack = new ArrayList();

    /**
     * Complete name of the current element.
     */
    private String              match = "";

    /**
     * <tt>true</tt> if the maximum stack size and number of local variables
     * must be automatically computed.
     */
    protected boolean           computeMax;

    /**
     * Output stream to write result bytecode.
     */
    protected OutputStream      os;

    /**
     * Current instance of the {@link ClassWriter ClassWriter} used to write
     * class bytecode.
     */
    protected ClassWriter       cw;

    /**
     * Map of the active {@link Label Label} instances for current method.
     */
    protected Map               labels;

    private static final String BASE  = "class";

    private final RuleSet       RULES = new RuleSet();
    {
        this.RULES.add( ASMContentHandler.BASE,
                        new ClassRule() );
        this.RULES.add( ASMContentHandler.BASE + "/interfaces/interface",
                        new InterfaceRule() );
        this.RULES.add( ASMContentHandler.BASE + "/interfaces",
                        new InterfacesRule() );
        this.RULES.add( ASMContentHandler.BASE + "/outerclass",
                        new OuterClassRule() );
        this.RULES.add( ASMContentHandler.BASE + "/innerclass",
                        new InnerClassRule() );
        this.RULES.add( ASMContentHandler.BASE + "/source",
                        new SourceRule() );
        this.RULES.add( ASMContentHandler.BASE + "/field",
                        new FieldRule() );

        this.RULES.add( ASMContentHandler.BASE + "/method",
                        new MethodRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/exceptions/exception",
                        new ExceptionRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/exceptions",
                        new ExceptionsRule() );

        this.RULES.add( ASMContentHandler.BASE + "/method/annotationDefault",
                        new AnnotationDefaultRule() );

        this.RULES.add( ASMContentHandler.BASE + "/method/code/*",
                        new OpcodesRule() ); // opcodes

        this.RULES.add( ASMContentHandler.BASE + "/method/code/TABLESWITCH",
                        new TableSwitchRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/TABLESWITCH/label",
                        new TableSwitchLabelRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/LOOKUPSWITCH",
                        new LookupSwitchRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/LOOKUPSWITCH/label",
                        new LookupSwitchLabelRule() );

        this.RULES.add( ASMContentHandler.BASE + "/method/code/Label",
                        new LabelRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/TryCatch",
                        new TryCatchRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/LineNumber",
                        new LineNumberRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/LocalVar",
                        new LocalVarRule() );
        this.RULES.add( ASMContentHandler.BASE + "/method/code/Max",
                        new MaxRule() );

        this.RULES.add( "*/annotation",
                        new AnnotationRule() );
        this.RULES.add( "*/parameterAnnotation",
                        new AnnotationParameterRule() );
        this.RULES.add( "*/annotationValue",
                        new AnnotationValueRule() );
        this.RULES.add( "*/annotationValueAnnotation",
                        new AnnotationValueAnnotationRule() );
        this.RULES.add( "*/annotationValueEnum",
                        new AnnotationValueEnumRule() );
        this.RULES.add( "*/annotationValueArray",
                        new AnnotationValueArrayRule() );
    };

    private static interface OpcodeGroup {
        public static final int INSN                = 0;
        public static final int INSN_INT            = 1;
        public static final int INSN_VAR            = 2;
        public static final int INSN_TYPE           = 3;
        public static final int INSN_FIELD          = 4;
        public static final int INSN_METHOD         = 5;
        public static final int INSN_JUMP           = 6;
        public static final int INSN_LDC            = 7;
        public static final int INSN_IINC           = 8;
        public static final int INSN_MULTIANEWARRAY = 9;
    }

    /**
     * Map of the opcode names to opcode and opcode group
     */
    static final Map OPCODES = new HashMap();
    static {
        ASMContentHandler.OPCODES.put( "NOP",
                                       new Opcode( Opcodes.NOP,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ACONST_NULL",
                                       new Opcode( Opcodes.ACONST_NULL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_M1",
                                       new Opcode( Opcodes.ICONST_M1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_0",
                                       new Opcode( Opcodes.ICONST_0,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_1",
                                       new Opcode( Opcodes.ICONST_1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_2",
                                       new Opcode( Opcodes.ICONST_2,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_3",
                                       new Opcode( Opcodes.ICONST_3,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_4",
                                       new Opcode( Opcodes.ICONST_4,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ICONST_5",
                                       new Opcode( Opcodes.ICONST_5,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LCONST_0",
                                       new Opcode( Opcodes.LCONST_0,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LCONST_1",
                                       new Opcode( Opcodes.LCONST_1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FCONST_0",
                                       new Opcode( Opcodes.FCONST_0,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FCONST_1",
                                       new Opcode( Opcodes.FCONST_1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FCONST_2",
                                       new Opcode( Opcodes.FCONST_2,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DCONST_0",
                                       new Opcode( Opcodes.DCONST_0,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DCONST_1",
                                       new Opcode( Opcodes.DCONST_1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "BIPUSH",
                                       new Opcode( Opcodes.BIPUSH,
                                                   OpcodeGroup.INSN_INT ) );
        ASMContentHandler.OPCODES.put( "SIPUSH",
                                       new Opcode( Opcodes.SIPUSH,
                                                   OpcodeGroup.INSN_INT ) );
        ASMContentHandler.OPCODES.put( "LDC",
                                       new Opcode( Opcodes.LDC,
                                                   OpcodeGroup.INSN_LDC ) );
        ASMContentHandler.OPCODES.put( "ILOAD",
                                       new Opcode( Opcodes.ILOAD,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "LLOAD",
                                       new Opcode( Opcodes.LLOAD,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "FLOAD",
                                       new Opcode( Opcodes.FLOAD,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "DLOAD",
                                       new Opcode( Opcodes.DLOAD,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "ALOAD",
                                       new Opcode( Opcodes.ALOAD,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "IALOAD",
                                       new Opcode( Opcodes.IALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LALOAD",
                                       new Opcode( Opcodes.LALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FALOAD",
                                       new Opcode( Opcodes.FALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DALOAD",
                                       new Opcode( Opcodes.DALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "AALOAD",
                                       new Opcode( Opcodes.AALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "BALOAD",
                                       new Opcode( Opcodes.BALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "CALOAD",
                                       new Opcode( Opcodes.CALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "SALOAD",
                                       new Opcode( Opcodes.SALOAD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ISTORE",
                                       new Opcode( Opcodes.ISTORE,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "LSTORE",
                                       new Opcode( Opcodes.LSTORE,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "FSTORE",
                                       new Opcode( Opcodes.FSTORE,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "DSTORE",
                                       new Opcode( Opcodes.DSTORE,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "ASTORE",
                                       new Opcode( Opcodes.ASTORE,
                                                   OpcodeGroup.INSN_VAR ) );
        ASMContentHandler.OPCODES.put( "IASTORE",
                                       new Opcode( Opcodes.IASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LASTORE",
                                       new Opcode( Opcodes.LASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FASTORE",
                                       new Opcode( Opcodes.FASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DASTORE",
                                       new Opcode( Opcodes.DASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "AASTORE",
                                       new Opcode( Opcodes.AASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "BASTORE",
                                       new Opcode( Opcodes.BASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "CASTORE",
                                       new Opcode( Opcodes.CASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "SASTORE",
                                       new Opcode( Opcodes.SASTORE,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "POP",
                                       new Opcode( Opcodes.POP,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "POP2",
                                       new Opcode( Opcodes.POP2,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DUP",
                                       new Opcode( Opcodes.DUP,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DUP_X1",
                                       new Opcode( Opcodes.DUP_X1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DUP_X2",
                                       new Opcode( Opcodes.DUP_X2,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DUP2",
                                       new Opcode( Opcodes.DUP2,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DUP2_X1",
                                       new Opcode( Opcodes.DUP2_X1,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DUP2_X2",
                                       new Opcode( Opcodes.DUP2_X2,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "SWAP",
                                       new Opcode( Opcodes.SWAP,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IADD",
                                       new Opcode( Opcodes.IADD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LADD",
                                       new Opcode( Opcodes.LADD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FADD",
                                       new Opcode( Opcodes.FADD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DADD",
                                       new Opcode( Opcodes.DADD,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ISUB",
                                       new Opcode( Opcodes.ISUB,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LSUB",
                                       new Opcode( Opcodes.LSUB,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FSUB",
                                       new Opcode( Opcodes.FSUB,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DSUB",
                                       new Opcode( Opcodes.DSUB,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IMUL",
                                       new Opcode( Opcodes.IMUL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LMUL",
                                       new Opcode( Opcodes.LMUL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FMUL",
                                       new Opcode( Opcodes.FMUL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DMUL",
                                       new Opcode( Opcodes.DMUL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IDIV",
                                       new Opcode( Opcodes.IDIV,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LDIV",
                                       new Opcode( Opcodes.LDIV,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FDIV",
                                       new Opcode( Opcodes.FDIV,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DDIV",
                                       new Opcode( Opcodes.DDIV,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IREM",
                                       new Opcode( Opcodes.IREM,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LREM",
                                       new Opcode( Opcodes.LREM,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FREM",
                                       new Opcode( Opcodes.FREM,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DREM",
                                       new Opcode( Opcodes.DREM,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "INEG",
                                       new Opcode( Opcodes.INEG,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LNEG",
                                       new Opcode( Opcodes.LNEG,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FNEG",
                                       new Opcode( Opcodes.FNEG,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DNEG",
                                       new Opcode( Opcodes.DNEG,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ISHL",
                                       new Opcode( Opcodes.ISHL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LSHL",
                                       new Opcode( Opcodes.LSHL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ISHR",
                                       new Opcode( Opcodes.ISHR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LSHR",
                                       new Opcode( Opcodes.LSHR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IUSHR",
                                       new Opcode( Opcodes.IUSHR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LUSHR",
                                       new Opcode( Opcodes.LUSHR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IAND",
                                       new Opcode( Opcodes.IAND,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LAND",
                                       new Opcode( Opcodes.LAND,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IOR",
                                       new Opcode( Opcodes.IOR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LOR",
                                       new Opcode( Opcodes.LOR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IXOR",
                                       new Opcode( Opcodes.IXOR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LXOR",
                                       new Opcode( Opcodes.LXOR,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IINC",
                                       new Opcode( Opcodes.IINC,
                                                   OpcodeGroup.INSN_IINC ) );
        ASMContentHandler.OPCODES.put( "I2L",
                                       new Opcode( Opcodes.I2L,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "I2F",
                                       new Opcode( Opcodes.I2F,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "I2D",
                                       new Opcode( Opcodes.I2D,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "L2I",
                                       new Opcode( Opcodes.L2I,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "L2F",
                                       new Opcode( Opcodes.L2F,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "L2D",
                                       new Opcode( Opcodes.L2D,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "F2I",
                                       new Opcode( Opcodes.F2I,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "F2L",
                                       new Opcode( Opcodes.F2L,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "F2D",
                                       new Opcode( Opcodes.F2D,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "D2I",
                                       new Opcode( Opcodes.D2I,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "D2L",
                                       new Opcode( Opcodes.D2L,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "D2F",
                                       new Opcode( Opcodes.D2F,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "I2B",
                                       new Opcode( Opcodes.I2B,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "I2C",
                                       new Opcode( Opcodes.I2C,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "I2S",
                                       new Opcode( Opcodes.I2S,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LCMP",
                                       new Opcode( Opcodes.LCMP,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FCMPL",
                                       new Opcode( Opcodes.FCMPL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FCMPG",
                                       new Opcode( Opcodes.FCMPG,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DCMPL",
                                       new Opcode( Opcodes.DCMPL,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DCMPG",
                                       new Opcode( Opcodes.DCMPG,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "IFEQ",
                                       new Opcode( Opcodes.IFEQ,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IFNE",
                                       new Opcode( Opcodes.IFNE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IFLT",
                                       new Opcode( Opcodes.IFLT,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IFGE",
                                       new Opcode( Opcodes.IFGE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IFGT",
                                       new Opcode( Opcodes.IFGT,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IFLE",
                                       new Opcode( Opcodes.IFLE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ICMPEQ",
                                       new Opcode( Opcodes.IF_ICMPEQ,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ICMPNE",
                                       new Opcode( Opcodes.IF_ICMPNE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ICMPLT",
                                       new Opcode( Opcodes.IF_ICMPLT,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ICMPGE",
                                       new Opcode( Opcodes.IF_ICMPGE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ICMPGT",
                                       new Opcode( Opcodes.IF_ICMPGT,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ICMPLE",
                                       new Opcode( Opcodes.IF_ICMPLE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ACMPEQ",
                                       new Opcode( Opcodes.IF_ACMPEQ,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IF_ACMPNE",
                                       new Opcode( Opcodes.IF_ACMPNE,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "GOTO",
                                       new Opcode( Opcodes.GOTO,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "JSR",
                                       new Opcode( Opcodes.JSR,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "RET",
                                       new Opcode( Opcodes.RET,
                                                   OpcodeGroup.INSN_VAR ) );
        // OPCODES.put( "TABLESWITCH", new Opcode( TABLESWITCH,
        // "visiTableSwitchInsn"));
        // OPCODES.put( "LOOKUPSWITCH", new Opcode( LOOKUPSWITCH,
        // "visitLookupSwitch"));
        ASMContentHandler.OPCODES.put( "IRETURN",
                                       new Opcode( Opcodes.IRETURN,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "LRETURN",
                                       new Opcode( Opcodes.LRETURN,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "FRETURN",
                                       new Opcode( Opcodes.FRETURN,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "DRETURN",
                                       new Opcode( Opcodes.DRETURN,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ARETURN",
                                       new Opcode( Opcodes.ARETURN,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "RETURN",
                                       new Opcode( Opcodes.RETURN,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "GETSTATIC",
                                       new Opcode( Opcodes.GETSTATIC,
                                                   OpcodeGroup.INSN_FIELD ) );
        ASMContentHandler.OPCODES.put( "PUTSTATIC",
                                       new Opcode( Opcodes.PUTSTATIC,
                                                   OpcodeGroup.INSN_FIELD ) );
        ASMContentHandler.OPCODES.put( "GETFIELD",
                                       new Opcode( Opcodes.GETFIELD,
                                                   OpcodeGroup.INSN_FIELD ) );
        ASMContentHandler.OPCODES.put( "PUTFIELD",
                                       new Opcode( Opcodes.PUTFIELD,
                                                   OpcodeGroup.INSN_FIELD ) );
        ASMContentHandler.OPCODES.put( "INVOKEVIRTUAL",
                                       new Opcode( Opcodes.INVOKEVIRTUAL,
                                                   OpcodeGroup.INSN_METHOD ) );
        ASMContentHandler.OPCODES.put( "INVOKESPECIAL",
                                       new Opcode( Opcodes.INVOKESPECIAL,
                                                   OpcodeGroup.INSN_METHOD ) );
        ASMContentHandler.OPCODES.put( "INVOKESTATIC",
                                       new Opcode( Opcodes.INVOKESTATIC,
                                                   OpcodeGroup.INSN_METHOD ) );
        ASMContentHandler.OPCODES.put( "INVOKEINTERFACE",
                                       new Opcode( Opcodes.INVOKEINTERFACE,
                                                   OpcodeGroup.INSN_METHOD ) );
        ASMContentHandler.OPCODES.put( "NEW",
                                       new Opcode( Opcodes.NEW,
                                                   OpcodeGroup.INSN_TYPE ) );
        ASMContentHandler.OPCODES.put( "NEWARRAY",
                                       new Opcode( Opcodes.NEWARRAY,
                                                   OpcodeGroup.INSN_INT ) );
        ASMContentHandler.OPCODES.put( "ANEWARRAY",
                                       new Opcode( Opcodes.ANEWARRAY,
                                                   OpcodeGroup.INSN_TYPE ) );
        ASMContentHandler.OPCODES.put( "ARRAYLENGTH",
                                       new Opcode( Opcodes.ARRAYLENGTH,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "ATHROW",
                                       new Opcode( Opcodes.ATHROW,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "CHECKCAST",
                                       new Opcode( Opcodes.CHECKCAST,
                                                   OpcodeGroup.INSN_TYPE ) );
        ASMContentHandler.OPCODES.put( "INSTANCEOF",
                                       new Opcode( Opcodes.INSTANCEOF,
                                                   OpcodeGroup.INSN_TYPE ) );
        ASMContentHandler.OPCODES.put( "MONITORENTER",
                                       new Opcode( Opcodes.MONITORENTER,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "MONITOREXIT",
                                       new Opcode( Opcodes.MONITOREXIT,
                                                   OpcodeGroup.INSN ) );
        ASMContentHandler.OPCODES.put( "MULTIANEWARRAY",
                                       new Opcode( Opcodes.MULTIANEWARRAY,
                                                   OpcodeGroup.INSN_MULTIANEWARRAY ) );
        ASMContentHandler.OPCODES.put( "IFNULL",
                                       new Opcode( Opcodes.IFNULL,
                                                   OpcodeGroup.INSN_JUMP ) );
        ASMContentHandler.OPCODES.put( "IFNONNULL",
                                       new Opcode( Opcodes.IFNONNULL,
                                                   OpcodeGroup.INSN_JUMP ) );
    }

    /**
     * Constructs a new {@link ASMContentHandler ASMContentHandler} object.
     * 
     * @param os output stream to write generated class.
     * @param computeMax <tt>true</tt> if the maximum stack size and the
     *        maximum number of local variables must be automatically computed.
     *        This value is passed to {@link ClassWriter ClassWriter} instance.
     */
    public ASMContentHandler(final OutputStream os,
                             final boolean computeMax) {
        this.os = os;
        this.computeMax = computeMax;
    }

    /**
     * Returns the bytecode of the class that was build with underneath class
     * writer.
     * 
     * @return the bytecode of the class that was build with underneath class
     *         writer or null if there are no classwriter created.
     */
    public byte[] toByteArray() {
        return this.cw == null ? null : this.cw.toByteArray();
    }

    /**
     * Process notification of the start of an XML element being reached.
     * 
     * @param ns - The Namespace URI, or the empty string if the element has no
     *        Namespace URI or if Namespace processing is not being performed.
     * @param localName - The local name (without prefix), or the empty string
     *        if Namespace processing is not being performed.
     * @param qName - The qualified name (with prefix), or the empty string if
     *        qualified names are not available.
     * @param list - The attributes attached to the element. If there are no
     *        attributes, it shall be an empty Attributes object.
     * @exception SAXException if a parsing error is to be reported
     */
    public final void startElement(final String ns,
                                   final String localName,
                                   final String qName,
                                   final Attributes list) throws SAXException {
        // the actual element name is either in localName or qName, depending
        // on whether the parser is namespace aware
        String name = localName;
        if ( name == null || name.length() < 1 ) {
            name = qName;
        }

        // Compute the current matching rule
        final StringBuffer sb = new StringBuffer( this.match );
        if ( this.match.length() > 0 ) {
            sb.append( '/' );
        }
        sb.append( name );
        this.match = sb.toString();

        // Fire "begin" events for all relevant rules
        final Rule r = (Rule) this.RULES.match( this.match );
        if ( r != null ) {
            r.begin( name,
                     list );
        }
    }

    /**
     * Process notification of the end of an XML element being reached.
     * 
     * @param ns - The Namespace URI, or the empty string if the element has no
     *        Namespace URI or if Namespace processing is not being performed.
     * @param localName - The local name (without prefix), or the empty string
     *        if Namespace processing is not being performed.
     * @param qName - The qualified XML 1.0 name (with prefix), or the empty
     *        string if qualified names are not available.
     * 
     * @exception SAXException if a parsing error is to be reported
     */
    public final void endElement(final String ns,
                                 final String localName,
                                 final String qName) throws SAXException {
        // the actual element name is either in localName or qName, depending
        // on whether the parser is namespace aware
        String name = localName;
        if ( name == null || name.length() < 1 ) {
            name = qName;
        }

        // Fire "end" events for all relevant rules in reverse order
        final Rule r = (Rule) this.RULES.match( this.match );
        if ( r != null ) {
            r.end( name );
        }

        // Recover the previous match expression
        final int slash = this.match.lastIndexOf( '/' );
        if ( slash >= 0 ) {
            this.match = this.match.substring( 0,
                                               slash );
        } else {
            this.match = "";
        }
    }

    /**
     * Process notification of the end of a document and write generated
     * bytecode into output stream.
     * 
     * @exception SAXException if parsing or writing error is to be reported.
     */
    public final void endDocument() throws SAXException {
        try {
            this.os.write( this.cw.toByteArray() );
        } catch ( final IOException ex ) {
            throw new SAXException( ex.toString(),
                                    ex );
        }
    }

    /**
     * Return the top object on the stack without removing it. If there are no
     * objects on the stack, return <code>null</code>.
     * 
     * @return the top object on the stack without removing it.
     */
    final Object peek() {
        return this.stack.size() == 0 ? null : this.stack.get( this.stack.size() - 1 );
    }

    /**
     * Return the n'th object down the stack, where 0 is the top element and
     * [getCount()-1] is the bottom element. If the specified index is out of
     * range, return <code>null</code>.
     * 
     * @param n Index of the desired element, where 0 is the top of the stack, 1
     *        is the next element down, and so on.
     * @return the n'th object down the stack.
     */
    final Object peek(final int n) {
        return this.stack.size() < (n + 1) ? null : this.stack.get( n );
    }

    /**
     * Pop the top object off of the stack, and return it. If there are no
     * objects on the stack, return <code>null</code>.
     * 
     * @return the top object off of the stack.
     */
    final Object pop() {
        return this.stack.size() == 0 ? null : this.stack.remove( this.stack.size() - 1 );
    }

    /**
     * Push a new object onto the top of the object stack.
     * 
     * @param object The new object
     */
    final void push(final Object object) {
        this.stack.add( object );
    }

    private static final class RuleSet {
        private final Map  rules     = new HashMap();

        private final List lpatterns = new ArrayList();

        private final List rpatterns = new ArrayList();

        public void add(final String path,
                        final Object rule) {
            String pattern = path;
            if ( path.startsWith( "*/" ) ) {
                pattern = path.substring( 1 );
                this.lpatterns.add( pattern );
            } else if ( path.endsWith( "/*" ) ) {
                pattern = path.substring( 0,
                                          path.length() - 1 );
                this.rpatterns.add( pattern );
            }
            this.rules.put( pattern,
                            rule );
        }

        public Object match(final String path) {
            if ( this.rules.containsKey( path ) ) {
                return this.rules.get( path );
            }

            final int n = path.lastIndexOf( '/' );
            for ( final Iterator it = this.lpatterns.iterator(); it.hasNext(); ) {
                final String pattern = (String) it.next();
                if ( path.substring( n ).endsWith( pattern ) ) {
                    return this.rules.get( pattern );
                }
            }

            for ( final Iterator it = this.rpatterns.iterator(); it.hasNext(); ) {
                final String pattern = (String) it.next();
                if ( path.startsWith( pattern ) ) {
                    return this.rules.get( pattern );
                }
            }

            return null;
        }

    }

    /**
     * Rule
     */
    protected abstract class Rule {

        public void begin(final String name,
                          final Attributes attrs) {
        }

        public void end(final String name) {
        }

        protected final Object getValue(final String desc,
                                        final String val) {
            Object value = null;
            if ( val != null ) {
                if ( desc.equals( "Ljava/lang/String;" ) ) {
                    value = decode( val );
                } else if ( "Ljava/lang/Integer;".equals( desc ) || "I".equals( desc ) || "S".equals( desc ) || "B".equals( desc ) || "C".equals( desc ) || desc.equals( "Z" ) ) {
                    value = new Integer( val );

                } else if ( "Ljava/lang/Short;".equals( desc ) ) {
                    value = new Short( val );

                } else if ( "Ljava/lang/Byte;".equals( desc ) ) {
                    value = new Byte( val );

                } else if ( "Ljava/lang/Character;".equals( desc ) ) {
                    value = new Character( decode( val ).charAt( 0 ) );

                } else if ( "Ljava/lang/Boolean;".equals( desc ) ) {
                    value = Boolean.valueOf( val );

                    // } else if ("Ljava/lang/Integer;".equals(desc)
                    // || desc.equals("I"))
                    // {
                    // value = new Integer(val);
                    // } else if ("Ljava/lang/Character;".equals(desc)
                    // || desc.equals("C"))
                    // {
                    // value = new Character(decode(val).charAt(0));
                    // } else if ("Ljava/lang/Short;".equals(desc) ||
                    // desc.equals("S"))
                    // {
                    // value = Short.valueOf(val);
                    // } else if ("Ljava/lang/Byte;".equals(desc) ||
                    // desc.equals("B"))
                    // {
                    // value = Byte.valueOf(val);

                } else if ( "Ljava/lang/Long;".equals( desc ) || desc.equals( "J" ) ) {
                    value = new Long( val );
                } else if ( "Ljava/lang/Float;".equals( desc ) || desc.equals( "F" ) ) {
                    value = new Float( val );
                } else if ( "Ljava/lang/Double;".equals( desc ) || desc.equals( "D" ) ) {
                    value = new Double( val );
                } else if ( Type.getDescriptor( Type.class ).equals( desc ) ) {
                    value = Type.getType( val );

                    // } else if ("[I".equals(desc)) {
                    // value = new int[0]; // TODO
                    // } else if ("[C".equals(desc)) {
                    // value = new char[0]; // TODO
                    // } else if ("[Z".equals(desc)) {
                    // value = new boolean[0]; // TODO
                    // } else if ("[S".equals(desc)) {
                    // value = new short[0]; // TODO
                    // } else if ("[B".equals(desc)) {
                    // value = new byte[0]; // TODO
                    // } else if ("[J".equals(desc)) {
                    // value = new long[0]; // TODO
                    // } else if ("[F".equals(desc)) {
                    // value = new float[0]; // TODO
                    // } else if ("[D".equals(desc)) {
                    // value = new double[0]; // TODO

                } else {
                    throw new RuntimeException( "Invalid value:" + val + " desc:" + desc + " ctx:" + this );
                }
            }
            return value;
        }

        private final String decode(final String val) {
            final StringBuffer sb = new StringBuffer( val.length() );
            try {
                int n = 0;
                while ( n < val.length() ) {
                    char c = val.charAt( n );
                    if ( c == '\\' ) {
                        n++;
                        c = val.charAt( n );
                        if ( c == '\\' ) {
                            sb.append( '\\' );
                        } else {
                            n++; // skip 'u'
                            sb.append( (char) Integer.parseInt( val.substring( n,
                                                                               n + 4 ),
                                                                16 ) );
                            n += 3;
                        }
                    } else {
                        sb.append( c );
                    }
                    n++;
                }

            } catch ( final RuntimeException ex ) {
                System.err.println( val + "\n" + ex.toString() );
                ex.printStackTrace();
                throw ex;
            }
            return sb.toString();
        }

        protected final Label getLabel(final Object label) {
            Label lbl = (Label) ASMContentHandler.this.labels.get( label );
            if ( lbl == null ) {
                lbl = new Label();
                ASMContentHandler.this.labels.put( label,
                                                   lbl );
            }
            return lbl;
        }

        // TODO verify move to stack
        protected final MethodVisitor getCodeVisitor() {
            return (MethodVisitor) peek();
        }

        protected final int getAccess(final String s) {
            int access = 0;
            if ( s.indexOf( "public" ) != -1 ) {
                access |= Opcodes.ACC_PUBLIC;
            }
            if ( s.indexOf( "private" ) != -1 ) {
                access |= Opcodes.ACC_PRIVATE;
            }
            if ( s.indexOf( "protected" ) != -1 ) {
                access |= Opcodes.ACC_PROTECTED;
            }
            if ( s.indexOf( "static" ) != -1 ) {
                access |= Opcodes.ACC_STATIC;
            }
            if ( s.indexOf( "final" ) != -1 ) {
                access |= Opcodes.ACC_FINAL;
            }
            if ( s.indexOf( "super" ) != -1 ) {
                access |= Opcodes.ACC_SUPER;
            }
            if ( s.indexOf( "synchronized" ) != -1 ) {
                access |= Opcodes.ACC_SYNCHRONIZED;
            }
            if ( s.indexOf( "volatile" ) != -1 ) {
                access |= Opcodes.ACC_VOLATILE;
            }
            if ( s.indexOf( "bridge" ) != -1 ) {
                access |= Opcodes.ACC_BRIDGE;
            }
            if ( s.indexOf( "varargs" ) != -1 ) {
                access |= Opcodes.ACC_VARARGS;
            }
            if ( s.indexOf( "transient" ) != -1 ) {
                access |= Opcodes.ACC_TRANSIENT;
            }
            if ( s.indexOf( "native" ) != -1 ) {
                access |= Opcodes.ACC_NATIVE;
            }
            if ( s.indexOf( "interface" ) != -1 ) {
                access |= Opcodes.ACC_INTERFACE;
            }
            if ( s.indexOf( "abstract" ) != -1 ) {
                access |= Opcodes.ACC_ABSTRACT;
            }
            if ( s.indexOf( "strict" ) != -1 ) {
                access |= Opcodes.ACC_STRICT;
            }
            if ( s.indexOf( "synthetic" ) != -1 ) {
                access |= Opcodes.ACC_SYNTHETIC;
            }
            if ( s.indexOf( "annotation" ) != -1 ) {
                access |= Opcodes.ACC_ANNOTATION;
            }
            if ( s.indexOf( "enum" ) != -1 ) {
                access |= Opcodes.ACC_ENUM;
            }
            if ( s.indexOf( "deprecated" ) != -1 ) {
                access |= Opcodes.ACC_DEPRECATED;
            }
            return access;
        }

    }

    /**
     * ClassRule
     */
    private final class ClassRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            final int major = Integer.parseInt( attrs.getValue( "major" ) );
            final int minor = Integer.parseInt( attrs.getValue( "minor" ) );
            ASMContentHandler.this.cw = new ClassWriter( ASMContentHandler.this.computeMax );
            final Map vals = new HashMap();
            vals.put( "version",
                      new Integer( minor << 16 | major ) );
            vals.put( "access",
                      attrs.getValue( "access" ) );
            vals.put( "name",
                      attrs.getValue( "name" ) );
            vals.put( "parent",
                      attrs.getValue( "parent" ) );
            vals.put( "source",
                      attrs.getValue( "source" ) );
            vals.put( "signature",
                      attrs.getValue( "signature" ) );
            vals.put( "interfaces",
                      new ArrayList() );
            push( vals );
            // values will be extracted in InterfacesRule.end();
        }

    }

    private final class SourceRule extends Rule {

        public void begin(final String name,
                          final Attributes attrs) {
            final String file = attrs.getValue( "file" );
            final String debug = attrs.getValue( "debug" );
            ASMContentHandler.this.cw.visitSource( file,
                                                   debug );
        }

    }

    /**
     * InterfaceRule
     */
    private final class InterfaceRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            ((List) ((Map) peek()).get( "interfaces" )).add( attrs.getValue( "name" ) );
        }

    }

    /**
     * InterfacesRule
     */
    private final class InterfacesRule extends Rule {

        public final void end(final String element) {
            final Map vals = (Map) pop();
            final int version = ((Integer) vals.get( "version" )).intValue();
            final int access = getAccess( (String) vals.get( "access" ) );
            final String name = (String) vals.get( "name" );
            final String signature = (String) vals.get( "signature" );
            final String parent = (String) vals.get( "parent" );
            final List infs = (List) vals.get( "interfaces" );
            final String[] interfaces = (String[]) infs.toArray( new String[infs.size()] );
            ASMContentHandler.this.cw.visit( version,
                                             access,
                                             name,
                                             signature,
                                             parent,
                                             interfaces );
            push( ASMContentHandler.this.cw );
        }

    }

    /**
     * OuterClassRule
     */
    private final class OuterClassRule extends Rule {

        public final void begin(final String element,
                                final Attributes attrs) {
            final String owner = attrs.getValue( "owner" );
            final String name = attrs.getValue( "name" );
            final String desc = attrs.getValue( "desc" );
            ASMContentHandler.this.cw.visitOuterClass( owner,
                                                       name,
                                                       desc );
        }

    }

    /**
     * InnerClassRule
     */
    private final class InnerClassRule extends Rule {

        public final void begin(final String element,
                                final Attributes attrs) {
            final int access = getAccess( attrs.getValue( "access" ) );
            final String name = attrs.getValue( "name" );
            final String outerName = attrs.getValue( "outerName" );
            final String innerName = attrs.getValue( "innerName" );
            ASMContentHandler.this.cw.visitInnerClass( name,
                                                       outerName,
                                                       innerName,
                                                       access );
        }

    }

    /**
     * FieldRule
     */
    private final class FieldRule extends Rule {

        public final void begin(final String element,
                                final Attributes attrs) {
            final int access = getAccess( attrs.getValue( "access" ) );
            final String name = attrs.getValue( "name" );
            final String signature = attrs.getValue( "signature" );
            final String desc = attrs.getValue( "desc" );
            final Object value = getValue( desc,
                                           attrs.getValue( "value" ) );
            push( ASMContentHandler.this.cw.visitField( access,
                                                        name,
                                                        desc,
                                                        signature,
                                                        value ) );
        }

        public void end(final String name) {
            ((FieldVisitor) pop()).visitEnd();
        }

    }

    /**
     * MethodRule
     */
    private final class MethodRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            ASMContentHandler.this.labels = new HashMap();
            final Map vals = new HashMap();
            vals.put( "access",
                      attrs.getValue( "access" ) );
            vals.put( "name",
                      attrs.getValue( "name" ) );
            vals.put( "desc",
                      attrs.getValue( "desc" ) );
            vals.put( "signature",
                      attrs.getValue( "signature" ) );
            vals.put( "exceptions",
                      new ArrayList() );
            push( vals );
            // values will be extracted in ExceptionsRule.end();
        }

        public final void end(final String name) {
            ((MethodVisitor) pop()).visitEnd();
            ASMContentHandler.this.labels = null;
        }

    }

    /**
     * ExceptionRule
     */
    private final class ExceptionRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            ((List) ((Map) peek()).get( "exceptions" )).add( attrs.getValue( "name" ) );
        }

    }

    /**
     * ExceptionsRule
     */
    private final class ExceptionsRule extends Rule {

        public final void end(final String element) {
            final Map vals = (Map) pop();
            final int access = getAccess( (String) vals.get( "access" ) );
            final String name = (String) vals.get( "name" );
            final String desc = (String) vals.get( "desc" );
            final String signature = (String) vals.get( "signature" );
            final List excs = (List) vals.get( "exceptions" );
            final String[] exceptions = (String[]) excs.toArray( new String[excs.size()] );

            push( ASMContentHandler.this.cw.visitMethod( access,
                                                         name,
                                                         desc,
                                                         signature,
                                                         exceptions ) );
        }

    }

    /**
     * TableSwitchRule
     */
    private class TableSwitchRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            final Map vals = new HashMap();
            vals.put( "min",
                      attrs.getValue( "min" ) );
            vals.put( "max",
                      attrs.getValue( "max" ) );
            vals.put( "dflt",
                      attrs.getValue( "dflt" ) );
            vals.put( "labels",
                      new ArrayList() );
            push( vals );
        }

        public final void end(final String name) {
            final Map vals = (Map) pop();
            final int min = Integer.parseInt( (String) vals.get( "min" ) );
            final int max = Integer.parseInt( (String) vals.get( "max" ) );
            final Label dflt = getLabel( vals.get( "dflt" ) );
            final List lbls = (List) vals.get( "labels" );
            final Label[] labels = (Label[]) lbls.toArray( new Label[lbls.size()] );
            getCodeVisitor().visitTableSwitchInsn( min,
                                                   max,
                                                   dflt,
                                                   labels );
        }

    }

    /**
     * TableSwitchLabelRule
     */
    private final class TableSwitchLabelRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            ((List) ((Map) peek()).get( "labels" )).add( getLabel( attrs.getValue( "name" ) ) );
        }

    }

    /**
     * LookupSwitchRule
     */
    private final class LookupSwitchRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            final Map vals = new HashMap();
            vals.put( "dflt",
                      attrs.getValue( "dflt" ) );
            vals.put( "labels",
                      new ArrayList() );
            vals.put( "keys",
                      new ArrayList() );
            push( vals );
        }

        public final void end(final String name) {
            final Map vals = (Map) pop();
            final Label dflt = getLabel( vals.get( "dflt" ) );
            final List keyList = (List) vals.get( "keys" );
            final List lbls = (List) vals.get( "labels" );
            final Label[] labels = (Label[]) lbls.toArray( new Label[lbls.size()] );
            final int[] keys = new int[keyList.size()];
            for ( int i = 0; i < keys.length; i++ ) {
                keys[i] = Integer.parseInt( (String) keyList.get( i ) );
            }
            getCodeVisitor().visitLookupSwitchInsn( dflt,
                                                    keys,
                                                    labels );
        }

    }

    /**
     * LookupSwitchLabelRule
     */
    private final class LookupSwitchLabelRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            final Map vals = (Map) peek();
            ((List) vals.get( "labels" )).add( getLabel( attrs.getValue( "name" ) ) );
            ((List) vals.get( "keys" )).add( attrs.getValue( "key" ) );
        }

    }

    /**
     * LabelRule
     */
    private final class LabelRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            getCodeVisitor().visitLabel( getLabel( attrs.getValue( "name" ) ) );
        }

    }

    /**
     * TryCatchRule
     */
    private final class TryCatchRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            final Label start = getLabel( attrs.getValue( "start" ) );
            final Label end = getLabel( attrs.getValue( "end" ) );
            final Label handler = getLabel( attrs.getValue( "handler" ) );
            final String type = attrs.getValue( "type" );
            getCodeVisitor().visitTryCatchBlock( start,
                                                 end,
                                                 handler,
                                                 type );
        }

    }

    /**
     * LineNumberRule
     */
    private final class LineNumberRule extends Rule {

        public final void begin(final String name,
                                final Attributes attrs) {
            final int line = Integer.parseInt( attrs.getValue( "line" ) );
            final Label start = getLabel( attrs.getValue( "start" ) );
            getCodeVisitor().visitLineNumber( line,
                                              start );
        }

    }

    /**
     * LocalVarRule
     */
    private final class LocalVarRule extends Rule {

        public final void begin(final String element,
                                final Attributes attrs) {
            final String name = attrs.getValue( "name" );
            final String desc = attrs.getValue( "desc" );
            final String signature = attrs.getValue( "signature" );
            final Label start = getLabel( attrs.getValue( "start" ) );
            final Label end = getLabel( attrs.getValue( "end" ) );
            final int var = Integer.parseInt( attrs.getValue( "var" ) );
            getCodeVisitor().visitLocalVariable( name,
                                                 desc,
                                                 signature,
                                                 start,
                                                 end,
                                                 var );
        }

    }

    /**
     * OpcodesRule
     */
    private final class OpcodesRule extends Rule {

        // public boolean match( String match, String element) {
        // return match.startsWith( path) && OPCODES.containsKey( element);
        // }

        public final void begin(final String element,
                                final Attributes attrs) {
            final Opcode o = ((Opcode) ASMContentHandler.OPCODES.get( element ));
            if ( o == null ) {
                return;
            }

            switch ( o.type ) {
                case OpcodeGroup.INSN :
                    getCodeVisitor().visitInsn( o.opcode );
                    break;

                case OpcodeGroup.INSN_FIELD :
                    getCodeVisitor().visitFieldInsn( o.opcode,
                                                     attrs.getValue( "owner" ),
                                                     attrs.getValue( "name" ),
                                                     attrs.getValue( "desc" ) );
                    break;

                case OpcodeGroup.INSN_INT :
                    getCodeVisitor().visitIntInsn( o.opcode,
                                                   Integer.parseInt( attrs.getValue( "value" ) ) );
                    break;

                case OpcodeGroup.INSN_JUMP :
                    getCodeVisitor().visitJumpInsn( o.opcode,
                                                    getLabel( attrs.getValue( "label" ) ) );
                    break;

                case OpcodeGroup.INSN_METHOD :
                    getCodeVisitor().visitMethodInsn( o.opcode,
                                                      attrs.getValue( "owner" ),
                                                      attrs.getValue( "name" ),
                                                      attrs.getValue( "desc" ) );
                    break;

                case OpcodeGroup.INSN_TYPE :
                    getCodeVisitor().visitTypeInsn( o.opcode,
                                                    attrs.getValue( "desc" ) );
                    break;

                case OpcodeGroup.INSN_VAR :
                    getCodeVisitor().visitVarInsn( o.opcode,
                                                   Integer.parseInt( attrs.getValue( "var" ) ) );
                    break;

                case OpcodeGroup.INSN_IINC :
                    getCodeVisitor().visitIincInsn( Integer.parseInt( attrs.getValue( "var" ) ),
                                                    Integer.parseInt( attrs.getValue( "inc" ) ) );
                    break;

                case OpcodeGroup.INSN_LDC :
                    getCodeVisitor().visitLdcInsn( getValue( attrs.getValue( "desc" ),
                                                             attrs.getValue( "cst" ) ) );
                    break;

                case OpcodeGroup.INSN_MULTIANEWARRAY :
                    getCodeVisitor().visitMultiANewArrayInsn( attrs.getValue( "desc" ),
                                                              Integer.parseInt( attrs.getValue( "dims" ) ) );
                    break;

                default :
                    throw new RuntimeException( "Invalid element: " + element + " at " + ASMContentHandler.this.match );

            }
        }
    }

    /**
     * MaxRule
     */
    private final class MaxRule extends Rule {

        public final void begin(final String element,
                                final Attributes attrs) {
            final int maxStack = Integer.parseInt( attrs.getValue( "maxStack" ) );
            final int maxLocals = Integer.parseInt( attrs.getValue( "maxLocals" ) );
            getCodeVisitor().visitMaxs( maxStack,
                                        maxLocals );
        }

    }

    private final class AnnotationRule extends Rule {

        public void begin(final String name,
                          final Attributes attrs) {
            final String desc = attrs.getValue( "desc" );
            final boolean visible = Boolean.valueOf( attrs.getValue( "visible" ) ).booleanValue();

            final Object v = peek();
            if ( v instanceof ClassVisitor ) {
                push( ((ClassVisitor) v).visitAnnotation( desc,
                                                          visible ) );
            } else if ( v instanceof FieldVisitor ) {
                push( ((FieldVisitor) v).visitAnnotation( desc,
                                                          visible ) );
            } else if ( v instanceof MethodVisitor ) {
                push( ((MethodVisitor) v).visitAnnotation( desc,
                                                           visible ) );
            }
        }

        public void end(final String name) {
            ((AnnotationVisitor) pop()).visitEnd();
        }

    }

    private final class AnnotationParameterRule extends Rule {

        public void begin(final String name,
                          final Attributes attrs) {
            final int parameter = Integer.parseInt( attrs.getValue( "parameter" ) );
            final String desc = attrs.getValue( "desc" );
            final boolean visible = Boolean.valueOf( attrs.getValue( "visible" ) ).booleanValue();

            push( ((MethodVisitor) peek()).visitParameterAnnotation( parameter,
                                                                     desc,
                                                                     visible ) );
        }

        public void end(final String name) {
            ((AnnotationVisitor) pop()).visitEnd();
        }

    }

    private final class AnnotationValueRule extends Rule {

        public void begin(final String nm,
                          final Attributes attrs) {
            final String name = attrs.getValue( "name" );
            final String desc = attrs.getValue( "desc" );
            final String value = attrs.getValue( "value" );
            ((AnnotationVisitor) peek()).visit( name,
                                                getValue( desc,
                                                          value ) );
        }

    }

    private final class AnnotationValueEnumRule extends Rule {

        public void begin(final String nm,
                          final Attributes attrs) {
            final String name = attrs.getValue( "name" );
            final String desc = attrs.getValue( "desc" );
            final String value = attrs.getValue( "value" );
            ((AnnotationVisitor) peek()).visitEnum( name,
                                                    desc,
                                                    value );
        }

    }

    private final class AnnotationValueAnnotationRule extends Rule {

        public void begin(final String nm,
                          final Attributes attrs) {
            final String name = attrs.getValue( "name" );
            final String desc = attrs.getValue( "desc" );
            push( ((AnnotationVisitor) peek()).visitAnnotation( name,
                                                                desc ) );
        }

        public void end(final String name) {
            ((AnnotationVisitor) pop()).visitEnd();
        }

    }

    private final class AnnotationValueArrayRule extends Rule {

        public void begin(final String nm,
                          final Attributes attrs) {
            final String name = attrs.getValue( "name" );
            push( ((AnnotationVisitor) peek()).visitArray( name ) );
        }

        public void end(final String name) {
            ((AnnotationVisitor) pop()).visitEnd();
        }

    }

    private final class AnnotationDefaultRule extends Rule {

        public void begin(final String nm,
                          final Attributes attrs) {
            push( ((MethodVisitor) peek()).visitAnnotationDefault() );
        }

        public void end(final String name) {
            ((AnnotationVisitor) pop()).visitEnd();
        }

    }

    /**
     * Opcode
     */
    private final static class Opcode {
        public int opcode;

        public int type;

        public Opcode(final int opcode,
                      final int type) {
            this.opcode = opcode;
            this.type = type;
        }

    }

}
