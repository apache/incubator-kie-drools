package org.drools.impact.analysis.parser.impl;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.impact.analysis.model.Package;
import org.drools.impact.analysis.model.Rule;
import org.drools.impact.analysis.parser.internal.ImpactModelBuilderImpl;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PackageParser {

    Logger logger = LoggerFactory.getLogger(PackageParser.class);

    private final ImpactModelBuilderImpl kbuilder;
    private final PackageModel packageModel;
    private final CompositePackageDescr packageDescr;
    private final PackageRegistry pkgRegistry;

    public PackageParser( ImpactModelBuilderImpl kbuilder, PackageModel packageModel, CompositePackageDescr packageDescr, PackageRegistry pkgRegistry ) {
        this.kbuilder = kbuilder;
        this.packageModel = packageModel;
        this.packageDescr = packageDescr;
        this.pkgRegistry = pkgRegistry;
    }

    public Package parse() {
        List<Rule> rules = new ArrayList<>();
        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            rules.add( parseRule( ruleDescr ) );
        }
        return new Package( packageDescr.getName(), rules );
    }

    private Rule parseRule( RuleDescr ruleDescr ) {
        RuleContext context = new ImpactAnalysisRuleContext(kbuilder, packageModel, pkgRegistry.getTypeResolver(), ruleDescr);
        context.addGlobalDeclarations();
        context.setDialectFromAttributes( packageDescr.getAttributes() );
        Rule rule = new Rule( packageDescr.getName(), ruleDescr.getName(), ruleDescr.getResource().getSourcePath() );

        logger.debug("Parsing : " + rule.getName());

        new LhsParser( packageModel, pkgRegistry ).parse( ruleDescr, context, rule );
        new RhsParser( pkgRegistry ).parse( ruleDescr, context, rule );

        return rule;
    }
}
