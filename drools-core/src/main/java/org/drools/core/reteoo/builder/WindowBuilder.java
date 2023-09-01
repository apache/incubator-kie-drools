package org.drools.core.reteoo.builder;

import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.WindowDeclaration;

/**
 * A builder for patterns
 */
public class WindowBuilder {
    
    public static final WindowBuilder INSTANCE = new WindowBuilder();

    /**
     * @inheritDoc
     */
    public void build( final BuildContext context,
                       final BuildUtils utils,
                       final WindowDeclaration window ) {

        final Pattern pattern = window.getPattern();

        final ReteooComponentBuilder builder = utils.getBuilderFor( pattern );

        context.setAttachPQN( false );
        builder.build( context,
                       utils,
                       pattern );
        context.setAttachPQN( true );
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation( final BuildUtils utils,
                                           final RuleConditionElement rce ) {
        return false;
    }
}
