package org.drools.core.reteoo.builder;

import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.RuleConditionElement;

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
