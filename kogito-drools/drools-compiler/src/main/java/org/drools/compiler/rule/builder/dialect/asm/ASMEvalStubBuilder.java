package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.rule.builder.dialect.asm.ClassGenerator;
import org.drools.core.rule.builder.dialect.asm.EvalGenerator;
import org.drools.core.rule.builder.dialect.asm.EvalStub;
import org.drools.core.rule.builder.dialect.asm.InvokerDataProvider;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.Tuple;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;

import java.util.Map;

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
                .addField(ACC_PRIVATE + ACC_VOLATILE, "eval", EvalExpression.class);

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
                Label syncStart = new Label();
                Label syncEnd = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(syncStart, l1, l2, null);
                Label l3 = new Label();
                mv.visitTryCatchBlock(l2, l3, l2, null);
                getFieldFromThis("eval", EvalExpression.class);
                mv.visitJumpInsn(IFNONNULL, syncEnd);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(DUP);
                mv.visitVarInsn(ASTORE, 5);
                // synchronized(this) {
                mv.visitInsn(MONITORENTER);
                mv.visitLabel(syncStart);
                getFieldFromThis("eval", EvalExpression.class);
                // if (eval == null) ...
                Label ifNotInitialized = new Label();
                mv.visitJumpInsn(IFNONNULL, ifNotInitialized);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                // ... EvalGenerator.generate(this, tuple, declarations, workingMemory)
                invokeStatic(EvalGenerator.class, "generate", null, EvalStub.class, Tuple.class, Declaration[].class, WorkingMemory.class);
                mv.visitLabel(ifNotInitialized);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitInsn(MONITOREXIT);
                mv.visitLabel(l1);
                mv.visitJumpInsn(GOTO, syncEnd);
                mv.visitLabel(l2);
                mv.visitVarInsn(ASTORE, 6);
                mv.visitVarInsn(ALOAD, 5);
                mv.visitInsn(MONITOREXIT);
                mv.visitLabel(l3);
                mv.visitVarInsn(ALOAD, 6);
                mv.visitInsn(ATHROW);
                mv.visitLabel(syncEnd);
                // } end of synchronized
                getFieldFromThis("eval", EvalExpression.class);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitVarInsn(ALOAD, 3);
                mv.visitVarInsn(ALOAD, 4);
                invokeInterface(EvalExpression.class, "evaluate", Boolean.TYPE, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class);
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "setEval", generator.methodDescr(null, EvalExpression.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                putFieldInThisFromRegistry("eval", EvalExpression.class, 1);
                mv.visitInsn(RETURN);
            }
        });
    }
}
