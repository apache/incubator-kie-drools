/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.JavaRuleClassBuilder;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.spi.DeclarationScopeResolver;
import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;

import static com.github.javaparser.StaticJavaParser.parse;
import static com.github.javaparser.StaticJavaParser.parseBodyDeclaration;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_WITH_EXTERNAL_DECLRS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;

public class LegacyAccumulate {

    private final AccumulateDescr descr;
    private final PatternDescr basePattern;
    private final RuleBuildContext ruleBuildContext;
    private final RuleContext context;
    private final PackageModel packageModel;

    public LegacyAccumulate(RuleContext context, AccumulateDescr descr, PatternDescr basePattern, Set<String> externalDeclrs) {
        this.descr = descr;
        this.basePattern = basePattern;
        this.context = context;
        this.packageModel = context.getPackageModel();

        final DialectCompiletimeRegistry dialectCompiletimeRegistry = packageModel.getDialectCompiletimeRegistry();
        final Dialect defaultDialect = dialectCompiletimeRegistry.getDialect("java");
        final InternalKnowledgePackage pkg = packageModel.getPkg();
        final RuleDescr ruleDescr = context.getRuleDescr();

        ruleBuildContext = new RuleBuildContext(context.getKbuilder(), ruleDescr, dialectCompiletimeRegistry, pkg, defaultDialect);
        ruleBuildContext.setDeclarationResolver( new DelegateDeclarationScopeResolver(ruleBuildContext.getDeclarationResolver(), context, externalDeclrs ) );
        ruleBuildContext.setDialect( defaultDialect ); // enforce java dialect even if the rule declares the mvel one
        for (int i = 0; i < context.getLegacyAccumulateCounter(); i++) {
            ruleBuildContext.getNextId();
        }
    }

    public void build() {
        Pattern pattern = (Pattern) new PatternBuilder().build(ruleBuildContext, basePattern);
        Accumulate accumulate = (Accumulate) pattern.getSource();

        final Set<String> imports = ruleBuildContext.getPkg().getImports().keySet();
        final String packageName = ruleBuildContext.getPkg().getName();

        if ( context.getLegacyAccumulateCounter() == 0 ) {
            GeneratedClassWithPackage generatedClassWithPackage = createAllAccumulateClass( imports, packageName );
            packageModel.addGeneratedAccumulateClasses( generatedClassWithPackage );
        } else {
            for (GeneratedClassWithPackage c : packageModel.getGeneratedAccumulateClasses()) {
                String ruleClassName = StringUtils.ucFirst(context.getRuleDescr().getClassName());
                if (ruleClassName.equals( c.getClassName() )) {
                    for (String m : ruleBuildContext.getMethods()) {
                        c.getGeneratedClass().addMember( parseBodyDeclaration(m) );
                    }
                    break;
                }
            }
        }

        GeneratedClassWithPackage invokerGenerated = createInvokerClass(imports, packageName);
        packageModel.addGeneratedAccumulateClasses(invokerGenerated);

        final String generatedClassName = invokerGenerated.getGeneratedClass().getName().asString();
        String typeWithPackage = String.format("%s.%s", packageName, generatedClassName);

        Expression accExpr = new MethodReferenceExpr(new NameExpr( typeWithPackage ), new NodeList<Type>(), "new");
        MethodCallExpr accFunctionCall = new MethodCallExpr(null, ACC_FUNCTION_CALL, nodeList(accExpr));

        if (accumulate.getRequiredDeclarations().length > 0) {
            accFunctionCall = new MethodCallExpr(accFunctionCall, ACC_WITH_EXTERNAL_DECLRS_CALL);
            for (Declaration requiredDeclaration : accumulate.getRequiredDeclarations()) {
                accFunctionCall.addArgument( context.getVar(requiredDeclaration.getIdentifier()) );
            }
        }

        final String identifier;
        if(basePattern.getIdentifier() != null) {
            identifier = basePattern.getIdentifier();
        } else {
            // As we don't have a bind we need a unique name. Let's use the same as the generated invoker
            identifier = generatedClassName;
            context.addDeclaration(generatedClassName, Object.class);
        }
        Expression bindingVariable = context.getVarExpr(identifier);
        accFunctionCall = new MethodCallExpr(accFunctionCall, BIND_AS_CALL, nodeList(bindingVariable));

        context.addExpression(accFunctionCall);
        context.increaseLegacyAccumulateCounter();
    }

