/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.asm;

import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.builder.dialect.asm.ReturnValueStub;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.ReturnValueExpression;
import org.drools.core.spi.Tuple;
import org.drools.mvel.asm.GeneratorHelper.DeclarationMatcher;
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

public class ReturnValueGenerator {
    public static void generate(final ReturnValueStub stub,
                                final Tuple tuple,
                                final Declaration[] previousDeclarations,
                                final Declaration[] localDeclarations,
                                final WorkingMemory workingMemory) {

        final String[] globals = stub.getGlobals();
        final String[] globalTypes = stub.getGlobalTypes();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(previousDeclarations);

        final ClassGenerator generator = createInvokerClassGenerator(stub, workingMemory)
                .setInterfaces(ReturnValueExpression.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(FieldValue.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                objAstorePos = 9;

                int[] previousDeclarationsParamsPos = new int[previousDeclarations.length];

                mv.visitVarInsn(ALOAD, 2);
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 7); // LeftTuple

                Tuple currentTuple = tuple;
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex();
                    previousDeclarationsParamsPos[i] = objAstorePos;

                    currentTuple = traverseTuplesUntilDeclaration(currentTuple, matcher.getRootDistance(), 7);

                    mv.visitVarInsn(ALOAD, 3);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 5); // workingMemory

                    mv.visitVarInsn(ALOAD, 7);
                    invokeInterface(LeftTuple.class, "getFactHandle", InternalFactHandle.class);
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class); // tuple.getFactHandle().getObject()

                    storeObjectFromDeclaration(previousDeclarations[i], previousDeclarations[i].getTypeName());
                }

                int[] localDeclarationsParamsPos = parseDeclarations(localDeclarations, 4, 2, 5, false);

                // @{ruleClassName}.@{methodName}(@foreach{previousDeclarations}, @foreach{localDeclarations}, @foreach{globals})
                StringBuilder returnValueMethodDescr = new StringBuilder("(");
                for (int i = 0; i < previousDeclarations.length; i++) {
                    load(previousDeclarationsParamsPos[i]); // previousDeclarations[i]
                    returnValueMethodDescr.append(typeDescr(previousDeclarations[i].getTypeName()));
                }
                for (int i = 0; i < localDeclarations.length; i++) {
                    load(localDeclarationsParamsPos[i]); // localDeclarations[i]
                    returnValueMethodDescr.append(typeDescr(localDeclarations[i].getTypeName()));
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                parseGlobals(globals, globalTypes, 5, returnValueMethodDescr);

                returnValueMethodDescr.append(")Lorg/drools/core/spi/FieldValue;");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), returnValueMethodDescr.toString());
                mv.visitInsn(ARETURN);
            }
        });
        
        stub.setReturnValue(generator.<ReturnValueExpression>newInstance());
    }
}
