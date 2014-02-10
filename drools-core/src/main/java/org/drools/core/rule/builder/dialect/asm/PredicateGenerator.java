package org.drools.core.rule.builder.dialect.asm;

import org.drools.core.WorkingMemory;
import org.drools.core.rule.builder.dialect.asm.GeneratorHelper.DeclarationMatcher;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.PredicateExpression;
import org.drools.core.spi.Tuple;
import org.mvel2.asm.MethodVisitor;

import java.util.List;

import static org.drools.core.rule.builder.dialect.asm.GeneratorHelper.createInvokerClassGenerator;
import static org.drools.core.rule.builder.dialect.asm.GeneratorHelper.matchDeclarationsToTuple;

import static org.mvel2.asm.Opcodes.AALOAD;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ACONST_NULL;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;
import static org.mvel2.asm.Opcodes.IRETURN;

public class PredicateGenerator {
    public static void generate(final PredicateStub stub,
                                final Tuple tuple,
                                final Declaration[] previousDeclarations,
                                final Declaration[] localDeclarations,
                                final WorkingMemory workingMemory) {

        final LeftTuple leftTuple = (LeftTuple)tuple;
        final String[] globals = stub.getGlobals();
        final String[] globalTypes = stub.getGlobalTypes();

        // Sort declarations based on their offset, so it can ascend the tuple's parents stack only once
        final List<DeclarationMatcher> declarationMatchers = matchDeclarationsToTuple(previousDeclarations);

        final ClassGenerator generator = createInvokerClassGenerator(stub, workingMemory)
                .setInterfaces(PredicateExpression.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "createContext", generator.methodDescr(Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitInsn(ACONST_NULL);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(Boolean.TYPE, InternalFactHandle.class, Tuple.class, Declaration[].class, Declaration[].class, WorkingMemory.class, Object.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                objAstorePos = 9;

                int[] previousDeclarationsParamsPos = new int[previousDeclarations.length];

                mv.visitVarInsn( ALOAD, 1 );
                invokeInterface( InternalFactHandle.class, "getObject", Object.class );
                mv.visitVarInsn( ASTORE, 1 );


                mv.visitVarInsn( ALOAD, 2 );
                cast(LeftTuple.class);
                mv.visitVarInsn(ASTORE, 7); // LeftTuple

                LeftTuple currentLeftTuple = leftTuple;                
                for (DeclarationMatcher matcher : declarationMatchers) {
                    int i = matcher.getOriginalIndex();
                    previousDeclarationsParamsPos[i] = objAstorePos;

                    currentLeftTuple = traverseTuplesUntilDeclaration(currentLeftTuple, matcher.getRootDistance(), 7);

                    mv.visitVarInsn(ALOAD, 3);
                    push(i);
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 5); // workingMemory

                    mv.visitVarInsn(ALOAD, 7);
                    invokeInterface(LeftTuple.class, "getHandle", InternalFactHandle.class);
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class); // tuple.getHandle().getObject()

                    storeObjectFromDeclaration(previousDeclarations[i], previousDeclarations[i].getTypeName());
                }

                int[] localDeclarationsParamsPos = parseDeclarations(localDeclarations, 4, 2, 5, false);

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

                predicateMethodDescr.append(")Z");
                mv.visitMethodInsn(INVOKESTATIC, stub.getInternalRuleClassName(), stub.getMethodName(), predicateMethodDescr.toString());
                mv.visitInsn(IRETURN);
            }
        });

        stub.setPredicate(generator.<PredicateExpression>newInstance());
    }
}
