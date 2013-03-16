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

import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectSource;
import org.drools.core.rule.EntryPoint;
import org.drools.core.rule.RuleConditionElement;

/**
 * This is a builder for the entry point pattern
 * source.
 */
public class EntryPointBuilder
    implements
    ReteooComponentBuilder {

    /* (non-Javadoc)
     * @see org.kie.reteoo.builder.ReteooComponentBuilder#build(org.kie.reteoo.builder.BuildContext, org.kie.reteoo.builder.BuildUtils, org.kie.rule.RuleConditionElement)
     */
    public void build(BuildContext context,
                      BuildUtils utils,
                      RuleConditionElement rce) {
        final EntryPoint entry = (EntryPoint) rce;
        context.setCurrentEntryPoint( entry );
        
        EntryPointNode epn = context.getRuleBase().getRete().getEntryPointNode( entry );
        if( epn == null ) {
            context.setObjectSource( (ObjectSource) utils.attachNode( context,
                                                                      new EntryPointNode( context.getNextId(),
                                                                                          context.getRuleBase().getRete(),
                                                                                          context ) ) );
        } else {
            context.setObjectSource( epn );
        }
     }

    /* (non-Javadoc)
     * @see org.kie.reteoo.builder.ReteooComponentBuilder#requiresLeftActivation(org.kie.reteoo.builder.BuildUtils, org.kie.rule.RuleConditionElement)
     */
    public boolean requiresLeftActivation(BuildUtils utils,
                                          RuleConditionElement rce) {
        return true;
    }

}
