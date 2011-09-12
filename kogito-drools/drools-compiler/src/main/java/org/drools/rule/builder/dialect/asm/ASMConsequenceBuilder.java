package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.common.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class ASMConsequenceBuilder extends AbstractASMConsequenceBuilder {

    protected byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext) {
        final ConsequenceDataProvider data = new ConsequenceContext(consequenceContext);
        final String invokerClassName = (String)consequenceContext.get("invokerClassName");
        final String name = (String)consequenceContext.get("consequenceName");
        final Declaration[] declarations = (Declaration[])consequenceContext.get("declarations");

        final ClassGenerator generator = new ClassGenerator(data.getPackageName() + "." + invokerClassName,
                                                            ruleContext.getPackageBuilder().getRootClassLoader(),
                                                            ruleContext.getDialect("java").getPackageRegistry().getTypeResolver())
                .setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, CONSEQUENCE_SERIAL_UID);

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) { }
        }).addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(name);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.hashCode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class),
                     new ConsequenceGenerator.GetMethodBytecodeMethod(data)
        ).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class),
                    new ConsequenceGenerator.EqualsMethod()
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                // Tuple tuple = knowledgeHelper.getTuple();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getTuple", Tuple.class);
                invokeInterface(Tuple.class, "toFactHandles", InternalFactHandle[].class);
                mv.visitVarInsn(ASTORE, 3); // InternalFactHandle[]

                // Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getActivation().getTuple().getLeftTupleSink()).getDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getActivation", Activation.class);
                invokeInterface(Activation.class, "getTuple", LeftTuple.class);
                invokeInterface(LeftTuple.class, "getLeftTupleSink", LeftTupleSink.class);
                cast(RuleTerminalNode.class);
                invokeVirtual(RuleTerminalNode.class, "getDeclarations", Declaration[].class);
                mv.visitVarInsn(ASTORE, 4);

                String[] declarationTypes = data.getDeclarationTypes();
                int[] paramsPos = new int[declarations.length];
                int offset = 5;
                for (int i = 0; i < declarations.length; i++) {
                    int factPos = offset;
                    int objPos = ++offset;
                    paramsPos[i] = factPos;

                    // InternalFactHandle fact[i] = handles[declaration[i].getPattern().getOffset()];
                    mv.visitVarInsn(ALOAD, 3); // InternalFactHandle[]
                    mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                    push(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    invokeVirtual(Declaration.class, "getPattern", Pattern.class);
                    invokeVirtual(Pattern.class, "getOffset", Integer.TYPE);
                    mv.visitInsn(AALOAD); // handles[declaration[i].getPattern().getOffset()]
                    mv.visitVarInsn(ASTORE, factPos); // fact[i]

                    if (data.getNotPatterns()[i]) {
                        // declarations[i].getValue((org.drools.common.InternalWorkingMemory)workingMemory, fact[i].getObject() );
                        mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                        push(i); // i
                        mv.visitInsn(AALOAD); // declarations[i]
                        mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                        cast(InternalWorkingMemory.class);
                        mv.visitVarInsn(ALOAD, factPos); // fact[i]
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
                        mv.visitVarInsn(ASTORE, factPos);
                    } else {
                        mv.visitVarInsn(ALOAD, factPos); // fact[i]
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
                    mv.visitVarInsn(ALOAD, paramsPos[i]); // fact[i]
                    consequenceMethodDescr.append(typeDescr(declarationTypes[i]) + "Lorg/drools/FactHandle;");
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                String[] globals = data.getGlobals();
                String[] globalTypes = data.getGlobalTypes();
                for (int i = 0; i < globals.length; i++) {
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    push(globals[i]);
                    invokeInterface(WorkingMemory.class, "getGlobal", Object.class, String.class);
                    mv.visitTypeInsn(CHECKCAST, internalName(globalTypes[i]));
                    consequenceMethodDescr.append(typeDescr(globalTypes[i]));
                }

                consequenceMethodDescr.append(")V");
                mv.visitMethodInsn(INVOKESTATIC, data.getInternalRuleClassName(), data.getMethodName(), consequenceMethodDescr.toString());
                mv.visitInsn(RETURN);
            }
        });

        return generator.generateBytecode();
    }
}
