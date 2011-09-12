package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.base.*;
import org.drools.common.*;
import org.drools.core.util.asm.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.Package;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.drools.util.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class ConsequenceGenerator {
    public static void generate(final ConsequenceStub stub, KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) {
        Package pkg = workingMemory.getRuleBase().getPackage(stub.getPackageName());
        CompositeClassLoader classLoader = getCompositeClassLoader(stub); // Try to use the same ClassLoader used for the stub
        if (classLoader == null) classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();

        TypeResolver typeResolver = pkg == null ? null : pkg.getTypeResolver();
        if (typeResolver == null) {
            Set<String> imports = new HashSet<String>();
            for (String imp : stub.getPackageImports()) imports.add(imp);
            typeResolver = new ClassTypeResolver(imports, classLoader, stub.getPackageName());
        }

        final String[] declarationTypes = stub.getDeclarationTypes();
        final Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getActivation().getTuple().getLeftTupleSink()).getDeclarations();
        final LeftTuple tuple = (LeftTuple)knowledgeHelper.getTuple();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarationTypes, declarations, tuple);

        final ClassGenerator generator = new ClassGenerator(stub.getPackageName() + "." + stub.getConsequenceClassName(),
                                                            classLoader,
                                                            typeResolver)
                .setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, ConsequenceBuilder.CONSEQUENCE_SERIAL_UID)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(stub.getConsequenceClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(stub.hashCode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class),
                     new ConsequenceGenerator.GetMethodBytecodeMethod(stub)
        ).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class),
                    new ConsequenceGenerator.EqualsMethod()
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                // Tuple tuple = knowledgeHelper.getTuple();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getTuple", Tuple.class);
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 3); // LeftTuple

                // Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getActivation().getTuple().getLeftTupleSink()).getDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getActivation", Activation.class);
                invokeInterface(Activation.class, "getTuple", LeftTuple.class);
                invokeInterface(LeftTuple.class, "getLeftTupleSink", LeftTupleSink.class);
                cast(RuleTerminalNode.class);
                invokeVirtual(RuleTerminalNode.class, "getDeclarations", Declaration[].class);
                mv.visitVarInsn(ASTORE, 4);

                int offset = 6;
                int[] paramsPos = new int[declarations.length];
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex();
                    int handlePos = offset;
                    int objPos = ++offset;
                    paramsPos[i] = handlePos;

                    mv.visitVarInsn(ALOAD, 4);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    invokeVirtual(Declaration.class, "getPattern", Pattern.class);
                    invokeVirtual(Pattern.class, "getOffset", Integer.TYPE); // declarations[i].getPattern().getOffset()
                    mv.visitVarInsn(ISTORE, 5); // declarations[i].getPattern().getOffset()

                    // while (tuple.getIndex() > declaration[i].getPattern().getOffset()) tuple = tuple.getParent()
                    Label whileStart = new Label();
                    Label whileExit = new Label();
                    mv.visitLabel(whileStart);
                    mv.visitVarInsn(ALOAD, 3);
                    invokeInterface(LeftTuple.class, "getIndex", Integer.TYPE);
                    mv.visitVarInsn(ILOAD, 5); // tuple.getIndex()
                    mv.visitJumpInsn(IF_ICMPLE, whileExit); // if tuple.getIndex() <= declarations[i].getPattern().getOffset() jump to whileExit
                    mv.visitVarInsn(ALOAD, 3);
                    invokeInterface(LeftTuple.class, "getParent", LeftTuple.class);
                    mv.visitVarInsn(ASTORE, 3); // tuple = tuple.getParent()
                    mv.visitJumpInsn(GOTO, whileStart);
                    mv.visitLabel(whileExit);

                    // handle = tuple.getHandle()
                    mv.visitVarInsn(ALOAD, 3);
                    invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                    mv.visitVarInsn(ASTORE, handlePos);

                    if (stub.getNotPatterns()[i]) {
                        // declarations[i].getValue((InternalWorkingMemory)workingMemory, fact[i].getObject());
                        mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                        push(i); // i
                        mv.visitInsn(AALOAD); // declarations[i]
                        mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                        cast(InternalWorkingMemory.class);
                        mv.visitVarInsn(ALOAD, handlePos); // handle[i]
                        invokeInterface(InternalFactHandle.class, "getObject", Object.class);
                        String readMethod = declarations[i].getNativeReadMethod().getName();
                        boolean isObject = readMethod.equals("getValue");
                        String returnedType = isObject ? "Ljava/lang/Object;" : typeDescr(declarationTypes[i]);
                        mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/rule/Declaration", readMethod, "(Lorg/drools/common/InternalWorkingMemory;Ljava/lang/Object;)" + returnedType);
                        if (isObject) mv.visitTypeInsn(CHECKCAST, internalName(declarationTypes[i]));
                        offset += store(objPos, declarationTypes[i]); // obj[i]

                        // fact[i] = (org.drools.common.InternalFactHandle) knowledgeHelper.getWorkingMemory().getFactHandle(obj);
                        mv.visitVarInsn(ALOAD, 1);
                        invokeInterface(KnowledgeHelper.class, "getWorkingMemory", WorkingMemory.class);
                        loadAsObject(objPos);
                        invokeInterface(WorkingMemory.class, "getFactHandle", FactHandle.class, Object.class);
                        cast(InternalFactHandle.class);
                        mv.visitVarInsn(ASTORE, handlePos);
                    } else {
                        mv.visitVarInsn(ALOAD, handlePos); // handle[i]
                        invokeInterface(InternalFactHandle.class, "getObject", Object.class);
                        mv.visitTypeInsn(CHECKCAST, internalName(declarationTypes[i]));
                        offset += store(objPos, declarationTypes[i]); // obj[i]
                    }
                }

                // @{ruleClassName}.@{methodName}(KnowledgeHelper, @foreach{declr : declarations} Object, FactHandle @end)
                StringBuilder consequenceMethodDescr = new StringBuilder("(Lorg/drools/spi/KnowledgeHelper;");
                mv.visitVarInsn(ALOAD, 1); // KnowledgeHelper
                for (int i = 0; i < declarations.length; i++) {
                    load(paramsPos[i] + 1); // obj[i]
                    mv.visitVarInsn(ALOAD, paramsPos[i]); // handle[i]
                    consequenceMethodDescr.append(typeDescr(declarationTypes[i]) + "Lorg/drools/FactHandle;");
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                String[] globals = stub.getGlobals();
                String[] globalTypes = stub.getGlobalTypes();
                for (int i = 0; i < globals.length; i++) {
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    push(globals[i]);
                    invokeInterface(WorkingMemory.class, "getGlobal", Object.class, String.class);
                    mv.visitTypeInsn(CHECKCAST, internalName(globalTypes[i]));
                    consequenceMethodDescr.append(typeDescr(globalTypes[i]));
                }

                consequenceMethodDescr.append(")V");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), consequenceMethodDescr.toString());
                mv.visitInsn(RETURN);
            }
        });

        stub.setConsequence(generator.<Consequence>newInstance());
    }

    private static CompositeClassLoader getCompositeClassLoader(Object obj) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        while (true) {
            if (classLoader instanceof CompositeClassLoader) return (CompositeClassLoader)classLoader;
            ClassLoader parentLoader = classLoader.getParent();
            if (parentLoader == null || parentLoader == classLoader) break;
            classLoader = parentLoader;
        }
        return null;
    }

    // DeclarationMatcher

    private static List<DeclarationMatcher> matchDeclarationsToTuple(String[] declarationTypes, Declaration[] declarations, LeftTuple tuple) {
        List<DeclarationMatcher> matchers = new ArrayList<DeclarationMatcher>();
        for (int i = 0; i < declarations.length; i++)
            matchers.add(new DeclarationMatcher(i, declarations[i].getPattern().getOffset()));
        Collections.sort(matchers);
        return matchers;
    }

    private static class DeclarationMatcher implements Comparable {
        private final int originalIndex;
        private final int rootDistance;

        public DeclarationMatcher(int originalIndex, int rootDistance) {
            this.originalIndex = originalIndex;
            this.rootDistance = rootDistance;
        }

        public int getOriginalIndex() {
            return originalIndex;
        }

        public int getRootDistance() {
            return rootDistance;
        }

        public int compareTo(Object obj) {
            return ((DeclarationMatcher)obj).rootDistance - rootDistance;
        }
    }

    // Reusable ASM generated methods

    public static class GetMethodBytecodeMethod extends ClassGenerator.MethodBody {

        private ConsequenceDataProvider data;

        public GetMethodBytecodeMethod(ConsequenceDataProvider data) {
            this.data = data;
        }

        @Override
        public void body(MethodVisitor mv) {
            mv.visitVarInsn(ALOAD, 0);
            invokeVirtual(Object.class, "getClass", Class.class);
            push(data.getRuleClassName());
            push(data.getPackageName());
            push(data.getMethodName());
            push(data.getInternalRuleClassName() + ".class");
            invokeStatic(Rule.class, "getMethodBytecode", List.class, Class.class, String.class, String.class, String.class, String.class);
            mv.visitInsn(ARETURN);
        }
    }

    public static class EqualsMethod extends ClassGenerator.MethodBody {

        @Override
        public void body(MethodVisitor mv) {
            Label l1 = new Label();
            Label l2 = new Label();
            mv.visitVarInsn(ALOAD, 1); // if (object == null)
            mv.visitJumpInsn(IFNULL, l1);
            mv.visitVarInsn(ALOAD, 1);
            instanceOf(CompiledInvoker.class);
            mv.visitJumpInsn(IFNE, l2); // if (!(object instanceof  org.drools.spi.CompiledInvoker))
            mv.visitLabel(l1);
            mv.visitInsn(ICONST_0); // return false
            mv.visitInsn(IRETURN);
            mv.visitLabel(l2);
            mv.visitVarInsn(ALOAD, 0);
            invokeThis("getMethodBytecode", List.class);
            mv.visitVarInsn(ALOAD, 1);
            cast(CompiledInvoker.class);
            invokeInterface(CompiledInvoker.class, "getMethodBytecode", List.class);
            invokeStatic(MethodComparator.class, "compareBytecode", Boolean.TYPE, List.class, List.class);
            // return MethodComparator.compareBytecode(getMethodBytecode(), ((CompiledInvoker)object).getMethodBytecode());
            mv.visitInsn(IRETURN);
        }
    }

    public static abstract class EvaluateMethod extends ClassGenerator.MethodBody {
        protected int offset;

        protected int[] parseDeclarations(Declaration[] declarations, String[] declarationTypes, int declarReg, int tupleReg, int wmReg, boolean isLocal) {
            int[] declarationsParamsPos = new int[declarations.length];
            // DeclarationTypes[i] value[i] = (DeclarationTypes[i])localDeclarations[i].getValue((InternalWorkingMemory)workingMemory, object);
            for (int i = 0; i < declarations.length; i++) {
                declarationsParamsPos[i] = offset;
                mv.visitVarInsn(ALOAD, declarReg); // declarations
                push(i);
                mv.visitInsn(AALOAD);  // declarations[i]
                mv.visitVarInsn(ALOAD, wmReg); // workingMemory
                cast(InternalWorkingMemory.class);
                if (isLocal) {
                    mv.visitVarInsn(ALOAD, 1); // object
                } else {
                    // tuple.get(declarations[i])).getObject()
                    mv.visitVarInsn(ALOAD, tupleReg); // tuple
                    mv.visitVarInsn(ALOAD, declarReg);
                    push(i);
                    mv.visitInsn(AALOAD);  // declarations[i]
                    invokeInterface(Tuple.class, "get", InternalFactHandle.class, Declaration.class);
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class);
                }

                String readMethod = declarations[i].getNativeReadMethod().getName();
                boolean isObject = readMethod.equals("getValue");
                String returnedType = isObject ? "Ljava/lang/Object;" : typeDescr(declarationTypes[i]);
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/rule/Declaration", readMethod, "(Lorg/drools/common/InternalWorkingMemory;Ljava/lang/Object;)" + returnedType);
                if (isObject) mv.visitTypeInsn(CHECKCAST, internalName(declarationTypes[i]));
                offset += store(offset, declarationTypes[i]); // obj[i]
            }
            return declarationsParamsPos;
        }

        protected void parseGlobals(String[] globals, String[] globalTypes, int wmReg, StringBuilder methodDescr) {
            for (int i = 0; i < globals.length; i++) {
                mv.visitVarInsn(ALOAD, wmReg); // workingMemory
                push(globals[i]);
                invokeInterface(WorkingMemory.class, "getGlobal", Object.class, String.class);
                mv.visitTypeInsn(CHECKCAST, internalName(globalTypes[i]));
                methodDescr.append(typeDescr(globalTypes[i]));
            }
        }
    }
}
