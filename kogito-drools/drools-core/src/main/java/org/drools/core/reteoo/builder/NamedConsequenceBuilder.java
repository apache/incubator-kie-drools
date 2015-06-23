/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.reteoo.builder;

import org.drools.core.ActivationListenerFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.NamedConsequence;
import org.drools.core.rule.RuleConditionElement;
import org.drools.core.time.impl.Timer;

public class NamedConsequenceBuilder implements ReteooComponentBuilder {

    public void build(BuildContext context, BuildUtils utils, RuleConditionElement rce) {
        NamedConsequence namedConsequence = (NamedConsequence) rce;

        Timer timer = context.getRule().getTimer();
        if  ( context.getKnowledgeBase().getConfiguration().isPhreakEnabled() && timer != null ) {
            ReteooComponentBuilder builder = utils.getBuilderFor( Timer.class );
            builder.build( context, utils, context.getRule().getTimer() );
        }

        RuleTerminalNode terminalNode = buildTerminalNodeForNamedConsequence(context, namedConsequence);

        terminalNode.attach(context);

        terminalNode.networkUpdated(new UpdateContext());

        // adds the terminal node to the list of nodes created/added by this sub-rule
        context.getNodes().add( terminalNode );

        if  ( context.getKnowledgeBase().getConfiguration().isPhreakEnabled() && timer != null ) {
            context.setTupleSource( context.getTupleSource().getLeftTupleSource() );
        }

        context.setTerminated(namedConsequence.isTerminal());
    }

    public boolean requiresLeftActivation(BuildUtils utils, RuleConditionElement rce) {
        return false;
    }

    static RuleTerminalNode buildTerminalNodeForNamedConsequence(BuildContext context, NamedConsequence namedConsequence) {
        RuleImpl rule = context.getRule();
        GroupElement subrule = (GroupElement) context.peek();

        ActivationListenerFactory factory = context.getKnowledgeBase().getConfiguration().getActivationListenerFactory( rule.getActivationListener() );
        TerminalNode terminal = factory.createActivationListener( context.getNextId(),
                                                                  context.getTupleSource(),
                                                                  rule,
                                                                  subrule,
                                                                  0, // subruleIndex,
                                                                  context );

        RuleTerminalNode terminalNode = (RuleTerminalNode) terminal;
        ((RuleTerminalNode) terminal).setConsequenceName( namedConsequence.getConsequenceName() );
        return terminalNode;
    }
}
