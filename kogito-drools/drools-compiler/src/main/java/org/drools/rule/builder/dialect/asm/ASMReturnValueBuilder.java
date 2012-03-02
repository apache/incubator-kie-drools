package org.drools.rule.builder.dialect.asm;

import org.drools.WorkingMemory;
import org.drools.rule.Declaration;
import org.drools.rule.builder.RuleBuildContext;
import org.drools.spi.CompiledInvoker;
import org.drools.spi.FieldValue;
import org.drools.spi.ReturnValueExpression;
import org.drools.spi.Tuple;
import org.mvel2.asm.MethodVisitor;

import java.util.Map;

import static org.drools.rule.builder.dialect.asm.GeneratorHelper.EvaluateMethod;
import static org.drools.rule.builder.dialect.asm.GeneratorHelper.createInvokerClassGenerator;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ACONST_NULL;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;

public class ASMReturnValueBuilder extends AbstractASMReturnValueBuilder {

    protected byte[] createReturnValueBytecode(final RuleBuildContext ruleContext, final Map vars, final boolean readLocalsFromTuple) {
        final InvokerDataProvider data = new InvokerContext(vars);

        final ClassGenerator generator = InvokerGenerator.createInvokerClassGenerator(data, ruleContext)
                .setInterfaces(ReturnValueExpression.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(FieldValue.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new EvaluateMethod() {
            public void body(MethodVisitor mv) {
                final Declaration[] previousDeclarations = (Declaration[])vars.get("declarations");
                final Declaration[] localDeclarations = (Declaration[])vars.get("localDeclarations");
                final String[] globals = (String[])vars.get("globals");
                final String[] globalTypes = (String[])vars.get("globalTypes");

                objAstorePos = 7;
                int[] previousDeclarationsParamsPos = parseDeclarations(previousDeclarations, 3, 2, 5, true);
                int[] localDeclarationsParamsPos = parseDeclarations(localDeclarations, 4, 2, 5, readLocalsFromTuple);

                // @{ruleClassName}.@{methodName}(@foreach{previousDeclarations}, @foreach{localDeclarations}, @foreach{globals})
                StringBuilder predicateMethodDescr = new StringBuilder("(");
                for (int i = 0; i < previousDeclarations.length; i++) {
                    load(previousDeclarationsParamsPos[i]); // previousDeclarations[i]
                    predicateMethodDescr.append(typeDescr(previousDeclarations[i].getTypeName()));
                }
                for (int i = 0; i < localDeclarations.length; i++) {
                    load(localDeclarationsParamsPos[i]); // localDeclarations[i]
                    predicateMethodDescr.append(typeDescr(localDeclarations[i].getTypeName()));
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                parseGlobals(globals, globalTypes, 5, predicateMethodDescr);

                predicateMethodDescr.append(")Lorg/drools/spi/FieldValue;");
                mv.visitMethodInsn(INVOKESTATIC, data.getInternalRuleClassName(), data.getMethodName(), predicateMethodDescr.toString());
                mv.visitInsn(ARETURN);
            }
        });

        return generator.generateBytecode();
    }
}
