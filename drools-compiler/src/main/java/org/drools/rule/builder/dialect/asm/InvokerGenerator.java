package org.drools.rule.builder.dialect.asm;

import org.drools.base.TypeResolver;
import org.drools.rule.builder.RuleBuildContext;
import org.mvel2.asm.MethodVisitor;

import java.util.List;
import java.util.Set;

import static org.mvel2.asm.Opcodes.ACC_FINAL;
import static org.mvel2.asm.Opcodes.ACC_PRIVATE;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.IRETURN;

public class InvokerGenerator {

    public static ClassGenerator createInvokerStubGenerator(final InvokerDataProvider data, final RuleBuildContext ruleContext) {
        return createStubGenerator(data,
                                   ruleContext.getPackageBuilder().getRootClassLoader(),
                                   ruleContext.getDialect("java").getPackageRegistry().getTypeResolver(),
                                   ruleContext.getPkg().getImports().keySet());
    }

    public static ClassGenerator createStubGenerator(final InvokerDataProvider data,
                                                     final ClassLoader classLoader,
                                                     final TypeResolver typeResolver,
                                                     final Set<String> imports) {
        final ClassGenerator generator = new ClassGenerator(data.getPackageName() + "." + data.getInvokerClassName(),
                                                            classLoader,
                                                            typeResolver);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, GeneratorHelper.INVOKER_SERIAL_UID)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.hashCode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class), new GeneratorHelper.GetMethodBytecodeMethod(data)
        ).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class), new GeneratorHelper.EqualsMethod()
        ).addMethod(ACC_PUBLIC, "getPackageName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getPackageName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getGeneratedInvokerClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getInvokerClassName() + "Generated");
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
        }).addMethod(ACC_PUBLIC, "getInvokerClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getInvokerClassName());
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
        }).addMethod(ACC_PUBLIC, "getPackageImports", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(imports, String.class);
            }
        });

        return generator;
    }

    static ClassGenerator createInvokerClassGenerator(final InvokerDataProvider data, final RuleBuildContext ruleContext) {
        String className = data.getPackageName() + "." + data.getInvokerClassName();
        return GeneratorHelper.createInvokerClassGenerator(className, data,
                                           ruleContext.getPackageBuilder().getRootClassLoader(),
                                           ruleContext.getDialect("java").getPackageRegistry().getTypeResolver());
    }
}
