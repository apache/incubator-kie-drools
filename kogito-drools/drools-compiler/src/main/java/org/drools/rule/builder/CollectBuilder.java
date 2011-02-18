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

import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.rule.Collect;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;

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
        
        Pattern resultPattern = (Pattern) context.getBuildStack().peek();

        final Collect collect = new Collect( sourcePattern,
                                             resultPattern );
        return collect;
    }

}
