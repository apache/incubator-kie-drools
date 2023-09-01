package org.drools.compiler.rule.builder;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.base.rule.Collect;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;

public class CollectBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                    final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final Pattern prefixPattern) {

        final CollectDescr collectDescr = (CollectDescr) descr;
        final PatternBuilder patternBuilder = (PatternBuilder) context.getDialect().getBuilder( PatternDescr.class );
        final Pattern sourcePattern = (Pattern) patternBuilder.build( context,
                                                                      collectDescr.getInputPattern() );

        if ( sourcePattern == null ) {
            return null;
        }

        final String className = "collect" + context.getNextId();
        collectDescr.setClassMethodName( className );
        
        Pattern resultPattern = (Pattern) context.getDeclarationResolver().peekBuildStack();

        return new Collect( sourcePattern, resultPattern );
    }

}
