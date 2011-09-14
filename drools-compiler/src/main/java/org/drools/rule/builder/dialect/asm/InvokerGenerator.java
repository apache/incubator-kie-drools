package org.drools.rule.builder.dialect.asm;

import org.drools.*;
import org.drools.base.*;
import org.drools.common.*;
import org.drools.core.util.asm.*;
import org.drools.reteoo.*;
import org.drools.rule.*;
import org.drools.rule.Package;
import org.drools.rule.builder.*;
import org.drools.spi.*;
import org.drools.util.*;
import org.mvel2.asm.*;

import java.util.*;

import static org.mvel2.asm.Opcodes.*;

public class InvokerGenerator {

    public static final Long INVOKER_SERIAL_UID = new Long(510L);

    public static ClassGenerator createInvokerStubGenerator(final InvokerDataProvider data, final RuleBuildContext ruleContext) {
        return createStubGenerator(data,
                                   ruleContext.getPackageBuilder().getRootClassLoader(),
                                   ruleContext.getDialect("java").getPackageRegistry().getTypeResolver(),
                                   ruleContext.getPkg().getImports().keySet());
    }

    public static ClassGenerator createStubGenerator(final InvokerDataProvider data,
                                                     final ClassLoader classLoader,
                                                     final TypeResolver typeResolver,
                                                     final Set<String> imports) {
        final ClassGenerator generator = new ClassGenerator(data.getPackageName() + "." + data.getInvokerClassName(),
                                                            classLoader,
                                                            typeResolver);

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, INVOKER_SERIAL_UID)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.hashCode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class), new GetMethodBytecodeMethod(data)
        ).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class), new EqualsMethod()
        ).addMethod(ACC_PUBLIC, "getPackageName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getPackageName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getGeneratedInvokerClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getInvokerClassName() + "Generated");
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getRuleClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getRuleClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getInternalRuleClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getInternalRuleClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getMethodName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getInvokerClassName", generator.methodDescr(String.class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.getInvokerClassName());
                mv.visitInsn(ARETURN);
            }
        }).addMethod(ACC_PUBLIC, "getDeclarationTypes", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getDeclarationTypes());
            }
        }).addMethod(ACC_PUBLIC, "getGlobals", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getGlobals());
            }
        }).addMethod(ACC_PUBLIC, "getGlobalTypes", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(data.getGlobalTypes());
            }
        }).addMethod(ACC_PUBLIC, "getPackageImports", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                returnAsArray(imports, String.class);
            }
        });

        return generator;
    }

    protected static CompositeClassLoader getCompositeClassLoader(Object obj) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        while (true) {
            if (classLoader instanceof CompositeClassLoader) return (CompositeClassLoader)classLoader;
            ClassLoader parentLoader = classLoader.getParent();
            if (parentLoader == null || parentLoader == classLoader) break;
            classLoader = parentLoader;
        }
        return null;
    }

    protected static CompositeClassLoader getCompositeClassLoader(final Object obj, final WorkingMemory workingMemory) {
        CompositeClassLoader classLoader = getCompositeClassLoader(obj); // Try to use the same ClassLoader used for the stub
        if (classLoader == null) classLoader = ((AbstractRuleBase)workingMemory.getRuleBase()).getRootClassLoader();
        return classLoader;
    }

    protected static TypeResolver getTypeResolver(final InvokerStub stub, final WorkingMemory workingMemory, final CompositeClassLoader classLoader) {
        Package pkg = workingMemory.getRuleBase().getPackage(stub.getPackageName());
        TypeResolver typeResolver = pkg == null ? null : pkg.getTypeResolver();
        if (typeResolver == null) {
            Set<String> imports = new HashSet<String>();
            for (String imp : stub.getPackageImports()) imports.add(imp);
            typeResolver = new ClassTypeResolver(imports, classLoader, stub.getPackageName());
        }
        return typeResolver;
    }

    public static ClassGenerator createInvokerClassGenerator(final InvokerStub stub, final WorkingMemory workingMemory) {
        String className = stub.getPackageName() + "." + stub.getGeneratedInvokerClassName();
        CompositeClassLoader classLoader = getCompositeClassLoader(stub, workingMemory);
        return createInvokerClassGenerator(className, stub, classLoader, getTypeResolver(stub, workingMemory, classLoader));
    }

    public static ClassGenerator createInvokerClassGenerator(final InvokerDataProvider data, final RuleBuildContext ruleContext) {
        String className = data.getPackageName() + "." + data.getInvokerClassName();
        return createInvokerClassGenerator(className, data,
                                           ruleContext.getPackageBuilder().getRootClassLoader(),
                                           ruleContext.getDialect("java").getPackageRegistry().getTypeResolver());
    }

    private static ClassGenerator createInvokerClassGenerator(final String className,
                                                              final InvokerDataProvider data,
                                                              final CompositeClassLoader classLoader,
                                                              final TypeResolver typeResolver) {
        final ClassGenerator generator = new ClassGenerator(className, classLoader, typeResolver)
                .addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, INVOKER_SERIAL_UID)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.hashCode());
                mv.visitInsn(IRETURN);
            }
        })
        .addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class), new GetMethodBytecodeMethod(data))
        .addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class), INVOKER_EQUALS_METHOD);

        return generator;
    }

    // DeclarationMatcher

    protected static List<DeclarationMatcher> matchDeclarationsToTuple(String[] declarationTypes, Declaration[] declarations, LeftTuple tuple) {
        List<DeclarationMatcher> matchers = new ArrayList<DeclarationMatcher>();
        for (int i = 0; i < declarations.length; i++)
            matchers.add(new DeclarationMatcher(i, declarations[i].getPattern().getOffset()));
        Collections.sort(matchers);
        return matchers;
    }

    protected static class DeclarationMatcher implements Comparable {
        private final int originalIndex;
        private final int rootDistance;

        public DeclarationMatcher(int originalIndex, int rootDistance) {
            this.originalIndex = originalIndex;
            this.rootDistance = rootDistance;
        }

        public int getOriginalIndex() {
            return originalIndex;
        }

        public int getRootDistance() {
            return rootDistance;
        }

        public int compareTo(Object obj) {
            return ((DeclarationMatcher)obj).rootDistance - rootDistance;
        }
    }

    // Reusable ASM generated methods

    public static class GetMethodBytecodeMethod extends ClassGenerator.MethodBody {

        private InvokerDataProvider data;

        public GetMethodBytecodeMethod(InvokerDataProvider data) {
            this.data = data;
        }

        @Override
        public void body(MethodVisitor mv) {
            mv.visitVarInsn(ALOAD, 0);
            invokeVirtual(Object.class, "getClass", Class.class);
            push(data.getRuleClassName());
            push(data.getPackageName());
            push(data.getMethodName());
            push(data.getInternalRuleClassName() + ".class");
            invokeStatic(Rule.class, "getMethodBytecode", List.class, Class.class, String.class, String.class, String.class, String.class);
            mv.visitInsn(ARETURN);
        }
    }

    public static final ClassGenerator.MethodBody INVOKER_EQUALS_METHOD = new EqualsMethod();

    public static class EqualsMethod extends ClassGenerator.MethodBody {

        @Override
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
    }

    public static abstract class EvaluateMethod extends ClassGenerator.MethodBody {
        protected int offset;

        protected int[] parseDeclarations(Declaration[] declarations, String[] declarationTypes, int declarReg, int tupleReg, int wmReg, boolean readLocalsFromTuple) {
            int[] declarationsParamsPos = new int[declarations.length];
            // DeclarationTypes[i] value[i] = (DeclarationTypes[i])localDeclarations[i].getValue((InternalWorkingMemory)workingMemory, object);
            for (int i = 0; i < declarations.length; i++) {
                declarationsParamsPos[i] = offset;
                mv.visitVarInsn(ALOAD, declarReg); // declarations
                push(i);
                mv.visitInsn(AALOAD);  // declarations[i]
                mv.visitVarInsn(ALOAD, wmReg); // workingMemory
                cast(InternalWorkingMemory.class);
                if (readLocalsFromTuple) {
                    // tuple.get(declarations[i])).getObject()
                    mv.visitVarInsn(ALOAD, tupleReg); // tuple
                    mv.visitVarInsn(ALOAD, declarReg);
                    push(i);
                    mv.visitInsn(AALOAD);  // declarations[i]
                    invokeInterface(Tuple.class, "get", InternalFactHandle.class, Declaration.class);
                    invokeInterface(InternalFactHandle.class, "getObject", Object.class);
                } else {
                    mv.visitVarInsn(ALOAD, 1); // object
                }

                String readMethod = declarations[i].getNativeReadMethod().getName();
                boolean isObject = readMethod.equals("getValue");
                String returnedType = isObject ? "Ljava/lang/Object;" : typeDescr(declarationTypes[i]);
                mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/rule/Declaration", readMethod, "(Lorg/drools/common/InternalWorkingMemory;Ljava/lang/Object;)" + returnedType);
                if (isObject) mv.visitTypeInsn(CHECKCAST, internalName(declarationTypes[i]));
                offset += store(offset, declarationTypes[i]); // obj[i]
            }
            return declarationsParamsPos;
        }

        protected void parseGlobals(String[] globals, String[] globalTypes, int wmReg, StringBuilder methodDescr) {
            for (int i = 0; i < globals.length; i++) {
                mv.visitVarInsn(ALOAD, wmReg); // workingMemory
                push(globals[i]);
                invokeInterface(WorkingMemory.class, "getGlobal", Object.class, String.class);
                mv.visitTypeInsn(CHECKCAST, internalName(globalTypes[i]));
                methodDescr.append(typeDescr(globalTypes[i]));
            }
        }

        protected void traverseTuplesUntilDeclaration(int declarIndex, int declarReg, int tupleReg, int declarOffsetReg) {
            mv.visitVarInsn(ALOAD, declarReg);
            push(declarIndex);
            mv.visitInsn(AALOAD); // declarations[i]
            invokeVirtual(Declaration.class, "getPattern", Pattern.class);
            invokeVirtual(Pattern.class, "getOffset", Integer.TYPE); // declarations[i].getPattern().getOffset()
            mv.visitVarInsn(ISTORE, declarOffsetReg); // declarations[i].getPattern().getOffset()

            // while (tuple.getIndex() > declaration[i].getPattern().getOffset()) tuple = tuple.getParent()
            Label whileStart = new Label();
            Label whileExit = new Label();
            mv.visitLabel(whileStart);
            mv.visitVarInsn(ALOAD, tupleReg);
            invokeInterface(LeftTuple.class, "getIndex", Integer.TYPE); // tuple.getIndex()
            mv.visitVarInsn(ILOAD, declarOffsetReg); // declarations[i].getPattern().getOffset()
            mv.visitJumpInsn(IF_ICMPLE, whileExit); // if tuple.getIndex() <= declarations[i].getPattern().getOffset() jump to whileExit
            mv.visitVarInsn(ALOAD, tupleReg);
            invokeInterface(LeftTuple.class, "getParent", LeftTuple.class);
            mv.visitVarInsn(ASTORE, tupleReg); // tuple = tuple.getParent()
            mv.visitJumpInsn(GOTO, whileStart);
            mv.visitLabel(whileExit);
        }

        protected void storeObjectFromDeclaration(Declaration declaration, String declarationType) {
            String readMethod = declaration.getNativeReadMethod().getName();
            boolean isObject = readMethod.equals("getValue");
            String returnedType = isObject ? "Ljava/lang/Object;" : typeDescr(declarationType);
            mv.visitMethodInsn(INVOKEVIRTUAL, "org/drools/rule/Declaration", readMethod, "(Lorg/drools/common/InternalWorkingMemory;Ljava/lang/Object;)" + returnedType);
            if (isObject) mv.visitTypeInsn(CHECKCAST, internalName(declarationType));
            offset += store(offset, declarationType); // obj[i]
        }
    }
}
