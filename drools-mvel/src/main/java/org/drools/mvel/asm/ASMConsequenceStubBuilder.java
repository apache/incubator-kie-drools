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

import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.accessor.CompiledInvoker;
import org.drools.base.rule.consequence.Consequence;
import org.drools.base.rule.consequence.ConsequenceContext;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;

import static org.mvel2.asm.Opcodes.ACC_PRIVATE;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ACC_VOLATILE;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.ATHROW;
import static org.mvel2.asm.Opcodes.DUP;
import static org.mvel2.asm.Opcodes.GOTO;
import static org.mvel2.asm.Opcodes.IFNONNULL;
import static org.mvel2.asm.Opcodes.MONITORENTER;
import static org.mvel2.asm.Opcodes.MONITOREXIT;
import static org.mvel2.asm.Opcodes.RETURN;

public class ASMConsequenceStubBuilder extends ASMConsequenceBuilder {

    protected byte[] createConsequenceBytecode(final RuleBuildContext ruleContext, final Map<String, Object> consequenceContext) {
        // If the LHS contains an OR we cannot use the optimized consequence invoker that traverses the tuple only once
        if (isOr(ruleContext.getRule().getLhs())) {
            return super.createConsequenceBytecode(ruleContext, consequenceContext);
        }

        final InvokerDataProvider data = new InvokerContext(consequenceContext);
        final ClassGenerator generator = InvokerGenerator.createInvokerStubGenerator(data, ruleContext);
        createStubConsequence(generator, data, consequenceContext);
        return generator.generateBytecode();
    }

    private boolean isOr(GroupElement groupElement) {
        if (groupElement.isOr()) return true;
        for (Object child : groupElement.getChildren()) {
            if (child instanceof GroupElement && isOr((GroupElement)child)) return true;
        }
        return false;
    }

    private void createStubConsequence(final ClassGenerator generator, final InvokerDataProvider data, final Map<String, Object> vars) {
        generator.setInterfaces(ConsequenceStub.class, CompiledInvoker.class)
                .addField(ACC_PRIVATE + ACC_VOLATILE, "consequence", Consequence.class);

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(vars.get("consequenceName"));
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        }).addMethod(ACC_PUBLIC, "getNotPatterns", generator.methodDescr(Boolean[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray((Boolean[]) vars.get("notPatterns"));
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, ConsequenceContext.class, ValueResolver.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Label syncStart = new Label();
                Label syncEnd = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(syncStart, l1, l2, null);
                Label l3 = new Label();
                mv.visitTryCatchBlock(l2, l3, l2, null);
                getFieldFromThis("consequence", Consequence.class);
                mv.visitJumpInsn(IFNONNULL, syncEnd);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(DUP);
                mv.visitVarInsn(ASTORE, 3);
                // synchronized(this) {
                mv.visitInsn(MONITORENTER);
                mv.visitLabel(syncStart);
                getFieldFromThis("consequence", Consequence.class);
                // if (consequence == null) ...
                Label ifNotInitialized = new Label();
                mv.visitJumpInsn(IFNONNULL, ifNotInitialized);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                // ... ConsequenceGenerator.generate(this, knowledgeHelper, workingMemory)
                invokeStatic(ConsequenceGenerator.class, "generate", null, ConsequenceStub.class, ConsequenceContext.class, ValueResolver.class);
                mv.visitLabel(ifNotInitialized);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(MONITOREXIT);
                mv.visitLabel(l1);
                mv.visitJumpInsn(GOTO, syncEnd);
                mv.visitLabel(l2);
                mv.visitVarInsn(ASTORE, 4);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitInsn(MONITOREXIT);
                mv.visitLabel(l3);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitInsn(ATHROW);
                mv.visitLabel(syncEnd);
                // } end of synchronized
                getFieldFromThis("consequence", Consequence.class);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                invokeInterface(Consequence.class, "evaluate", null, ConsequenceContext.class, ValueResolver.class);
                mv.visitInsn(RETURN);
            }
        }).addMethod(ACC_PUBLIC, "setConsequence", generator.methodDescr(null, Consequence.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                putFieldInThisFromRegistry("consequence", Consequence.class, 1);
                mv.visitInsn(RETURN);
            }
        });
    }
}
