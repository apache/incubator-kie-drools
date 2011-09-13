package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class ASMEvalStubBuilder extends AbstractASMEvalBuilder {

    protected byte[] createEvalBytecode(final RuleBuildContext ruleContext, final Map vars) {
        final InvokerDataProvider data = new InvokerContext(vars);
        final ClassGenerator generator = InvokerGenerator.createInvokerStubGenerator(data, ruleContext);
        createStubEval(generator, data, vars);
        return generator.generateBytecode();
    }

    private void createStubEval(final ClassGenerator generator, final InvokerDataProvider data, final Map vars) {
        generator.setInterfaces(EvalStub.class, CompiledInvoker.class)
                .addField(ACC_PRIVATE, "eval", EvalExpression.class);

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
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(Boolean.TYPE, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Label l1 = new Label();
                mv.visitVarInsn(ALOAD, 0);
                getField("eval", EvalExpression.class);
                mv.visitJumpInsn(IFNONNULL, l1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                invokeStatic(EvalGenerator.class, "generate", null, EvalStub.class, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class);
                mv.visitLabel(l1);
                mv.visitVarInsn(ALOAD, 0);
                getField("eval", EvalExpression.class);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                invokeInterface(EvalExpression.class, "evaluate", Boolean.TYPE, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class);
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "setEval", generator.methodDescr(null, EvalExpression.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                putField("eval", EvalExpression.class);
                mv.visitInsn(RETURN);
            }
        });
    }
}
