/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule.builder.dialect.asm;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.WorkingMemory;
import org.drools.core.rule.builder.dialect.asm.GeneratorHelper.DeclarationMatcher;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.mvel2.asm.MethodVisitor;

import java.util.List;

import static org.drools.core.rule.builder.dialect.asm.GeneratorHelper.createInvokerClassGenerator;
import static org.drools.core.rule.builder.dialect.asm.GeneratorHelper.matchDeclarationsToTuple;

import static org.mvel2.asm.Opcodes.AALOAD;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.CHECKCAST;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;
import static org.mvel2.asm.Opcodes.RETURN;

public class ConsequenceGenerator {

    public static void generate(final ConsequenceStub stub, KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) {
        RuleTerminalNode rtn = (RuleTerminalNode) knowledgeHelper.getMatch().getTuple().getLeftTupleSink();
        final Declaration[] declarations = rtn.getRequiredDeclarations();
        final LeftTuple tuple = (LeftTuple)knowledgeHelper.getTuple();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarations);

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

                // Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getMatch().getTuple().getLeftTupleSink()).getDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getMatch", Activation.class);
                invokeInterface(Activation.class, "getTuple", LeftTuple.class);
                invokeInterface(LeftTuple.class, "getLeftTupleSink", LeftTupleSink.class);
                cast(RuleTerminalNode.class);
                invokeVirtual(RuleTerminalNode.class, "getRequiredDeclarations", Declaration[].class);
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

                    currentLeftTuple = traverseTuplesUntilDeclaration(currentLeftTuple, matcher.getRootDistance(), 3);

                    // handle = tuple.getHandle()
                    mv.visitVarInsn(ALOAD, 3);
                    invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                    mv.visitVarInsn(ASTORE, handlePos);

                    String declarationType = declarations[i].getTypeName();
                    if (stub.getNotPatterns()[i]) {
                        // notPattern indexes field declarations
                        
                        // declarations[i].getValue((InternalWorkingMemory)workingMemory, fact[i].getObject());
                        mv.visitVarInsn(ALOAD, 4); // org.kie.rule.Declaration[]
                        push(i); // i
                        mv.visitInsn(AALOAD); // declarations[i]
                        mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                        cast(InternalWorkingMemory.class);
                        mv.visitVarInsn(ALOAD, handlePos); // handle[i]
                        invokeInterface(InternalFactHandle.class, "getObject", Object.class);

                        storeObjectFromDeclaration(declarations[i], declarationType);

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
                        mv.visitTypeInsn(CHECKCAST, internalName(declarationType));
                        objAstorePos += store(objPos, declarationType); // obj[i]
                    }
                }

                // @{ruleClassName}.@{methodName}(KnowledgeHelper, @foreach{declr : declarations} Object, FactHandle @end)
                StringBuilder consequenceMethodDescr = new StringBuilder("(L" + KnowledgeHelper.class.getName().replace('.', '/') +";");
                mv.visitVarInsn(ALOAD, 1); // KnowledgeHelper
                for (int i = 0; i < declarations.length; i++) {
                    load(paramsPos[i] + 1); // obj[i]
                    mv.visitVarInsn(ALOAD, paramsPos[i]); // handle[i]
                    consequenceMethodDescr.append(typeDescr(declarations[i].getTypeName())).append("L" + FactHandle.class.getName().replace('.', '/') + ";");
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
