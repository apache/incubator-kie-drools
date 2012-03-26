package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

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
                .addField(ACC_PRIVATE, "returnValue", ReturnValueExpression.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(FieldValue.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Label l1 = new Label();
                mv.visitVarInsn(ALOAD, 0);
                getField("returnValue", ReturnValueExpression.class);
                mv.visitJumpInsn(IFNONNULL, l1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitVarInsn(ALOAD, 6);
                invokeStatic(ReturnValueGenerator.class, "generate", null, ReturnValueStub.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class);
                mv.visitLabel(l1);
                mv.visitVarInsn(ALOAD, 0);
                getField("returnValue", ReturnValueExpression.class);
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
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                putField("returnValue", ReturnValueExpression.class);
                mv.visitInsn(RETURN);
            }
        });
    }
}
