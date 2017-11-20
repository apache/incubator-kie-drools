/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.functions;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LambdaIntrospector {
    private static final LambdaIntrospector INSTANCE = new LambdaIntrospector();

    private Map<ClassIdentifier, Map<String, String>> methodFingerprintsMap = new HashMap<>();

    private LambdaIntrospector() { }

    public static LambdaIntrospector getInstance() {
        return INSTANCE;
    }

    public static String getLambdaFingerprint(Object lambda) {
        String result = LambdaIntrospector.getInstance().introspectLambda( lambda );
        return result == null ? "" : result;
    }

    private String introspectLambda(Object lambda) {
        if (lambda instanceof IntrospectableLambda) {
            lambda = (( IntrospectableLambda ) lambda).getLambda();
        }
        java.lang.invoke.SerializedLambda extracted = extractLambda( (Serializable) lambda );
        return getFingerprintsForClass( lambda, extracted ).get( extracted.getImplMethodName() );
    }

    public static java.lang.invoke.SerializedLambda extractLambda(Serializable lambda) {
        try {
            Method method = lambda.getClass().getDeclaredMethod( "writeReplace" );
            method.setAccessible( true );
            return ( java.lang.invoke.SerializedLambda ) method.invoke( lambda );
        } catch (Exception e) {
            throw new RuntimeException( e );
        }
    }

    private Map<String, String> getFingerprintsForClass( Object lambda, java.lang.invoke.SerializedLambda extracted) {
        ClassLoader lambdaClassLoader = lambda.getClass().getClassLoader();
        String className = extracted.getImplClass();
        ClassIdentifier id = new ClassIdentifier( lambdaClassLoader, className );
        Map<String, String> fingerprints = methodFingerprintsMap.get( id );

        if (fingerprints == null) {
            LambdaIntrospector.LambdaClassVisitor visitor = new LambdaIntrospector.LambdaClassVisitor(lambda);
            try (InputStream classStream = lambdaClassLoader.getResourceAsStream( className.replace( '.', '/' ) + ".class" )) {
                ClassReader reader = new ClassReader( classStream);
                reader.accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            } catch (Exception e) {
                throw new RuntimeException( e );
            }
            fingerprints = visitor.getMethodsMap();
            methodFingerprintsMap.put( id, fingerprints );
        }
        return fingerprints;
    }

    private static class ClassIdentifier {
        private final ClassLoader classLoader;
        private final String className;

        private ClassIdentifier( ClassLoader classLoader, String className ) {
            this.classLoader = classLoader;
            this.className = className;
        }

        @Override
        public boolean equals( Object o ) {
            ClassIdentifier that = ( ClassIdentifier ) o;
            return className.equals( that.className ) && classLoader == that.classLoader;
        }

        @Override
        public int hashCode() {
            return 31 * className.hashCode() + classLoader.hashCode();
        }
    }

    private static class LambdaClassVisitor extends ClassVisitor {

        private final Object lambda;

        private final Map<String, String> methodsMap = new HashMap<String, String>();

        public LambdaClassVisitor( Object lambda ) {
            super( Opcodes.ASM5 );
            this.lambda = lambda;
        }

        @Override
        public MethodVisitor visitMethod( int access, String name, String desc, String signature, String[] exceptions ) {
            return name.startsWith( "lambda$" ) ? new LambdaIntrospector.LambdaMethodVisitor(this, name) : super.visitMethod(access, name, desc, signature, exceptions);
        }

        public void setMethodFingerprint( String methodname, String methodFingerprint ) {
            methodsMap.put( methodname, methodFingerprint );
        }

        public Map<String, String> getMethodsMap() {
            return methodsMap;
        }
    }

    public static class LambdaMethodVisitor extends MethodVisitor {

        private final LambdaIntrospector.LambdaClassVisitor lambdaClassVisitor;
        private final String methodName;
        private final StringBuilder sb = new StringBuilder();

        public LambdaMethodVisitor( LambdaIntrospector.LambdaClassVisitor lambdaClassVisitor, String methodName) {
            super(Opcodes.ASM5);
            this.lambdaClassVisitor = lambdaClassVisitor;
            this.methodName = methodName;
        }

        @Override
        public void visitLdcInsn(Object cst) {
            sb.append("const ").append(cst).append("\n");
        }

        @Override
        public void visitTypeInsn(int opcode, String type) {
            sb.append(decodeASMcode(opcode)).append( " " ).append(type).append("\n");
        }

        @Override
        public void visitJumpInsn( int opcode, Label label ) {
            sb.append( "jump " ).append( decodeASMcode(opcode) ).append("\n");
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            sb.append(decodeASMcode(opcode)).append( " " ).append(var).append("\n");
        }

        @Override
        public void visitIntInsn(int opcode, int operand) {
            sb.append(decodeASMcode(opcode)).append( " " ).append(operand).append("\n");
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            sb.append(decodeASMcode(opcode)).append( " " ).append( owner ).append(".").append(name).append(desc).append("\n");
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            sb.append(decodeASMcode(opcode)).append( " " ).append( owner ).append(".").append(name).append(desc).append("\n");
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            lambdaClassVisitor.setMethodFingerprint(methodName, sb.toString());
        }
    }

    private static String decodeASMcode(int opcode) {
        switch (opcode) {
            case Opcodes.NOP: return "NOP";
            case Opcodes.ACONST_NULL: return "ACONST_NULL";
            case Opcodes.ICONST_M1: return "ICONST_M1";
            case Opcodes.ICONST_0: return "ICONST_0";
            case Opcodes.ICONST_1: return "ICONST_1";
            case Opcodes.ICONST_2: return "ICONST_2";
            case Opcodes.ICONST_3: return "ICONST_3";
            case Opcodes.ICONST_4: return "ICONST_4";
            case Opcodes.ICONST_5: return "ICONST_5";
            case Opcodes.LCONST_0: return "LCONST_0";
            case Opcodes.LCONST_1: return "LCONST_1";
            case Opcodes.FCONST_0: return "FCONST_0";
            case Opcodes.FCONST_1: return "FCONST_1";
            case Opcodes.FCONST_2: return "FCONST_2";
            case Opcodes.DCONST_0: return "DCONST_0";
            case Opcodes.DCONST_1: return "DCONST_1";
            case Opcodes.BIPUSH: return "BIPUSH";
            case Opcodes.SIPUSH: return "SIPUSH";
            case Opcodes.ILOAD: return "ILOAD";
            case Opcodes.LLOAD: return "LLOAD";
            case Opcodes.FLOAD: return "FLOAD";
            case Opcodes.DLOAD: return "DLOAD";
            case Opcodes.ALOAD: return "ALOAD";
            case Opcodes.IALOAD: return "IALOAD";
            case Opcodes.LALOAD: return "LALOAD";
            case Opcodes.FALOAD: return "FALOAD";
            case Opcodes.DALOAD: return "DALOAD";
            case Opcodes.AALOAD: return "AALOAD";
            case Opcodes.BALOAD: return "BALOAD";
            case Opcodes.CALOAD: return "CALOAD";
            case Opcodes.SALOAD: return "SALOAD";
            case Opcodes.ISTORE: return "ISTORE";
            case Opcodes.LSTORE: return "LSTORE";
            case Opcodes.FSTORE: return "FSTORE";
            case Opcodes.DSTORE: return "DSTORE";
            case Opcodes.ASTORE: return "ASTORE";
            case Opcodes.IASTORE: return "IASTORE";
            case Opcodes.LASTORE: return "LASTORE";
            case Opcodes.FASTORE: return "FASTORE";
            case Opcodes.DASTORE: return "DASTORE";
            case Opcodes.AASTORE: return "AASTORE";
            case Opcodes.BASTORE: return "BASTORE";
            case Opcodes.CASTORE: return "CASTORE";
            case Opcodes.SASTORE: return "SASTORE";
            case Opcodes.POP: return "POP";
            case Opcodes.POP2: return "POP2";
            case Opcodes.DUP: return "DUP";
            case Opcodes.DUP_X1: return "DUP_X1";
            case Opcodes.DUP_X2: return "DUP_X2";
            case Opcodes.DUP2: return "DUP2";
            case Opcodes.DUP2_X1: return "DUP2_X1";
            case Opcodes.DUP2_X2: return "DUP2_X2";
            case Opcodes.SWAP: return "SWAP";
            case Opcodes.IADD: return "IADD";
            case Opcodes.LADD: return "LADD";
            case Opcodes.FADD: return "FADD";
            case Opcodes.DADD: return "DADD";
            case Opcodes.ISUB: return "ISUB";
            case Opcodes.LSUB: return "LSUB";
            case Opcodes.FSUB: return "FSUB";
            case Opcodes.DSUB: return "DSUB";
            case Opcodes.IMUL: return "IMUL";
            case Opcodes.LMUL: return "LMUL";
            case Opcodes.FMUL: return "FMUL";
            case Opcodes.DMUL: return "DMUL";
            case Opcodes.IDIV: return "IDIV";
            case Opcodes.LDIV: return "LDIV";
            case Opcodes.FDIV: return "FDIV";
            case Opcodes.DDIV: return "DDIV";
            case Opcodes.IREM: return "IREM";
            case Opcodes.LREM: return "LREM";
            case Opcodes.FREM: return "FREM";
            case Opcodes.DREM: return "DREM";
            case Opcodes.INEG: return "INEG";
            case Opcodes.LNEG: return "LNEG";
            case Opcodes.FNEG: return "FNEG";
            case Opcodes.DNEG: return "DNEG";
            case Opcodes.ISHL: return "ISHL";
            case Opcodes.LSHL: return "LSHL";
            case Opcodes.ISHR: return "ISHR";
            case Opcodes.LSHR: return "LSHR";
            case Opcodes.IUSHR: return "IUSHR";
            case Opcodes.LUSHR: return "LUSHR";
            case Opcodes.IAND: return "IAND";
            case Opcodes.LAND: return "LAND";
            case Opcodes.IOR: return "IOR";
            case Opcodes.LOR: return "LOR";
            case Opcodes.IXOR: return "IXOR";
            case Opcodes.LXOR: return "LXOR";
            case Opcodes.IINC: return "IINC";
            case Opcodes.I2L: return "I2L";
            case Opcodes.I2F: return "I2F";
            case Opcodes.I2D: return "I2D";
            case Opcodes.L2I: return "L2I";
            case Opcodes.L2F: return "L2F";
            case Opcodes.L2D: return "L2D";
            case Opcodes.F2I: return "F2I";
            case Opcodes.F2L: return "F2L";
            case Opcodes.F2D: return "F2D";
            case Opcodes.D2I: return "D2I";
            case Opcodes.D2L: return "D2L";
            case Opcodes.D2F: return "D2F";
            case Opcodes.I2B: return "I2B";
            case Opcodes.I2C: return "I2C";
            case Opcodes.I2S: return "I2S";
            case Opcodes.LCMP: return "LCMP";
            case Opcodes.FCMPL: return "FCMPL";
            case Opcodes.FCMPG: return "FCMPG";
            case Opcodes.DCMPL: return "DCMPL";
            case Opcodes.DCMPG: return "DCMPG";
            case Opcodes.IFEQ: return "IFEQ";
            case Opcodes.IFNE: return "IFNE";
            case Opcodes.IFLT: return "IFLT";
            case Opcodes.IFGE: return "IFGE";
            case Opcodes.IFGT: return "IFGT";
            case Opcodes.IFLE: return "IFLE";
            case Opcodes.IF_ICMPEQ: return "IF_ICMPEQ";
            case Opcodes.IF_ICMPNE: return "IF_ICMPNE";
            case Opcodes.IF_ICMPLT: return "IF_ICMPLT";
            case Opcodes.IF_ICMPGE: return "IF_ICMPGE";
            case Opcodes.IF_ICMPGT: return "IF_ICMPGT";
            case Opcodes.IF_ICMPLE: return "IF_ICMPLE";
            case Opcodes.IF_ACMPEQ: return "IF_ACMPEQ";
            case Opcodes.IF_ACMPNE: return "IF_ACMPNE";
            case Opcodes.GOTO: return "GOTO";
            case Opcodes.JSR: return "JSR";
            case Opcodes.RET: return "RET";
            case Opcodes.TABLESWITCH: return "TABLESWITCH";
            case Opcodes.LOOKUPSWITCH: return "LOOKUPSWITCH";
            case Opcodes.IRETURN: return "IRETURN";
            case Opcodes.LRETURN: return "LRETURN";
            case Opcodes.FRETURN: return "FRETURN";
            case Opcodes.DRETURN: return "DRETURN";
            case Opcodes.ARETURN: return "ARETURN";
            case Opcodes.RETURN: return "RETURN";
            case Opcodes.GETSTATIC: return "GETSTATIC";
            case Opcodes.PUTSTATIC: return "PUTSTATIC";
            case Opcodes.GETFIELD: return "GETFIELD";
            case Opcodes.PUTFIELD: return "PUTFIELD";
            case Opcodes.INVOKEVIRTUAL: return "INVOKEVIRTUAL";
            case Opcodes.INVOKESPECIAL: return "INVOKESPECIAL";
            case Opcodes.INVOKESTATIC: return "INVOKESTATIC";
            case Opcodes.INVOKEINTERFACE: return "INVOKEINTERFACE";
            case Opcodes.INVOKEDYNAMIC: return "INVOKEDYNAMIC";
            case Opcodes.NEW: return "NEW";
            case Opcodes.NEWARRAY: return "NEWARRAY";
            case Opcodes.ANEWARRAY: return "ANEWARRAY";
            case Opcodes.ARRAYLENGTH: return "ARRAYLENGTH";
            case Opcodes.ATHROW: return "ATHROW";
            case Opcodes.CHECKCAST: return "CHECKCAST";
            case Opcodes.INSTANCEOF: return "INSTANCEOF";
            case Opcodes.MONITORENTER: return "MONITORENTER";
            case Opcodes.MONITOREXIT: return "MONITOREXIT";
            case Opcodes.MULTIANEWARRAY: return "MULTIANEWARRAY";
            case Opcodes.IFNULL: return "IFNULL";
            case Opcodes.IFNONNULL: return "IFNONNULL";
        }
        throw new RuntimeException( "Unknown ASM code: " + opcode );
    }

}
