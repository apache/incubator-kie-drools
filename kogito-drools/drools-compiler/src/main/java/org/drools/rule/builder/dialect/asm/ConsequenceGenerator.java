package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.common.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class ConsequenceGenerator extends InvokerGenerator {

    public static void generate(final ConsequenceStub stub, KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) {
        final String[] declarationTypes = stub.getDeclarationTypes();
        final Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getActivation().getTuple().getLeftTupleSink()).getDeclarations();
        final LeftTuple tuple = (LeftTuple)knowledgeHelper.getTuple();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarationTypes, declarations, tuple);

        final ClassGenerator generator = createInvokerClassGenerator(stub, workingMemory)
                .setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(stub.getGeneratedInvokerClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new EvaluateMethod() {
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

                offset = 6;
                int[] paramsPos = new int[declarations.length];
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex();
                    int handlePos = offset;
                    int objPos = ++offset;
                    paramsPos[i] = handlePos;

                    traverseTuplesUntilDeclaration(i, 4, 3, 5);

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

                        storeObjectFromDeclaration(declarations[i], declarationTypes[i]);

                        // fact[i] = (InternalFactHandle)workingMemory.getFactHandle(obj);
                        mv.visitVarInsn(ALOAD, 2);
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
                parseGlobals(stub.getGlobals(), stub.getGlobalTypes(), 2, consequenceMethodDescr);

                consequenceMethodDescr.append(")V");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), consequenceMethodDescr.toString());
                mv.visitInsn(RETURN);
            }
        });

        stub.setConsequence(generator.<Consequence>newInstance());
    }

}
