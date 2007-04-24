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
import org.drools.rule.Collect;
import org.drools.rule.Pattern;
import org.drools.rule.ConditionalElement;
import org.drools.rule.builder.dialect.java.BuildUtils;

/**
 * @author etirelli
 *
 */
public class CollectBuilder
    implements
    ConditionalElementBuilder {

    /* (non-Javadoc)
     * @see org.drools.semantics.java.builder.ConditionalElementBuilder#build(org.drools.semantics.java.builder.BuildContext, org.drools.semantics.java.builder.BuildUtils, org.drools.semantics.java.builder.PatternBuilder, org.drools.lang.descr.BaseDescr)
     */
    public ConditionalElement build(final BuildContext context,
                                    final BuildUtils utils,
                                    final PatternBuilder patternBuilder,
                                    final BaseDescr descr) {

        final CollectDescr collectDescr = (CollectDescr) descr;

        final Pattern sourcePattern = patternBuilder.build( context,
                                                            utils,
                                                            collectDescr.getSourcePattern() );

        if ( sourcePattern == null ) {
            return null;
        }

        final Pattern resultPattern = patternBuilder.build( context,
                                                            utils,
                                                            collectDescr.getResultPattern() );

        final String className = "collect" + context.getNextId();
        collectDescr.setClassMethodName( className );

        final Collect collect = new Collect( sourcePattern,
                                             resultPattern );
        return collect;
    }

}
