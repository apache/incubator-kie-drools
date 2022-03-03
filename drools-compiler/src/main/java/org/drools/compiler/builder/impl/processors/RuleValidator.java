package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.DuplicateRule;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.parser.ParserError;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import java.util.HashSet;
import java.util.Set;

public class RuleValidator extends AbstractPackageCompilationPhase {

    private final KnowledgeBuilderConfiguration configuration;

    public RuleValidator(PackageRegistry pkgRegistry, PackageDescr packageDescr, KnowledgeBuilderConfiguration configuration) {
        super(pkgRegistry, packageDescr);
        this.configuration = configuration;
    }

    public void process() {
        final Set<String> names = new HashSet<>();
        InternalKnowledgePackage pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }
        for (final RuleDescr rule : packageDescr.getRules()) {
            validateRule(packageDescr, rule);

            final String name = rule.getName();
            if (names.contains(name)) {
                this.results.add(new ParserError(rule.getResource(),
                        "Duplicate rule name: " + name,
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
            if (pkg != null) {
                RuleImpl duplicatedRule = pkg.getRule(name);
                if (duplicatedRule != null) {
                    Resource resource = rule.getResource();
                    Resource duplicatedResource = duplicatedRule.getResource();
                    if (resource == null || duplicatedResource == null || duplicatedResource.getSourcePath() == null ||
                            duplicatedResource.getSourcePath().equals(resource.getSourcePath())) {
                        this.results.add(new DuplicateRule(rule,
                                packageDescr,
                                this.configuration));
                    } else {
                        this.results.add(new ParserError(rule.getResource(),
                                "Duplicate rule name: " + name,
                                rule.getLine(),
                                rule.getColumn(),
                                packageDescr.getNamespace()));
                    }
                }
            }
            names.add(name);
        }
    }

    private void validateRule(PackageDescr packageDescr,
                              RuleDescr rule) {
        if (rule.hasErrors()) {
            for (String error : rule.getErrors()) {
                this.results.add(new ParserError(rule.getResource(),
                        error + " in rule " + rule.getName(),
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
        }
    }

}
