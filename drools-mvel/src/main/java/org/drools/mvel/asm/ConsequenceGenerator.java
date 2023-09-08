/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.asm;

import org.drools.base.base.ValueResolver;
import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.base.reteoo.BaseTuple;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.Tuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.base.rule.consequence.Consequence;
import org.drools.base.rule.consequence.ConsequenceContext;
import org.drools.mvel.asm.GeneratorHelper.DeclarationMatcher;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.asm.MethodVisitor;

import java.util.List;

import static org.drools.mvel.asm.GeneratorHelper.createInvokerClassGenerator;
import static org.drools.mvel.asm.GeneratorHelper.matchDeclarationsToTuple;
import static org.mvel2.asm.Opcodes.*;

public class ConsequenceGenerator {

    public static void generate(final ConsequenceStub stub, ConsequenceContext knowledgeHelper, ValueResolver valueResolver) {
        final Declaration[] declarations = knowledgeHelper.getRequiredDeclarations();
        final Tuple tuple = (Tuple) knowledgeHelper.getTuple();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarations);

        final ClassGenerator generator = createInvokerClassGenerator(stub, valueResolver).setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(stub.getGeneratedInvokerClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, ConsequenceContext.class, ValueResolver.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                // Tuple tuple = knowledgeHelper.getTuple();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(ConsequenceContext.class, "getTuple", BaseTuple.class);
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 3); // LeftTuple

                // Declaration[] declarations = knowledgeHelper.getRequiredDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(ConsequenceContext.class, "getRequiredDeclarations", Declaration[].class);
                mv.visitVarInsn(ASTORE, 4);

                BaseTuple currentTuple = tuple;
                objAstorePos = 6; // astore start position for objects to store in loop
                int[] paramsPos = new int[declarations.length];
                // declarationMatchers is already sorted by offset with tip declarations now first
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getMatcherIndex(); // original index refers to the array position with RuleTerminalNode.getDeclarations()
                    int handlePos = objAstorePos;
                    int objPos = ++objAstorePos;
                    paramsPos[i] = handlePos;

                    currentTuple = traverseTuplesUntilDeclaration(currentTuple, matcher.getTupleIndex(), 3);

                    // handle = tuple.getFactHandle()
                    mv.visitVarInsn(ALOAD, 3);
                    invokeInterface(Tuple.class, "getOriginalFactHandle", InternalFactHandle.class);
                    mv.visitVarInsn(ASTORE, handlePos);

                    String declarationType = declarations[i].getTypeName();
                    if (stub.getNotPatterns()[i]) {
                        // notPattern indexes field declarations
                        
                        // declarations[i].getValue(reteEvaluator, fact[i].getObject());
                        mv.visitVarInsn(ALOAD, 4); // org.kie.rule.Declaration[]
                        push(i); // i
                        mv.visitInsn(AALOAD); // declarations[i]
                        mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                        mv.visitVarInsn(ALOAD, handlePos); // handle[i]
                        invokeInterface(InternalFactHandle.class, "getObject", Object.class);

                        storeObjectFromDeclaration(declarations[i], declarationType);

                        // The facthandle should be set to that of the field, if it's an object, otherwise this will return null
                        // fact[i] = (InternalFactHandle)reteEvaluator.getFactHandle(obj);
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
                StringBuilder consequenceMethodDescr = new StringBuilder("(L" + ConsequenceContext.class.getName().replace('.', '/') +";");
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

        Consequence consequence = generator.newInstance();
        stub.setConsequence(consequence);
    }
}
