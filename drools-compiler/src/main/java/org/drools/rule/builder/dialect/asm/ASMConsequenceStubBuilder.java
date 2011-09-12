package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.core.util.asm.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class ASMConsequenceStubBuilder extends AbstractASMConsequenceBuilder {

    private static final Long CONSEQUENCE_SERIAL_UID = new Long(510L);

    protected byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext) {
        final String packageName = (String) consequenceContext.get("package");
        final String invokerClassName = (String) consequenceContext.get("invokerClassName");
        final Set<String> imports = ruleContext.getPkg().getImports().keySet();

        final ClassGenerator generator = new ClassGenerator(packageName + "." + invokerClassName,
                                                            ruleContext.getPackageBuilder().getRootClassLoader(),
                                                            ruleContext.getDialect("java").getPackageRegistry().getTypeResolver());

        generateConsequence(generator, consequenceContext, imports);

        return generator.generateBytecode();
    }

    void generateConsequence(final ClassGenerator generator, final Map<String, Object> consequenceContext, final Set<String> imports) {
        final ConsequenceDataProvider data = new ConsequenceContext(consequenceContext);
        final String invokerClassName = (String)consequenceContext.get("invokerClassName");
        final String name = (String)consequenceContext.get("consequenceName");
        final String consequenceClassName = invokerClassName + "Generated";

        generator.setInterfaces(ConsequenceStub.class, CompiledInvoker.class);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, CONSEQUENCE_SERIAL_UID)
                .addField(ACC_PRIVATE, "consequence", Consequence.class);

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
            }
        }).addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(name);
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        }).addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.hashCode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class),
                     new ConsequenceGenerator.GetMethodBytecodeMethod(data)
        ).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class),
                    new ConsequenceGenerator.EqualsMethod()
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
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
        }).addMethod(ACC_PUBLIC, "getPackageName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getPackageName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getConsequenceClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(consequenceClassName);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getRuleClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getRuleClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getInternalRuleClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getInternalRuleClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getMethodName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getDeclarationTypes", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getDeclarationTypes());
            }
        }).addMethod(ACC_PUBLIC, "getGlobals", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getGlobals());
            }
        }).addMethod(ACC_PUBLIC, "getGlobalTypes", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getGlobalTypes());
            }
        }).addMethod(ACC_PUBLIC, "getNotPatterns", generator.methodDescr(Boolean[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getNotPatterns());
            }
        }).addMethod(ACC_PUBLIC, "getPackageImports", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(imports, String.class);
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
