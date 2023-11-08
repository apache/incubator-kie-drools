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
package org.drools.compiler.rule.builder;

import org.drools.base.rule.GroupElement;
import org.drools.base.rule.GroupElementFactory;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;

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
        context.getDeclarationResolver().pushOnBuildStack( ge );

        if ( prefixPattern != null ) {
            ge.addChild( prefixPattern );
        }

        // iterate over child descriptors
        for ( final BaseDescr child : cedescr.getDescrs() ) {
            // gets child to build

            // child.setResource(..) does not seem to be necessary (since builderImpls have already set the resource for all children)
            // but leaving it in here to be save
            child.setResource( context.getRuleDescr().getResource() );
            child.setNamespace( context.getRuleDescr().getNamespace() );

            // gets corresponding builder
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder( child.getClass() );

            if ( builder != null ) {
                final RuleConditionElement element = builder.build( context, child );
                // in case there is a problem with the building,
                // builder will return null. Ex: ClassNotFound for the pattern type
                if ( element != null ) {
                    ge.addChild( element );
                }
            } else {
                throw new RuntimeException( "BUG: no builder found for descriptor class " + child.getClass() );
            }
        }

        context.getDeclarationResolver().popBuildStack();

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
            throw new RuntimeException( "BUG: Not able to create a group element for descriptor: " + descr.getName() );
        }
    }

}
