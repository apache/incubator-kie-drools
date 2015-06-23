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

package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.rule.builder.dialect.asm.ClassGenerator;
import org.drools.core.rule.builder.dialect.asm.InvokerDataProvider;
import org.drools.core.rule.builder.dialect.asm.ReturnValueGenerator;
import org.drools.core.rule.builder.dialect.asm.ReturnValueStub;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.FieldValue;
import org.drools.core.spi.ReturnValueExpression;
import org.drools.core.spi.Tuple;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;

import java.util.Map;

import static org.mvel2.asm.Opcodes.*;

public class ASMReturnValueStubBuilder extends AbstractASMReturnValueBuilder {

    protected byte[] createReturnValueBytecode(RuleBuildContext ruleContext, Map vars, boolean readLocalsFromTuple) {
        final InvokerDataProvider data = new InvokerContext(vars);
        final ClassGenerator generator = InvokerGenerator.createInvokerStubGenerator(data, ruleContext);
        createStubReturnValue(generator, data, vars);
        return generator.generateBytecode();
    }

    private void createStubReturnValue(final ClassGenerator generator, final InvokerDataProvider data, final Map vars) {
        generator.setInterfaces(ReturnValueStub.class, CompiledInvoker.class)
                .addField(ACC_PRIVATE + ACC_VOLATILE, "returnValue", ReturnValueExpression.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(FieldValue.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Label syncStart = new Label();
                Label syncEnd = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(syncStart, l1, l2, null);
                Label l3 = new Label();
                mv.visitTryCatchBlock(l2, l3, l2, null);
                getFieldFromThis("returnValue", ReturnValueExpression.class);
                mv.visitJumpInsn(IFNONNULL, syncEnd);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(DUP);
                mv.visitVarInsn(ASTORE, 7);
                // synchronized(this) {
                mv.visitInsn(MONITORENTER);
                mv.visitLabel(syncStart);
                getFieldFromThis("returnValue", ReturnValueExpression.class);
                // if (returnValue == null) ...
                Label ifNotInitialized = new Label();
                mv.visitJumpInsn(IFNONNULL, ifNotInitialized);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitVarInsn(ALOAD, 5);
                invokeStatic(ReturnValueGenerator.class, "generate", null, ReturnValueStub.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class);
                mv.visitLabel(ifNotInitialized);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitInsn(MONITOREXIT);
                mv.visitLabel(l1);
                mv.visitJumpInsn(GOTO, syncEnd);
                mv.visitLabel(l2);
                mv.visitVarInsn(ASTORE, 8);
                mv.visitVarInsn(ALOAD, 7);
                mv.visitInsn(MONITOREXIT);
                mv.visitLabel(l3);
                mv.visitVarInsn(ALOAD, 8);
                mv.visitInsn(ATHROW);
                mv.visitLabel(syncEnd);
                // } end of synchronized
                getFieldFromThis("returnValue", ReturnValueExpression.class);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitVarInsn(ALOAD, 6);
                invokeInterface(ReturnValueExpression.class, "evaluate", FieldValue.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "setReturnValue", generator.methodDescr(null, ReturnValueExpression.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                putFieldInThisFromRegistry("returnValue", ReturnValueExpression.class, 1);
                mv.visitInsn(RETURN);
            }
        });
    }
}
