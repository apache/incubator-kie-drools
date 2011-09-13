package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.compiler.*;
import org.drools.lang.descr.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.drools.rule.builder.dialect.asm.InvokerGenerator.createInvokerClassGenerator;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;
import static org.mvel2.asm.Opcodes.*;

public class ASMEvalBuilder extends AbstractASMEvalBuilder {

    protected byte[] createEvalBytecode(final RuleBuildContext ruleContext, final Map vars) {
        final InvokerDataProvider data = new InvokerContext(vars);

        final ClassGenerator generator = createInvokerClassGenerator(data, ruleContext)
                .setInterfaces(EvalExpression.class, CompiledInvoker.class);

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
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(Boolean.TYPE, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new InvokerGenerator.EvaluateMethod() {
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
                mv.visitMethodInsn(INVOKESTATIC, data.getInternalRuleClassName(), data.getMethodName(), evalMethodDescr.toString());
                mv.visitInsn(IRETURN);
            }
        });

        return generator.generateBytecode();
    }
}
