package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.common.*;
import org.drools.core.util.asm.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.builder.*;
import org.drools.rule.builder.dialect.java.*;
import org.drools.spi.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.drools.rule.builder.dialect.DialectUtil.*;
import static org.drools.rule.builder.dialect.java.JavaRuleBuilderHelper.*;
import static org.mvel2.asm.Opcodes.*;

public class ASMConsequenceBuilder implements ConsequenceBuilder {

    private static final Long CONSEQUENCE_SERIAL_UID = new Long(510L);

    public void build(RuleBuildContext context, String consequenceName) {
        // pushing consequence LHS into the stack for variable resolution
        context.getBuildStack().push( context.getRule().getLhs() );

        Map<String, Object> vars = consequenceContext(context, consequenceName);
        if (vars == null) return;
        generateMethodTemplate("consequenceMethod", context, vars);

        byte[] bytecode = createConsequenceBytecode(context, consequenceName, vars);
        writeBytecode(context, vars, bytecode);

        // popping Rule.getLHS() from the build stack
        context.getBuildStack().pop();
    }

    private Map<String, Object> consequenceContext(RuleBuildContext context, String consequenceName) {
        String className = consequenceName + "Consequence";
        Map<String, Declaration> decls = context.getDeclarationResolver().getDeclarations( context.getRule() );
        JavaAnalysisResult analysis = createJavaAnalysisResult(context, consequenceName, decls);

        if ( analysis == null ) {
            // not possible to get the analysis results
            return null;
        }

        // this will fix modify, retract, insert, update, entrypoints and channels
        String fixedConsequence = fixBlockDescr(context, analysis, decls);

        if ( fixedConsequence == null ) {
            // not possible to rewrite the modify blocks
            return null;
        }
        fixedConsequence = KnowledgeHelperFixer.fix( fixedConsequence );

        return createConsequenceContext(context, consequenceName, className, fixedConsequence, decls, analysis.getBoundIdentifiers());
    }

    private void writeBytecode(RuleBuildContext context, Map<String, Object> consequenceContext, byte[] bytecode) {
        String packageName = (String)consequenceContext.get("package");
        String invokerClassName = (String)consequenceContext.get("invokerClassName");
        String className = packageName + "." + invokerClassName;
        String resourceName = className.replace('.', '/') + ".class";

        JavaDialectRuntimeData data = (JavaDialectRuntimeData)context.getPkg().getDialectRuntimeRegistry().getDialectData("java");
        data.write(resourceName, bytecode);
        data.putInvoker(className, context.getRule());
    }

    private byte[] createConsequenceBytecode(RuleBuildContext ruleContext, String consequenceName, final Map<String, Object> consequenceContext) {
        final String packageName = (String)consequenceContext.get("package");
        final String invokerClassName = (String)consequenceContext.get("invokerClassName");
        final String ruleClassName = (String)consequenceContext.get("ruleClassName");
        final String internalRuleClassName = (packageName + "." + ruleClassName).replace(".", "/");
        final String methodName = (String)consequenceContext.get("methodName");
        final String name = (String)consequenceContext.get("consequenceName");
        final Integer hashCode = (Integer)consequenceContext.get("hashCode");
        final Declaration[] declarations = (Declaration[])consequenceContext.get("declarations");
        final String[] declarationTypes = (String[])consequenceContext.get("declarationTypes");
        final String[] globals = (String[])consequenceContext.get("globals");
        final String[] globalTypes = (String[])consequenceContext.get("globalTypes");
        final Boolean[] notPatterns = (Boolean[])consequenceContext.get("notPatterns");

        final ClassGenerator generator = new ClassGenerator(packageName + "." + invokerClassName,
                                                            ruleContext.getPackageBuilder().getRootClassLoader(),
                                                            ruleContext.getDialect("java").getPackageRegistry().getTypeResolver())
                .setInterfaces(Consequence.class, CompiledInvoker.class);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, CONSEQUENCE_SERIAL_UID)
                .addField(ACC_PRIVATE + ACC_FINAL, "consequenceName", String.class);

