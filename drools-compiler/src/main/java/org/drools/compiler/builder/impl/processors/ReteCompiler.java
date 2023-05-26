package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.kie.internal.builder.ResourceChange;

import java.util.ArrayList;
import java.util.Collection;

public class ReteCompiler extends AbstractPackageCompilationPhase {
    private final AssetFilter assetFilter;
    private InternalRuleBase kBase;

    public ReteCompiler(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalRuleBase kBase, AssetFilter assetFilter) {
        super(pkgRegistry, packageDescr);
        this.kBase = kBase;
        this.assetFilter = assetFilter;
    }

    @Override
    public void process() {
        if (this.kBase != null) {
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            Collection<RuleImpl> rulesToBeAdded = new ArrayList<>();
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    rulesToBeAdded.add(pkg.getRule(ruleDescr.getName()));
                }
            }
            if (!rulesToBeAdded.isEmpty()) {
                this.kBase.addRules(rulesToBeAdded);
            }
        }
    }

    private boolean filterAccepts(ResourceChange.Type type, String namespace, String name) {
        return assetFilter == null || !AssetFilter.Action.DO_NOTHING.equals(assetFilter.accept(type, namespace, name));
    }

}
