package org.drools.rule.builder.dialect.asm;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.EvalExpression;
import org.drools.spi.Tuple;
import org.mvel2.asm.MethodVisitor;

import java.util.Map;

import static org.drools.rule.builder.dialect.asm.InvokerGenerator.createInvokerClassGenerator;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ACONST_NULL;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;
import static org.mvel2.asm.Opcodes.IRETURN;

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
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(Boolean.TYPE, Tuple.class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                final Declaration[] declarations = (Declaration[])vars.get("declarations");
                final String[] globals = (String[])vars.get("globals");
                final String[] globalTypes = (String[])vars.get("globalTypes");

                objAstorePos = 5;
                int[] declarationsParamsPos = parseDeclarations(declarations, 2, 1, 3, true);

                // @{ruleClassName}.@{methodName}(@foreach{declarations}, @foreach{globals})
                StringBuilder evalMethodDescr = new StringBuilder("(");
                for (int i = 0; i < declarations.length; i++) {
                    load(declarationsParamsPos[i]); // declarations[i]
                    evalMethodDescr.append(typeDescr(declarations[i].getTypeName()));
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
