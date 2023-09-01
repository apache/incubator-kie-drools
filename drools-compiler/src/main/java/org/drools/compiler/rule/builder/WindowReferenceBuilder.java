package org.drools.compiler.rule.builder;

import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.WindowReference;

/**
 * A class capable of building window source references
 */
public class WindowReferenceBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr,
                                      Pattern prefixPattern) {
        final WindowReferenceDescr window = (WindowReferenceDescr) descr;

        if ( !context.getPkg().getWindowDeclarations().containsKey( window.getName() ) ) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 descr,
                                                 null,
                                                 "Unknown window " + window.getName()));
        }

        return new WindowReference( window.getName() );
    }

}
