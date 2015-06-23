/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.core.base.TypeResolver;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.builder.dialect.asm.ClassGenerator;
import org.drools.core.rule.builder.dialect.asm.GeneratorHelper;
import org.drools.core.rule.builder.dialect.asm.InvokerDataProvider;
import org.mvel2.asm.MethodVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.mvel2.asm.Opcodes.ACC_FINAL;
import static org.mvel2.asm.Opcodes.ACC_PRIVATE;
import static org.mvel2.asm.Opcodes.ACC_PUBLIC;
import static org.mvel2.asm.Opcodes.ARETURN;
import static org.mvel2.asm.Opcodes.IRETURN;

public class InvokerGenerator {

    public static ClassGenerator createInvokerStubGenerator(final InvokerDataProvider data, final RuleBuildContext ruleContext) {
        return createStubGenerator(data,
                                   ruleContext.getKnowledgeBuilder().getRootClassLoader(),
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

        generator.addStaticField(ACC_PRIVATE + ACC_FINAL, "serialVersionUID", Long.TYPE, GeneratorHelper.INVOKER_SERIAL_UID)
                .addDefaultConstructor();

        generator.addMethod(ACC_PUBLIC, "hashCode", generator.methodDescr(Integer.TYPE), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                push(data.hashCode());
                mv.visitInsn(IRETURN);
            }
        }).addMethod(ACC_PUBLIC, "getMethodBytecode", generator.methodDescr(List.class), new GeneratorHelper.GetMethodBytecodeMethod(data)
        ).addMethod(ACC_PUBLIC, "equals", generator.methodDescr(Boolean.TYPE, Object.class), new GeneratorHelper.EqualsMethod()
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
        }).addMethod(ACC_PUBLIC, "getExpectedDeclarationTypes", generator.methodDescr(String[].class), new ClassGenerator.MethodBody() {
            public void body(MethodVisitor mv) {
                Declaration[] declarations = ( (InvokerContext) data ).getDeclarations();
                List<String> declarationTypes = new ArrayList<String>( declarations.length );
                for ( Declaration decl : declarations ) {
                    declarationTypes.add( decl.getTypeName() );
                }
                returnAsArray( declarationTypes, String.class );
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

    static ClassGenerator createInvokerClassGenerator(final InvokerDataProvider data, final RuleBuildContext ruleContext) {
        String className = data.getPackageName() + "." + data.getInvokerClassName();
        return GeneratorHelper.createInvokerClassGenerator(className, data,
                                           ruleContext.getKnowledgeBuilder().getRootClassLoader(),
                                           ruleContext.getDialect("java").getPackageRegistry().getTypeResolver());
    }
}
