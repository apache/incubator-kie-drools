package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.common.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class EvalGenerator extends InvokerGenerator {

    public static void generate(final EvalStub stub ,
                                final Tuple tuple,
                                final Declaration[] declarations,
                                final WorkingMemory workingMemory,
                                final Object context) {

        final LeftTuple leftTuple = (LeftTuple)tuple;
        final String[] declarationTypes = stub.getDeclarationTypes();
        final String[] globals = stub.getGlobals();
        final String[] globalTypes = stub.getGlobalTypes();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(declarationTypes, declarations, leftTuple);

        final ClassGenerator generator = createInvokerClassGenerator(stub, workingMemory)
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
                offset = 7;

                int[] declarationsParamsPos = new int[declarations.length];

                mv.visitVarInsn(ALOAD, 1);
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 5); // LeftTuple

                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex();
                    declarationsParamsPos[i] = offset;

                    traverseTuplesUntilDeclaration(i, 2, 5, 6);

                    mv.visitVarInsn(ALOAD, 2);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 3); // workingMemory

                    mv.visitVarInsn(ALOAD, 5);
                    invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class); // tuple.getHandle().getObject()

                    storeObjectFromDeclaration(declarations[i], declarationTypes[i]);
                }

                // @{ruleClassName}.@{methodName}(@foreach{declarations}, @foreach{globals})
                StringBuilder evalMethodDescr = new StringBuilder("(");
                for (int i = 0; i < declarations.length; i++) {
                    load(declarationsParamsPos[i]); // declarations[i]
                    evalMethodDescr.append(typeDescr(declarationTypes[i]));
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                parseGlobals(globals, globalTypes, 3, evalMethodDescr);

                evalMethodDescr.append(")Z");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), evalMethodDescr.toString());
                mv.visitInsn(IRETURN);
            }
        });

        stub.setEval(generator.<EvalExpression>newInstance());
    }
}
