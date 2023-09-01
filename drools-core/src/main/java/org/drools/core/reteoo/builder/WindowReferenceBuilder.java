package org.drools.core.reteoo.builder;

import org.drools.core.reteoo.WindowNode;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.WindowReference;

/**
 * This is a builder for named window references 
 */
public class WindowReferenceBuilder
    implements
    ReteooComponentBuilder {

    /* (non-Javadoc)
     * @see org.kie.reteoo.builder.ReteooComponentBuilder#build(org.kie.reteoo.builder.BuildContext, org.kie.reteoo.builder.BuildUtils, org.kie.rule.RuleConditionElement)
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
     * @see org.kie.reteoo.builder.ReteooComponentBuilder#requiresLeftActivation(org.kie.reteoo.builder.BuildUtils, org.kie.rule.RuleConditionElement)
     */
    public boolean requiresLeftActivation(BuildUtils utils,
                                          RuleConditionElement rce) {
        return true;
    }

}
