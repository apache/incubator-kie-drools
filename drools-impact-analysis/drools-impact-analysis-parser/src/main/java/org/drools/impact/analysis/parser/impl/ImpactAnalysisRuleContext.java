package org.drools.impact.analysis.parser.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.util.TypeResolver;

public class ImpactAnalysisRuleContext extends RuleContext {

    private Map<String, Object> bindVariableLiteralMap = new HashMap<>();

    public ImpactAnalysisRuleContext(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, TypeResolver typeResolver, RuleDescr ruleDescr) {
        super(kbuilder, kbuilder, packageModel, typeResolver, ruleDescr);
    }

    public Map<String, Object> getBindVariableLiteralMap() {
        return bindVariableLiteralMap;
    }

    public void setBindVariableLiteralMap(Map<String, Object> bindVariableLiteralMap) {
        this.bindVariableLiteralMap = bindVariableLiteralMap;
    }

}
