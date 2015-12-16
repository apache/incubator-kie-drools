/*
 * Copyright 2006 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.rule.builder;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.core.rule.Forall;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;

public class ForallBuilder
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
        final ForallDescr forallDescr = (ForallDescr) descr;

        final PatternBuilder patternBuilder = (PatternBuilder) context.getDialect().getBuilder( PatternDescr.class );
        final Pattern basePattern = (Pattern) patternBuilder.build( context,
                                                                    forallDescr.getBasePattern() );

        if ( basePattern == null ) {
            return null;
        }

        final Forall forall = new Forall( basePattern );

        // adding the newly created forall CE to the build stack
        // this is necessary in case of local declaration usage
        context.getDeclarationResolver().pushOnBuildStack( forall );

        for ( BaseDescr baseDescr : forallDescr.getRemainingPatterns() ) {
            final Pattern anotherPattern = (Pattern) patternBuilder.build( context,
                                                                           (PatternDescr) baseDescr );
            forall.addRemainingPattern( anotherPattern );
        }
        
        
        if ( forallDescr.getDescrs().size() == 1 ) {
            // An optimization for unlinking, where we allow unlinking if the resulting 'not' node has no constraints
            // we need to record this here, due to getRemainingPatterns injecting "this == " + BASE_IDENTIFIER $__forallBaseIdentifier 
            // which we wish to ignore
            PatternDescr p = ( PatternDescr ) forallDescr.getDescrs().get( 0 );
            if ( p.getConstraint().getDescrs().isEmpty() ) {
                forall.setEmptyBetaConstraints( true );
            }
        }        

        // poping the forall
        context.getDeclarationResolver().popBuildStack();

        return forall;
    }

}
