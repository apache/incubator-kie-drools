package org.drools.modelcompiler.builder.generator.visitor.accumulate;

import java.util.List;

import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.compiler.rule.builder.PatternBuilder;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.dialect.java.JavaAccumulateBuilder;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class LegacyAccumulate {

    private final AccumulateDescr descr;
    private final PatternDescr basePattern;
    private final RuleBuildContext ruleBuildContext;
    private final JavaAccumulateBuilder javaAccumulateBuilder = new JavaAccumulateBuilder();
    private final RuleContext context;

    public LegacyAccumulate(RuleContext context, AccumulateDescr descr, PatternDescr basePattern) {
        this.descr = descr;
        this.basePattern = basePattern;
        this.context = context;

        final PackageModel packageModel = context.getPackageModel();

        final PackageRegistry pkgRegistry = packageModel.getPkgRegistry();
        final DialectCompiletimeRegistry dialectCompiletimeRegistry = pkgRegistry.getDialectCompiletimeRegistry();
        final Dialect defaultDialect = dialectCompiletimeRegistry.getDialect("java");
        final InternalKnowledgePackage pkg = packageModel.getPkg();
        final RuleDescr ruleDescr = context.getRuleDescr();


        ruleBuildContext = new RuleBuildContext(context.getKbuilder(), ruleDescr, dialectCompiletimeRegistry, pkg, defaultDialect);
    }

    public List<String> build() {

        final Pattern pattern = (Pattern) new PatternBuilder().build(ruleBuildContext, basePattern);
        final RuleConditionElement build = javaAccumulateBuilder.build(ruleBuildContext, descr, pattern);

        context.addAccumulateClasses(ruleBuildContext.getMethods());

        return ruleBuildContext.getMethods();
    }
}
