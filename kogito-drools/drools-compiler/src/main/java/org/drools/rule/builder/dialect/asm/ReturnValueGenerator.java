package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.common.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.drools.rule.builder.dialect.asm.InvokerGenerator.*;
import static org.mvel2.asm.Opcodes.*;

public class ReturnValueGenerator {
    public static void generate(final ReturnValueStub stub,
                                final Object object,
                                final Tuple tuple,
                                final Declaration[] previousDeclarations,
                                final Declaration[] localDeclarations,
                                final WorkingMemory workingMemory,
                                final Object context) {

        final LeftTuple leftTuple = (LeftTuple)tuple;
        final String[] previousDeclarationTypes = stub.getDeclarationTypes();
        final String[] localDeclarationTypes = stub.getLocalDeclarationTypes();
        final String[] globals = stub.getGlobals();
        final String[] globalTypes = stub.getGlobalTypes();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<InvokerGenerator.DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(previousDeclarationTypes, previousDeclarations, leftTuple);

        final ClassGenerator generator = createInvokerClassGenerator(stub, workingMemory)
                .setInterfaces(ReturnValueExpression.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "replaceDeclaration", generator.methodDescr(null, Declaration.class, Declaration.class)
        ).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(FieldValue.class, Object.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new InvokerGenerator.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                offset = 9;

                int[] previousDeclarationsParamsPos = new int[previousDeclarations.length];

                mv.visitVarInsn(ALOAD, 2);
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 7); // LeftTuple

                for (InvokerGenerator.DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex();
                    previousDeclarationsParamsPos[i] = offset;

                    traverseTuplesUntilDeclaration(i, 3, 7, 8);

                    mv.visitVarInsn(ALOAD, 3);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 5); // workingMemory

                    mv.visitVarInsn(ALOAD, 7);
                    invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class); // tuple.getHandle().getObject()

                    storeObjectFromDeclaration(previousDeclarations[i], previousDeclarationTypes[i]);
                }

                int[] localDeclarationsParamsPos = parseDeclarations(localDeclarations, localDeclarationTypes, 4, 2, 5, false);

                // @{ruleClassName}.@{methodName}(@foreach{previousDeclarations}, @foreach{localDeclarations}, @foreach{globals})
                StringBuilder returnValueMethodDescr = new StringBuilder("(");
                for (int i = 0; i < previousDeclarations.length; i++) {
                    load(previousDeclarationsParamsPos[i]); // previousDeclarations[i]
                    returnValueMethodDescr.append(typeDescr(previousDeclarationTypes[i]));
                }
                for (int i = 0; i < localDeclarations.length; i++) {
                    load(localDeclarationsParamsPos[i]); // localDeclarations[i]
                    returnValueMethodDescr.append(typeDescr(localDeclarationTypes[i]));
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                parseGlobals(globals, globalTypes, 5, returnValueMethodDescr);

                returnValueMethodDescr.append(")Lorg/drools/spi/FieldValue;");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), returnValueMethodDescr.toString());
                mv.visitInsn(ARETURN);
            }
        });
        
        stub.setReturnValue(generator.<ReturnValueExpression>newInstance());
    }
}
