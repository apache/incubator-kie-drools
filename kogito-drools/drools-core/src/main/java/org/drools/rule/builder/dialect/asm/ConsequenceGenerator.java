package org.drools.rule.builder.dialect.asm;

import org.drools.rule.builder.dialect.asm.GeneratorHelper.DeclarationMatcher;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.spi.Activation;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.Tuple;
import org.mvel2.asm.MethodVisitor;

import java.util.List;

import static org.drools.rule.builder.dialect.asm.GeneratorHelper.createInvokerClassGenerator;
import static org.drools.rule.builder.dialect.asm.GeneratorHelper.matchDeclarationsToTuple;

import static org.mvel2.asm.Opcodes.AALOAD;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.CHECKCAST;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;
import static org.mvel2.asm.Opcodes.RETURN;

public class ConsequenceGenerator {

    public static void generate(final ConsequenceStub stub, final KnowledgeHelper knowledgeHelper, final WorkingMemory workingMemory) {
        final String[] declarationTypes = stub.getDeclarationTypes();
        final RuleTerminalNode rtn = (RuleTerminalNode) knowledgeHelper.getActivation().getTuple().getLeftTupleSink();
        final Declaration[] declarations = rtn.getDeclarations();
        final LeftTuple tuple = (LeftTuple)knowledgeHelper.getTuple();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarationTypes, declarations, tuple);

        final ClassGenerator generator = createInvokerClassGenerator(stub, workingMemory).setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(stub.getGeneratedInvokerClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
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

                
                LeftTuple currentLeftTuple = tuple;
                objAstorePos = 6; // astore start position for objects to store in loop
                int[] paramsPos = new int[declarations.length];
                // declarationMatchers is already sorted by offset with tip declarations now first
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex(); // original index refers to the array position with RuleTerminalNode.getDeclarations()
                    int handlePos = objAstorePos;
                    int objPos = ++objAstorePos;
                    paramsPos[i] = handlePos;

                    if ( rtn.getRule().getTransformedLhs().length > 1 ) {
                        // We do not generate an invoker per 'or' branch, so use runtime traversal for that
                        traverseTuplesUntilDeclarationWithOr(i, 4, 3, 5);                        
                    } else {
                        currentLeftTuple = traverseTuplesUntilDeclaration(currentLeftTuple, i, matcher.getRootDistance(), 4, 3, 5);                        
                    }

                    // handle = tuple.getHandle()
                    mv.visitVarInsn(ALOAD, 3);
                    invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                    mv.visitVarInsn(ASTORE, handlePos);

                    if (stub.getNotPatterns()[i]) {
                        // notPattern indexes field declarations
                        
                        // declarations[i].getValue((InternalWorkingMemory)workingMemory, fact[i].getObject());
                        mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                        push(i); // i
                        mv.visitInsn(AALOAD); // declarations[i]
                        mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                        cast(InternalWorkingMemory.class);
                        mv.visitVarInsn(ALOAD, handlePos); // handle[i]
                        invokeInterface(InternalFactHandle.class, "getObject", Object.class);

                        storeObjectFromDeclaration(declarations[i], declarationTypes[i]);

                        // The facthandle should be set to that of the field, if it's an object, otherwise this will return null
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
                        objAstorePos += store(objPos, declarationTypes[i]); // obj[i]
                    }
                }

                // @{ruleClassName}.@{methodName}(KnowledgeHelper, @foreach{declr : declarations} Object, FactHandle @end)
                StringBuilder consequenceMethodDescr = new StringBuilder("(Lorg/drools/spi/KnowledgeHelper;");
                mv.visitVarInsn(ALOAD, 1); // KnowledgeHelper
                for (int i = 0; i < declarations.length; i++) {
                    load(paramsPos[i] + 1); // obj[i]
                    mv.visitVarInsn(ALOAD, paramsPos[i]); // handle[i]
                    consequenceMethodDescr.append(typeDescr(declarationTypes[i])).append("Lorg/drools/FactHandle;");
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
