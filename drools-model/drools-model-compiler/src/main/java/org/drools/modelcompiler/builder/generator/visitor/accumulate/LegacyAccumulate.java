package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.java.JavaAccumulateBuilder;
import org.drools.compiler.rule.builder.dialect.java.JavaRuleClassBuilder;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;

import static org.drools.javaparser.ast.NodeList.nodeList;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ACC_FUNCTION_CALL;

public class LegacyAccumulate {

    private final AccumulateDescr descr;
    private final PatternDescr basePattern;
    private final RuleBuildContext ruleBuildContext;
    private final JavaAccumulateBuilder javaAccumulateBuilder = new JavaAccumulateBuilder();
    private final RuleContext context;
    private final PackageModel packageModel;

    public LegacyAccumulate(RuleContext context, AccumulateDescr descr, PatternDescr basePattern) {
        this.descr = descr;
        this.basePattern = basePattern;
        this.context = context;
        this.packageModel = context.getPackageModel();

        final DialectCompiletimeRegistry dialectCompiletimeRegistry = packageModel.getDialectCompiletimeRegistry();
        final Dialect defaultDialect = dialectCompiletimeRegistry.getDialect("java");
        final InternalKnowledgePackage pkg = packageModel.getPkg();
        final RuleDescr ruleDescr = context.getRuleDescr();

        ruleBuildContext = new RuleBuildContext(context.getKbuilder(), ruleDescr, dialectCompiletimeRegistry, pkg, defaultDialect);
    }

    public void build() {

        new PatternBuilder().build(ruleBuildContext, basePattern);

        final Set<String> imports = ruleBuildContext.getPkg().getImports().keySet();
        final String packageName = ruleBuildContext.getPkg().getName();

        GeneratedClassWithPackage generatedClassWithPackage = createAllAccumulateClass(imports, packageName);
        packageModel.addGeneratedAccumulateClasses(generatedClassWithPackage);

        GeneratedClassWithPackage invokerGenerated = createInvokerClass(imports, packageName);
        packageModel.addGeneratedAccumulateClasses(invokerGenerated);

        final String typeWithPackage = String.format("%s.%s", packageName, invokerGenerated.getGeneratedClass().getName().asString());
        final ClassExpr accFunctionName = new ClassExpr(JavaParser.parseType(typeWithPackage));
        final MethodCallExpr accFunctionCall = new MethodCallExpr(null, ACC_FUNCTION_CALL, nodeList(accFunctionName));

        final NameExpr bindingVariable = new NameExpr(toVar(basePattern.getIdentifier()));
        final MethodCallExpr asDSL = new MethodCallExpr(accFunctionCall, "as", nodeList(bindingVariable));

        context.addExpression(asDSL);
    }

    private GeneratedClassWithPackage createInvokerClass(Set<String> imports, String packageName) {
        final String invokerClass = ruleBuildContext.getInvokers().values().iterator().next();
        final CompilationUnit parsedInvokedClass = JavaParser.parse(invokerClass);

        Set<String> allImports = new HashSet<>();
        allImports.addAll(imports);
        allImports.addAll(parsedInvokedClass
                                  .getImports()
                                  .stream()
                                  .map(importDeclaration -> importDeclaration.getName().toString())
                                  .collect(Collectors.toList()));

        return new GeneratedClassWithPackage(
                (ClassOrInterfaceDeclaration) parsedInvokedClass.getType(0), packageName, allImports
        );
    }

    private GeneratedClassWithPackage createAllAccumulateClass(Set<String> imports, String packageName) {
        final String allAccumulatesClass = new JavaRuleClassBuilder().buildRule(ruleBuildContext);
        final CompilationUnit parsedAccumulateClass = JavaParser.parse(allAccumulatesClass);
        return new GeneratedClassWithPackage(
                (ClassOrInterfaceDeclaration) parsedAccumulateClass.getType(0), packageName, imports
        );
    }
}
