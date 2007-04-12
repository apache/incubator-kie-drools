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

package org.drools.reteoo.builder;

import java.util.Iterator;

import org.drools.rule.Column;
import org.drools.rule.Forall;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElementFactory;
import org.drools.rule.RuleConditionElement;

/**
 * The Reteoo component builder for forall CE
 * 
 * @author etirelli
 */
public class ForallBuilder
    implements
    ReteooComponentBuilder {

    /**
     * @inheritDoc
     */
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final RuleConditionElement rce) {
        final Forall forall = (Forall) rce;

        // forall can be translated into
        // not( baseColumn and not( <remaining_columns>+ ) ) 
        // so we just do that:

        final GroupElement and = GroupElementFactory.newAndInstance();
        and.addChild( forall.getBaseColumn() );

        final GroupElement not2 = GroupElementFactory.newNotInstance();
        if ( forall.getRemainingColumns().size() == 1 ) {
            not2.addChild( (Column) forall.getRemainingColumns().get( 0 ) );
            and.addChild( not2 );
        } else if ( forall.getRemainingColumns().size() > 1 ) {
            final GroupElement and2 = GroupElementFactory.newAndInstance();
            for ( final Iterator it = forall.getRemainingColumns().iterator(); it.hasNext(); ) {
                and2.addChild( (Column) it.next() );
            }
            not2.addChild( and2 );
            and.addChild( not2 );
        }

        final GroupElement not = GroupElementFactory.newNotInstance();
        not.addChild( and );

        // get builder for the CEs
        final ReteooComponentBuilder builder = utils.getBuilderFor( not );

        // builds the CEs
        builder.build( context,
                       utils,
                       not );
    }

    /**
     * @inheritDoc
     */
    public boolean requiresLeftActivation(final BuildUtils utils,
                                          final RuleConditionElement rce) {
        return true;
    }

}
