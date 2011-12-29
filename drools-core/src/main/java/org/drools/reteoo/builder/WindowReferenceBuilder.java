/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.builder;

import org.drools.reteoo.WindowNode;
import org.drools.rule.EntryPoint;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.WindowReference;

/**
 * This is a builder for named window references 
 */
public class WindowReferenceBuilder
    implements
    ReteooComponentBuilder {

    /* (non-Javadoc)
     * @see org.drools.reteoo.builder.ReteooComponentBuilder#build(org.drools.reteoo.builder.BuildContext, org.drools.reteoo.builder.BuildUtils, org.drools.rule.RuleConditionElement)
     */
    public void build(BuildContext context,
                      BuildUtils utils,
                      RuleConditionElement rce) {
        final WindowReference window = (WindowReference) rce;
        final WindowNode node = context.getRuleBase().getReteooBuilder().getWindowNode( window.getName() );
        
        context.setObjectSource( node );
        context.setCurrentEntryPoint( node.getEntryPoint() );
     }

    /* (non-Javadoc)
     * @see org.drools.reteoo.builder.ReteooComponentBuilder#requiresLeftActivation(org.drools.reteoo.builder.BuildUtils, org.drools.rule.RuleConditionElement)
     */
    public boolean requiresLeftActivation(BuildUtils utils,
                                          RuleConditionElement rce) {
        return true;
    }

}
