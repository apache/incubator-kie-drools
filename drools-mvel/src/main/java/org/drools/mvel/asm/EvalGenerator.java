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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.core.reteoo.LeftTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.base.rule.accessor.EvalExpression;
import org.drools.mvel.asm.GeneratorHelper.DeclarationMatcher;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.asm.MethodVisitor;

import static org.drools.mvel.asm.GeneratorHelper.createInvokerClassGenerator;
import static org.drools.mvel.asm.GeneratorHelper.matchDeclarationsToTuple;
import static org.mvel2.asm.Opcodes.AALOAD;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ACONST_NULL;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;
import static org.mvel2.asm.Opcodes.IRETURN;

public class EvalGenerator {

    private static final AtomicInteger evalId = new AtomicInteger();

    public static void generate(final EvalStub stub ,
                                final BaseTuple tuple,
                                final Declaration[] declarations,
                                final ValueResolver reteEvaluator) {

        final String[] globals = stub.getGlobals();
        final String[] globalTypes = stub.getGlobalTypes();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarations);

        final ClassGenerator generator = createInvokerClassGenerator(stub, "_" + evalId.getAndIncrement(), reteEvaluator)
                .setInterfaces(EvalExpression.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "clone", generator.methodDescr(EvalExpression.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(Boolean.TYPE, BaseTuple.class, Declaration[].class, ValueResolver.class, Object.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                objAstorePos = 7;

                String[] expectedDeclarations = stub.getExpectedDeclarationTypes();
                int[] declarationsParamsPos = new int[declarations.length];

                mv.visitVarInsn(ALOAD, 1);
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 5); // LeftTuple

                BaseTuple currentTuple = tuple;
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getMatcherIndex();
                    declarationsParamsPos[i] = objAstorePos;

                    currentTuple = traverseTuplesUntilDeclaration(currentTuple, matcher.getTupleIndex(), 5);

                    mv.visitVarInsn(ALOAD, 2);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 3); // reteEvaluator

                    mv.visitVarInsn(ALOAD, 5);
                    invokeInterface(BaseTuple.class, "getFactHandle", FactHandle.class);
                    invokeInterface(FactHandle.class, "getObject", Object.class); // tuple.getFactHandle().getObject()

                    storeObjectFromDeclaration(declarations[i], expectedDeclarations[i]);
                }

                // @{ruleClassName}.@{methodName}(@foreach{declarations}, @foreach{globals})
                StringBuilder evalMethodDescr = new StringBuilder("(");
                for (int i = 0; i < declarations.length; i++) {
                    load(declarationsParamsPos[i]); // declarations[i]
                    evalMethodDescr.append(typeDescr(expectedDeclarations[i]));
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                parseGlobals(globals, globalTypes, 3, evalMethodDescr);

                evalMethodDescr.append(")Z");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), evalMethodDescr.toString());
                mv.visitInsn(IRETURN);
            }
        });

        stub.setEval(generator.newInstance());
    }
}
