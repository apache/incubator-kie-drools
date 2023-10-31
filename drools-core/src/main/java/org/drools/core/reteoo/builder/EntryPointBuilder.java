/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo.builder;

import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.RuleConditionElement;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.EntryPointNode;

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
        final EntryPointId entry = (EntryPointId) rce;
        context.setCurrentEntryPoint( entry );
        
        EntryPointNode epn = context.getRuleBase().getRete().getEntryPointNode( entry );
        if( epn == null ) {
            NodeFactory nFactory = CoreComponentFactory.get().getNodeFactoryService();
            context.setObjectSource( utils.attachNode( context,
                                                       nFactory.buildEntryPointNode( context.getNextNodeId(),
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