        generator.addDefaultConstructor(new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0); // read local variable 0 (initialized to this) and push it on the stack
                push(name); // push the String "default" on the stack
                putField("consequenceName", String.class);
            }
        }).addMethod(ACC_PUBLIC, "getName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                mv.visitVarInsn(ALOAD, 0); // read local variable 0 (initialized to this) and push it on the stack
                getField("consequenceName", String.class);
                mv.visitInsn(ARETURN); // return the first object on the stack
            }
        }).addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
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
        }).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Label l1 = new Label();
                Label l2 = new Label();
                mv.visitVarInsn(ALOAD, 1); // if (object == null)
                mv.visitJumpInsn(IFNULL, l1);
                mv.visitVarInsn(ALOAD, 1);
                instanceOf(CompiledInvoker.class);
                mv.visitJumpInsn(IFNE, l2); // if (!(object instanceof  org.drools.spi.CompiledInvoker))
                mv.visitLabel(l1);
                mv.visitInsn(ICONST_0); // return false
                mv.visitInsn(IRETURN);
                mv.visitLabel(l2);
                mv.visitVarInsn(ALOAD, 0);
                invokeThis("getMethodBytecode", List.class);
                mv.visitVarInsn(ALOAD, 1);
                cast(CompiledInvoker.class);
                invokeInterface(CompiledInvoker.class, "getMethodBytecode", List.class);
                invokeStatic(MethodComparator.class, "compareBytecode", Boolean.TYPE, List.class, List.class);
                // return MethodComparator.compareBytecode(getMethodBytecode(), ((CompiledInvoker)object).getMethodBytecode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "evaluate", generator.methodDescr(null, KnowledgeHelper.class, WorkingMemory.class), new String[]{"java/lang/Exception"}, new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                // Tuple tuple = knowledgeHelper.getTuple();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getTuple", Tuple.class);
                mv.visitVarInsn(ASTORE, 3);

                // Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getActivation().getTuple().getLeftTupleSink()).getDeclarations();
                mv.visitVarInsn(ALOAD, 1);
                invokeInterface(KnowledgeHelper.class, "getActivation", Activation.class);
                invokeInterface(Activation.class, "getTuple", LeftTuple.class);
                invokeInterface(LeftTuple.class, "getLeftTupleSink", LeftTupleSink.class);
                cast(RuleTerminalNode.class);
                invokeVirtual(RuleTerminalNode.class, "getDeclarations", Declaration[].class);
                mv.visitVarInsn(ASTORE, 4);

                int[] paramsPos = new int[declarations.length];
                int offset = 5;
                for (int i = 0; i < declarations.length; i++) {
                    int factPos = offset;
                    int objPos = ++offset;
                    paramsPos[i] = factPos;

                    // InternalFactHandle fact[i] = tuple.get(declarations[i]);
                    mv.visitVarInsn(ALOAD, 3); // org.drools.spi.Tuple
                    mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                    push(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    invokeInterface(Tuple.class, "get", InternalFactHandle.class, Declaration.class);
                    mv.visitVarInsn(ASTORE, factPos); // fact[i]

                    // declarations[i].getValue((org.drools.common.InternalWorkingMemory)workingMemory, fact[i].getObject() );
                    mv.visitVarInsn(ALOAD, 4); // org.drools.rule.Declaration[]
                    push(i); // i
                    mv.visitInsn(AALOAD); // declarations[i]
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    cast(InternalWorkingMemory.class);
                    mv.visitVarInsn(ALOAD, factPos); // fact[i]
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class);
                    String readMethod = declarations[i].getNativeReadMethod().getName();
                    boolean isObject = readMethod.equals("getValue");
                    String returnedType = isObject ? "Ljava/lang/Object;" : typeDescr(declarationTypes[i]);
                    mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/rule/Declaration", readMethod, "(Lorg/drools/common/InternalWorkingMemory;Ljava/lang/Object;)" + returnedType);
                    if (isObject) mv.visitTypeInsn(CHECKCAST, interalName(declarationTypes[i]));
                    offset += store(objPos, declarationTypes[i]); // obj[i]

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
                StringBuilder consequenceMethodDescr = new StringBuilder("(Lorg/drools/spi/KnowledgeHelper;");
                mv.visitVarInsn(ALOAD, 1); // KnowledgeHelper
                for (int i = 0; i < declarations.length; i++) {
                    load(paramsPos[i] + 1); // obj[i]
                    mv.visitVarInsn(ALOAD, paramsPos[i]); // fact[i]
                    consequenceMethodDescr.append(typeDescr(declarationTypes[i]) + "Lorg/drools/FactHandle;");
                }

                // @foreach{type : globalTypes, identifier : globals} @{type} @{identifier} = ( @{type} ) workingMemory.getGlobal( "@{identifier}" );
                for (int i = 0; i < globals.length; i++) {
                    mv.visitVarInsn(ALOAD, 2); // WorkingMemory
                    push(globals[i]);
                    invokeInterface(WorkingMemory.class, "getGlobal", Object.class, String.class);
                    mv.visitTypeInsn(CHECKCAST, interalName(globalTypes[i]));
                    consequenceMethodDescr.append(typeDescr(globalTypes[i]));
                }

                consequenceMethodDescr.append(")V");
                mv.visitMethodInsn(INVOKESTATIC, internalRuleClassName, methodName, consequenceMethodDescr.toString());
                mv.visitInsn(RETURN);
            }
        });

        return generator.generateBytecode();
    }
}
