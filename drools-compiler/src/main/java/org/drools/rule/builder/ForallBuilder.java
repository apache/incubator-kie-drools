/*
 * Copyright 2006 JBoss Inc
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

package org.drools.rule.builder;

import java.util.Iterator;

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Forall;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;

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
        context.getBuildStack().push( forall );

        for ( final Iterator it = forallDescr.getRemainingPatterns().iterator(); it.hasNext(); ) {
            final Pattern anotherPattern = (Pattern) patternBuilder.build( context,
                                                                           (PatternDescr) it.next() );
            forall.addRemainingPattern( anotherPattern );
        }

        // poping the forall
        context.getBuildStack().pop();

        return forall;
    }

}
