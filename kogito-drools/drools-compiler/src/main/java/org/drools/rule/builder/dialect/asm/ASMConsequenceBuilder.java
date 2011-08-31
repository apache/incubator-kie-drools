package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.compiler.*;
import org.drools.lang.descr.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.rule.builder.dialect.java.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import static org.drools.rule.builder.dialect.asm.ClassGenerator.mDescr;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;
import static org.mvel2.asm.Type.*;
import static org.mvel2.asm.Opcodes.*;

import java.util.*;

public class ASMConsequenceBuilder implements ConsequenceBuilder {

    private static final Long CONSEQUENCE_SERIAL_UID = new Long(510L);

    public void build(RuleBuildContext context, String consequenceName) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        Consequence consequence = createConsequence(context, consequenceName, consequenceContext(context, consequenceName));
        context.getRule().setConsequence(consequence);

        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }

    private Map<String, Object> consequenceContext(RuleBuildContext context, String consequenceName) {
        String className = consequenceName + "Consequence";
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
        JavaAnalysisResult analysis = createJavaAnalysisResult(context, consequenceName, decls);

        return createConsequenceContext(context, consequenceName, className, analysis.getAnalyzedExpr(), decls, analysis.getBoundIdentifiers());
    }

    private Consequence createConsequence(RuleBuildContext ruleContext, String consequenceName, final Map<String, Object> consequenceContext) {
        final String packageName = (String)consequenceContext.get("package");
        final String invokerClassName = (String)consequenceContext.get("invokerClassName");
        final String ruleClassName = (String)consequenceContext.get("ruleClassName");
        final String internalRuleClassName = (packageName + "." + ruleClassName).replace(".", "/");
        final String methodName = (String)consequenceContext.get("methodName");
        final String name = (String)consequenceContext.get("consequenceName");
        final Integer hashCode = (Integer)consequenceContext.get("hashCode");
        final Declaration[] declarations = (Declaration[])consequenceContext.get("declarations");
        final String[] declarationTypes = (String[])consequenceContext.get("declarationTypes");

        final ClassGenerator generator = new ClassGenerator(packageName + "." + invokerClassName)
                .setInterfaces("org/drools/spi/Consequence", "org/drools/spi/CompiledInvoker");

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", LONG_TYPE.getDescriptor(), CONSEQUENCE_SERIAL_UID)
                .addField(ACC_PRIVATE + ACC_FINAL, "consequenceName", getDescriptor(String.class));

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0); // read local variable 0 (initialized to this) and push it on the stack
                mv.visitLdcInsn(name); // push the String "default" on the stack
                mv.visitFieldInsn(PUTFIELD, cg.getClassDescriptor(), "consequenceName", getDescriptor(String.class));
            }
        }).addMethod(ACC_PUBLIC, "getName", mDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0); // read local variable 0 (initialized to this) and push it on the stack
                mv.visitFieldInsn(GETFIELD, cg.getClassDescriptor(), "consequenceName", getDescriptor(String.class));
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        }).addMethod(ACC_PUBLIC, "hashCode", mDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv) {
                mv.visitLdcInsn(hashCode);
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", mDescr(List.class), new ClassGenerator.MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "getClass", "()Ljava/lang/Class;");
                mv.visitLdcInsn(ruleClassName);
                mv.visitLdcInsn(packageName);
                mv.visitLdcInsn(methodName);
                mv.visitLdcInsn(internalRuleClassName + ".class");
                mv.visitMethodInsn(INVOKESTATIC, "org/drools/rule/Rule", "getMethodBytecode", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/util/List;");
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "equals", mDescr(Boolean.TYPE, Object.class), new ClassGenerator.MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv) {
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitVarInsn(ALOAD, 1); // if (object == null)
                mv.visitJumpInsn(IFNULL, l1);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(INSTANCEOF, "org/drools/spi/CompiledInvoker");
                mv.visitJumpInsn(IFNE, l2); // if (!(object instanceof  org.drools.spi.CompiledInvoker))
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0); // return false
                mv.visitInsn(IRETURN);
                mv.visitLabel(l2);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, cg.getClassDescriptor(), "getMethodBytecode", "()Ljava/util/List;");
                mv.visitVarInsn(ALOAD, 1);
                mv.visitTypeInsn(CHECKCAST, "org/drools/spi/CompiledInvoker");
                mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/spi/CompiledInvoker", "getMethodBytecode", "()Ljava/util/List;");
                mv.visitMethodInsn(INVOKESTATIC, "org/drools/core/util/asm/MethodComparator", "compareBytecode", "(Ljava/util/List;Ljava/util/List;)Z");
                // return org.drools.core.util.asm.MethodComparator.compareBytecode( getMethodBytecode(), (( org.drools.spi.CompiledInvoker ) object).getMethodBytecode() );
                mv.visitInsn(IRETURN);

            }
        }).addMethod(ACC_PUBLIC, "evaluate", mDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(ClassGenerator cg, MethodVisitor mv) {
                // Tuple tuple = knowledgeHelper.getTuple();
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/spi/KnowledgeHelper", "getTuple", "()Lorg/drools/spi/Tuple;");
                mv.visitVarInsn(ASTORE, 3);

                // Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getActivation().getTuple().getLeftTupleSink()).getDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/spi/KnowledgeHelper", "getActivation", "()Lorg/drools/spi/Activation;");
                mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/spi/Activation", "getTuple", "()Lorg/drools/reteoo/LeftTuple;");
                mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/reteoo/LeftTuple", "getLeftTupleSink", "()Lorg/drools/reteoo/LeftTupleSink;");
                mv.visitTypeInsn(CHECKCAST, "org/drools/reteoo/RuleTerminalNode");
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/reteoo/RuleTerminalNode", "getDeclarations", "()[Lorg/drools/rule/Declaration;");
                mv.visitVarInsn(ASTORE, 4);

                for (int i = 0; i < declarations.length; i++) {
                    // InternalFactHandle fact[i] = tuple.get(declarations[i]);
                    mv.visitVarInsn(ALOAD, 3); // org.drools.spi.Tuple
                    mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                    mv.visitLdcInsn(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/spi/Tuple", "get", "(Lorg/drools/rule/Declaration;)Lorg/drools/common/InternalFactHandle;");
                    mv.visitVarInsn(ASTORE, i+5); // fact[i]

                    // declarations[i].getValue((org.drools.common.InternalWorkingMemory)workingMemory, fact[i].getObject() );
                    mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                    mv.visitLdcInsn(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    mv.visitTypeInsn(CHECKCAST, "org/drools/common/InternalWorkingMemory"); // (org.drools.common.InternalWorkingMemory)workingMemory
                    mv.visitVarInsn(ALOAD, i+5); // fact[i]
                    mv.visitMethodInsn(INVOKEINTERFACE, "org/drools/common/InternalFactHandle", "getObject", "()Ljava/lang/Object;");
                    mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/rule/Declaration", "getValue", "(Lorg/drools/common/InternalWorkingMemory;Ljava/lang/Object;)Ljava/lang/Object;");
                    mv.visitTypeInsn(CHECKCAST, declarationTypes[i].replace('.', '/'));
                    mv.visitVarInsn(ASTORE, i+6); // obj[i]
                }

                // TODO: @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );

                // @{ruleClassName}.@{methodName}
                StringBuilder consequenceMethodDescr = new StringBuilder("(Lorg/drools/spi/KnowledgeHelper;");
                mv.visitVarInsn(ALOAD, 1); // KnowledgeHelper
                for (int i = 0; i < declarations.length; i++) {
                    mv.visitVarInsn(ALOAD, i+6); // obj[i]
                    mv.visitVarInsn(ALOAD, i+5); // fact[i]
                    consequenceMethodDescr.append("L" + declarationTypes[i].replace('.', '/') + ";Lorg/drools/FactHandle;");
                }
                consequenceMethodDescr.append(")V");
                mv.visitMethodInsn(INVOKESTATIC, internalRuleClassName, methodName, consequenceMethodDescr.toString());
                mv.visitInsn(RETURN);
            }
        });
        return generator.newInstance();
    }
}
