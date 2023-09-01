package org.drools.compiler.builder.impl.errors;

import org.drools.drl.parser.DroolsError;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.base.definitions.rule.impl.RuleImpl;

public class RuleErrorHandler extends ErrorHandler {

    private BaseDescr descr;

    private RuleImpl rule;

    public RuleErrorHandler(final BaseDescr ruleDescr,
                            final RuleImpl rule,
                            final String message) {
        this.descr = ruleDescr;
        this.rule = rule;
        this.message = message;
    }

    public DroolsError getError() {
        return new RuleBuildError(this.rule,
                                  this.descr,
                                  collectCompilerProblems(),
                                  this.message);
    }

}
