package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.model.Variable;
import org.drools.model.consequences.ConsequenceImpl;
import org.drools.model.functions.ScriptBlock;
import org.drools.model.impl.DeclarationImpl;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.consequence.MVELConsequence;

public class ConsequenceValidation {

    private String packageName;
    private String consequenceString;
    private RuleDescr ruleDescr;
    private List<Variable> variables = new ArrayList<>();

    public ConsequenceValidation(String packageName, String consequenceString, RuleDescr ruleDescr) {
        this.packageName = packageName;
        this.consequenceString = consequenceString;
        this.ruleDescr = ruleDescr;
    }

    public void addVariable(DeclarationSpec d) {
        variables.add(new DeclarationImpl(d.getDeclarationClass(), d.getBindingId()));
    }

    public void validate(ModelBuilderImpl kbuilder) {
        List<DroolsError> droolsErrors = findErrors();
        for (DroolsError error : droolsErrors) {
            kbuilder.addBuilderResult(new InvalidExpressionErrorResult(error.getMessage()));
        }
    }

    private List<DroolsError> findErrors() {
        KnowledgePackageImpl pkg = new KnowledgePackageImpl(packageName);
        ScriptBlock block = new ScriptBlock(Object.class, consequenceString);

        ConsequenceImpl consequence = new ConsequenceImpl(block, variables.toArray(new Variable[0]), true, false, "mvel");
        RuleImpl rule = new RuleImpl(ruleDescr.getName());
        org.drools.modelcompiler.RuleContext context = new org.drools.modelcompiler.RuleContext(null, pkg, rule) {
            @Override
            public Collection<InternalKnowledgePackage> getKnowledgePackages() {
                return Collections.singletonList(pkg);
            }

            @Override
            public ClassLoader getClassLoader() {
                return this.getClass().getClassLoader();
            }
        };

        final List<DroolsError> errors = new ArrayList<>();
        try {
            MVELConsequence mvelConsequence = new MVELConsequence(consequence, context);
            mvelConsequence.init();
        } catch (Exception e) {
            errors.add(new RuleBuildError(null, ruleDescr, null, e.getMessage()));
        }
        return errors;
    }
}
