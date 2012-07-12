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

import java.util.Iterator;

import org.drools.RuntimeDroolsException;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElementFactory;
import org.drools.rule.Pattern;
import org.drools.rule.RuleConditionElement;

public class GroupElementBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(final RuleBuildContext context,
                                    final BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(final RuleBuildContext context,
                                    final BaseDescr descr,
                                    final Pattern prefixPattern) {
        final ConditionalElementDescr cedescr = (ConditionalElementDescr) descr;

        final GroupElement ge = this.newGroupElementFor( descr );
        context.getBuildStack().push( ge );

        if ( prefixPattern != null ) {
            ge.addChild( prefixPattern );
        }

        // iterate over child descriptors
        for ( final Iterator it = cedescr.getDescrs().iterator(); it.hasNext(); ) {
            // gets child to build
            final BaseDescr child = (BaseDescr) it.next();
            child.setResource(context.getRuleDescr().getResource());
            child.setNamespace(context.getRuleDescr().getNamespace());

            // gets corresponding builder
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( child.getClass() );

            if ( builder != null ) {
                final RuleConditionElement element = builder.build( context,
                                                                    child );
                // in case there is a problem with the building,
                // builder will return null. Ex: ClassNotFound for the pattern type
                if ( element != null ) {
                    ge.addChild( element );
                }
            } else {
                throw new RuntimeDroolsException( "BUG: no builder found for descriptor class " + child.getClass() );
            }

        }

        context.getBuildStack().pop();

        return ge;
    }

    protected GroupElement newGroupElementFor( final BaseDescr baseDescr ) {
        Class descr = baseDescr.getClass();
        if ( AndDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newAndInstance();
        } else if ( OrDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newOrInstance();
        } else if ( NotDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newNotInstance();
        } else if ( ExistsDescr.class.isAssignableFrom( descr ) ) {
            return GroupElementFactory.newExistsInstance();
        } else {
            throw new RuntimeDroolsException( "BUG: Not able to create a group element for descriptor: " + descr.getName() );
        }
    }

}
