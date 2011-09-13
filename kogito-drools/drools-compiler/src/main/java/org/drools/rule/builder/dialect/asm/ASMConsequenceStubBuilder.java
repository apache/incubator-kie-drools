package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

import static org.drools.rule.builder.dialect.asm.InvokerGenerator.*;

public class ASMConsequenceStubBuilder extends AbstractASMConsequenceBuilder {

    protected byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext) {
        final InvokerDataProvider data = new InvokerContext(consequenceContext);
        final ClassGenerator generator = createInvokerStubGenerator(data, ruleContext);
        createStubConsequence(generator, data, consequenceContext);
        return generator.generateBytecode();
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
                Label l1 = new Label();
                mv.visitVarInsn(ALOAD, 0);
                getField("consequence", Consequence.class);
                mv.visitJumpInsn(IFNONNULL, l1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                invokeStatic(ConsequenceGenerator.class, "generate", null, ConsequenceStub.class, KnowledgeHelper.class, WorkingMemory.class);
                mv.visitLabel(l1);
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
