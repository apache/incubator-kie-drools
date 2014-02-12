package org.drools.compiler.rule.builder.dialect.asm;

import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.WorkingMemory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.rule.builder.dialect.asm.ClassGenerator;
import org.drools.core.rule.builder.dialect.asm.GeneratorHelper;
import org.drools.core.rule.builder.dialect.asm.InvokerDataProvider;
import org.drools.core.spi.Activation;
import org.drools.core.spi.CompiledInvoker;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.mvel2.asm.MethodVisitor;

import java.util.Map;

import static org.mvel2.asm.Opcodes.AALOAD;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ALOAD;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.ASTORE;
import static org.mvel2.asm.Opcodes.CHECKCAST;
import static org.mvel2.asm.Opcodes.INVOKESTATIC;
import static org.mvel2.asm.Opcodes.INVOKEVIRTUAL;
import static org.mvel2.asm.Opcodes.RETURN;

public class ASMConsequenceBuilder extends AbstractASMConsequenceBuilder {

    protected byte[] createConsequenceBytecode(RuleBuildContext ruleContext, final Map<String, Object> consequenceContext) {
        final InvokerDataProvider data = new InvokerContext(consequenceContext);
        final String name = (String)consequenceContext.get("consequenceName");
        final Declaration[] declarations = (Declaration[])consequenceContext.get("declarations");

        final ClassGenerator generator = InvokerGenerator.createInvokerClassGenerator(data, ruleContext)
                .setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(name);
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new GeneratorHelper.EvaluateMethod() {
            public void body(MethodVisitor mv) {
                // Tuple tuple = knowledgeHelper.getTuple();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getTuple", Tuple.class);
                mv.visitVarInsn(ASTORE, 3);

                // Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getMatch().getTuple().getLeftTupleSink()).getDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getMatch", Activation.class);
                invokeInterface(Activation.class, "getTuple", LeftTuple.class);
                invokeInterface(LeftTuple.class, "getLeftTupleSink", LeftTupleSink.class);
                cast(RuleTerminalNode.class);
                invokeVirtual(RuleTerminalNode.class, "getDeclarations", Declaration[].class);
                mv.visitVarInsn(ASTORE, 4);

                final String[] globals = data.getGlobals();
                final String[] globalTypes = data.getGlobalTypes();
                final Boolean[] notPatterns = (Boolean[])consequenceContext.get("notPatterns");

                int[] paramsPos = new int[declarations.length];
                int offset = 5;
                for (int i = 0; i < declarations.length; i++) {
                    int factPos = offset;
                    int objPos = ++offset;
                    paramsPos[i] = factPos;

                    // InternalFactHandle fact[i] = tuple.get(declarations[i]);
                    mv.visitVarInsn(ALOAD, 3); // org.kie.spi.Tuple
                    mv.visitVarInsn(ALOAD, 4); // org.kie.rule.Declaration[]
                    push(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    invokeInterface(Tuple.class, "get", InternalFactHandle.class, Declaration.class);
                    mv.visitVarInsn(ASTORE, factPos); // fact[i]

                    // declarations[i].getValue((org.kie.common.InternalWorkingMemory)workingMemory, fact[i].getObject() );
                    mv.visitVarInsn(ALOAD, 4); // org.kie.rule.Declaration[]
                    push(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    cast(InternalWorkingMemory.class);
                    mv.visitVarInsn(ALOAD, factPos); // fact[i]
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class);
                    String readMethod = declarations[i].getNativeReadMethodName();
                    boolean isObject = readMethod.equals("getValue");
                    String returnedType = isObject ? "Ljava/lang/Object;" : typeDescr(declarations[i].getTypeName());
                    mv.visitMethodInsn(INVOKEVIRTUAL, Declaration.class.getName().replace('.', '/'), readMethod,
                                       "(L" + InternalWorkingMemory.class.getName().replace('.', '/')+";Ljava/lang/Object;)" + returnedType);
                    if (isObject) mv.visitTypeInsn(CHECKCAST, internalName(declarations[i].getTypeName()));
                    offset += store(objPos, declarations[i].getTypeName()); // obj[i]

                    if (notPatterns[i]) {
                        mv.visitVarInsn(ALOAD, 1);
                        invokeInterface(KnowledgeHelper.class, "getWorkingMemory", WorkingMemory.class);
                        loadAsObject(objPos);
                        invokeInterface(WorkingMemory.class, "getFactHandle", FactHandle.class, Object.class);
                        cast(InternalFactHandle.class);
                        mv.visitVarInsn(ASTORE, factPos);
                    }
                }

                // @{ruleClassName}.@{methodName}(KnowledgeHelper, @foreach{declr : declarations} Object, FactHandle @end)
                StringBuilder consequenceMethodDescr = new StringBuilder("(L" + KnowledgeHelper.class.getName().replace('.', '/')+ ";");
                mv.visitVarInsn(ALOAD, 1); // KnowledgeHelper
                for (int i = 0; i < declarations.length; i++) {
                    load(paramsPos[i] + 1); // obj[i]
                    mv.visitVarInsn(ALOAD, paramsPos[i]); // fact[i]
                    consequenceMethodDescr.append(typeDescr(declarations[i].getTypeName())).append("L" + FactHandle.class.getName().replace('.', '/') + ";" );
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                for (int i = 0; i < globals.length; i++) {
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    push(globals[i]);
                    invokeInterface(WorkingMemory.class, "getGlobal", Object.class, String.class);
                    mv.visitTypeInsn(CHECKCAST, internalName(globalTypes[i]));
                    consequenceMethodDescr.append(typeDescr(globalTypes[i]));
                }

                consequenceMethodDescr.append(")V");
                mv.visitMethodInsn(INVOKESTATIC, data.getInternalRuleClassName(), data.getMethodName(), consequenceMethodDescr.toString());
                mv.visitInsn(RETURN);
            }
        });

        return generator.generateBytecode();
    }
}
