package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.common.*;
import org.drools.compiler.*;
import org.drools.lang.descr.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;
import static org.mvel2.asm.Opcodes.*;

public class ASMEvalBuilder implements RuleConditionBuilder {

    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr) {
        return build(context, descr, null);
    }

    public RuleConditionElement build(RuleBuildContext context, BaseDescr descr, Pattern prefixPattern) {
        // it must be an EvalDescr
        final EvalDescr evalDescr = (EvalDescr) descr;

        final String className = "eval" + context.getNextId();

        evalDescr.setClassMethodName( className );

        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations(context.getRule());

        AnalysisResult analysis = context.getDialect().analyzeExpression( context,
                                                                          evalDescr,
                                                                          evalDescr.getContent(),
                                                                          new BoundIdentifiers( context.getDeclarationResolver().getDeclarationClasses( decls ),
                                                                                                context.getPackageBuilder().getGlobals() ) );
        final BoundIdentifiers usedIdentifiers = analysis.getBoundIdentifiers();

        final Declaration[] declarations = decls.values().toArray( new Declaration[decls.size()]);
        Arrays.sort( declarations, RuleTerminalNode.SortDeclarations.instance  );

        final EvalCondition eval = new EvalCondition( declarations );

        final Map vars = createVariableContext(className,
                                              (String)evalDescr.getContent(),
                                              context,
                                              declarations,
                                              null,
                                              usedIdentifiers.getGlobals());

        generateMethodTemplate("evalMethod", context, vars);

        byte[] bytecode = createEvalBytecode(context, vars);
        registerInvokerBytecode(context, vars, bytecode, eval);
        return eval;
    }

    private byte[] createEvalBytecode(final RuleBuildContext ruleContext, final Map vars) {
        final String packageName = (String)vars.get("package");
        final String invokerClassName = (String)vars.get("invokerClassName");
        final String ruleClassName = (String)vars.get("ruleClassName");
        final String internalRuleClassName = (packageName + "." + ruleClassName).replace(".", "/");
        final String methodName = (String)vars.get("methodName");

        final ClassGenerator generator = new ClassGenerator(packageName + "." + invokerClassName,
                                                            ruleContext.getPackageBuilder().getRootClassLoader(),
                                                            ruleContext.getDialect("java").getPackageRegistry().getTypeResolver())
                .setInterfaces(EvalExpression.class, CompiledInvoker.class);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, new Long(510L))
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                int hashCode = (Integer) vars.get("hashCode");
                push(hashCode);
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                invokeVirtual(Object.class, "getClass", Class.class);
                push(ruleClassName);
                push(packageName);
                push(methodName);
                push(internalRuleClassName + ".class");
                invokeStatic(Rule.class, "getMethodBytecode", List.class, Class.class, String.class, String.class, String.class, String.class);
                mv.visitInsn(ARETURN);

            }
        }).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class),
                new ConsequenceGenerator.EqualsMethod()
        ).addMethod(ACC_PUBLIC, "clone", generator.methodDescr(EvalExpression.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(Boolean.TYPE, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new ConsequenceGenerator.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                final Declaration[] declarations = (Declaration[])vars.get("declarations");
                final String[] declarationTypes = (String[])vars.get("declarationTypes");
                final String[] globals = (String[])vars.get("globals");
                final String[] globalTypes = (String[])vars.get("globalTypes");

                offset = 5;
                int[] declarationsParamsPos = parseDeclarations(declarations, declarationTypes, 2, 1, 3, false);

                // @{ruleClassName}.@{methodName}(@foreach{declarations}, @foreach{globals})
                StringBuilder evalMethodDescr = new StringBuilder("(");
                for (int i = 0; i < declarations.length; i++) {
                    load(declarationsParamsPos[i]); // declarations[i]
                    evalMethodDescr.append(typeDescr(declarationTypes[i]));
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                parseGlobals(globals, globalTypes, 3, evalMethodDescr);

                evalMethodDescr.append(")Z");
                mv.visitMethodInsn(INVOKESTATIC, internalRuleClassName, methodName, evalMethodDescr.toString());
                mv.visitInsn(IRETURN);
            }
        });

        return generator.generateBytecode();
    }
}
