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

import java.util.List;

import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.WindowNode;
import org.drools.rule.Behavior;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;
import org.drools.rule.WindowDeclaration;
import org.drools.spi.AlphaNodeFieldConstraint;

/**
 * A builder for patterns
 */
public class WindowBuilder implements ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build( final BuildContext context,
                       final BuildUtils utils,
                       final RuleConditionElement rce ) {

        final WindowDeclaration window = (WindowDeclaration) rce;

        final Pattern pattern = window.getPattern();

        final ReteooComponentBuilder builder = utils.getBuilderFor( pattern );

        context.setAttachAlphaNodes( false );

        builder.build( context,
                       utils,
                       pattern );

        context.setAttachAlphaNodes( true );
        
        final List<AlphaNodeFieldConstraint> alphaConstraints = context.getAlphaConstraints();
        final List<Behavior> behaviors = context.getBehaviors();
        
        // build the window node:
        WindowNode wn = new WindowNode( context.getNextId(),
                                        alphaConstraints,
                                        behaviors,
                                        context.getObjectSource(),
                                        context );
        utils.attachNode( context, 
                          wn );
        

    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation( final BuildUtils utils,
                                           final RuleConditionElement rce ) {
        return false;
    }
}