    private GeneratedClassWithPackage createInvokerClass(Set<String> imports, String packageName) {
        final String invokerClass = ruleBuildContext.getInvokers().values().iterator().next();
        final CompilationUnit parsedInvokedClass = parse(invokerClass);

        Set<String> allImports = new HashSet<>();
        allImports.addAll(imports);
        allImports.addAll(parsedInvokedClass
                                  .getImports()
                                  .stream()
                                  .map(importDeclaration -> importDeclaration.getName().toString())
                                  .collect(Collectors.toList()));

        return new GeneratedClassWithPackage(
                (ClassOrInterfaceDeclaration) parsedInvokedClass.getType(0), packageName, allImports, Collections.emptyList()
        );
    }

    private GeneratedClassWithPackage createAllAccumulateClass(Set<String> imports, String packageName) {
        final String allAccumulatesClass = new JavaRuleClassBuilder().buildRule(ruleBuildContext);
        final CompilationUnit parsedAccumulateClass = parse(allAccumulatesClass);
        return new GeneratedClassWithPackage(
                (ClassOrInterfaceDeclaration) parsedAccumulateClass.getType(0), packageName, imports, Collections.emptyList()
        );
    }

    private static class DelegateDeclarationScopeResolver extends DeclarationScopeResolver {
        private final DeclarationScopeResolver delegate;
        private final RuleContext context;
        private final Set<String> externalDeclrs;

        private DelegateDeclarationScopeResolver( DeclarationScopeResolver delegate, RuleContext context, Set<String> externalDeclrs ) {
            this.delegate = delegate;
            this.context = context;
            this.externalDeclrs = externalDeclrs;
        }

        @Override
        public void setRule( RuleImpl rule ) {
            delegate.setRule( rule );
        }

        @Override
        public RuleConditionElement peekBuildStack() {
            return delegate.peekBuildStack();
        }

        @Override
        public RuleConditionElement popBuildStack() {
            return delegate.popBuildStack();
        }

        @Override
        public void pushOnBuildStack( RuleConditionElement element ) {
            delegate.pushOnBuildStack( element );
        }

        @Override
        public Declaration getDeclaration( String identifier ) {
            return delegate.getDeclaration( identifier );
        }

        @Override
        public Class<?> resolveVarType( String identifier ) {
            return delegate.resolveVarType( identifier );
        }

        @Override
        public String normalizeValueForUnit( String value ) {
            return delegate.normalizeValueForUnit( value );
        }

        @Override
        public boolean hasDataSource( String name ) {
            return delegate.hasDataSource( name );
        }

        @Override
        public boolean available( RuleImpl rule, String name ) {
            return delegate.available( rule, name );
        }

        @Override
        public boolean isDuplicated( RuleImpl rule, String name, String type ) {
            return delegate.isDuplicated( rule, name, type );
        }

        @Override
        public Map<String, Declaration> getDeclarations( RuleImpl rule ) {
            Map<String, Declaration> declarationMap = new HashMap<>();
            for (String externalDeclr : externalDeclrs) {
                context.getDeclarationById( externalDeclr ).ifPresent( dSpec -> declarationMap.put(externalDeclr, dSpec.asDeclaration()) );
            }
            declarationMap.putAll( delegate.getDeclarations( rule ) );
            return declarationMap;
        }

        @Override
        public Map<String, Declaration> getDeclarations( RuleImpl rule, String consequenceName ) {
            return delegate.getDeclarations( rule, consequenceName );
        }

        @Override
        public Map<String, Class<?>> getDeclarationClasses( RuleImpl rule ) {
            return delegate.getDeclarationClasses( rule );
        }

        public static Map<String, Class<?>> getDeclarationClasses( Map<String, Declaration> declarations ) {
            return DeclarationScopeResolver.getDeclarationClasses( declarations );
        }

        @Override
        public Pattern findPatternById(int id) {
            return delegate.findPatternById(id);
        }
    }
}
