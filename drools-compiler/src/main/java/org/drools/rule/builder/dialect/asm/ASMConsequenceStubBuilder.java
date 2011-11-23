package org.drools.rule.builder.dialect.asm;

import org.drools.WorkingMemory;
import org.drools.rule.GroupElement;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.asm.Label;
import org.mvel2.asm.MethodVisitor;

import java.util.Map;

import static org.drools.rule.builder.dialect.asm.InvokerGenerator.createInvokerStubGenerator;
import static org.mvel2.asm.Opcodes.ACC_PRIVATE;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.IFNONNULL;
import static org.mvel2.asm.Opcodes.RETURN;
import static org.mvel2.asm.Opcodes.DUP;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.MONITORENTER;
import static org.mvel2.asm.Opcodes.MONITOREXIT;
import static org.mvel2.asm.Opcodes.GOTO;
import static org.mvel2.asm.Opcodes.ATHROW;

public class ASMConsequenceStubBuilder extends ASMConsequenceBuilder {

    protected byte[] createConsequenceBytecode(final RuleBuildContext ruleContext, final Map<String, Object> consequenceContext) {
        // If the LHS contains an OR we cannot use the optimized consequence invoker that traverses the tuple only once
        if (isOr(ruleContext.getRule().getLhs())) {
            return super.createConsequenceBytecode(ruleContext, consequenceContext);
        }

        final InvokerDataProvider data = new InvokerContext(consequenceContext);
        final ClassGenerator generator = createInvokerStubGenerator(data, ruleContext);
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
                .addField(ACC_PRIVATE, "consequence", Consequence.class);

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push((String)vars.get("consequenceName"));
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        }).addMethod(ACC_PUBLIC, "getNotPatterns", generator.methodDescr(Boolean[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray((Boolean[]) vars.get("notPatterns"));
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Label syncStart = new Label();
                Label syncEnd = new Label();
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitTryCatchBlock(syncStart, l1, l2, null);
                Label l3 = new Label();
                mv.visitTryCatchBlock(l2, l3, l2, null);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(DUP);
                mv.visitVarInsn(ASTORE, 3);
                // synchronized(this) {
                mv.visitInsn(MONITORENTER);
                mv.visitLabel(syncStart);
                mv.visitVarInsn(ALOAD, 0);
                getField("consequence", Consequence.class);
                // if (consequence == null) ...
                Label ifNotInitialized = new Label();
                mv.visitJumpInsn(IFNONNULL, ifNotInitialized);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                // ... ConsequenceGenerator.generate(this, knowledgeHelper, workingMemory)
                invokeStatic(ConsequenceGenerator.class, "generate", null, ConsequenceStub.class, KnowledgeHelper.class, WorkingMemory.class);
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
                mv.visitVarInsn(ALOAD, 0);
                getField("consequence", Consequence.class);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                invokeInterface(Consequence.class, "evaluate", null, KnowledgeHelper.class, WorkingMemory.class);
                mv.visitInsn(RETURN);
            }
        }).addMethod(ACC_PUBLIC, "setConsequence", generator.methodDescr(null, Consequence.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                putField("consequence", Consequence.class);
                mv.visitInsn(RETURN);
            }
        });
    }
}
