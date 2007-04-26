package org.drools.rule.builder;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.drools.RuntimeDroolsException;
import org.drools.base.evaluators.DateFactory;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

/**
 * This builds the rule structure from an AST.
 * Generates semantic code where necessary if semantics are used.
 * This is an internal API.
 */
public class RuleBuilder {

    // Constructor
    public RuleBuilder() {
    }

    /**
     * Build the give rule into the 
     * @param pkg
     * @param ruleDescr
     * @return
     */
    public void build(final RuleBuildContext context) {
        RuleDescr ruleDescr = context.getRuleDescr();

        final ConditionalElementBuilder builder = (ConditionalElementBuilder) context.getDialect().getBuilder( ruleDescr.getLhs().getClass() );
        if ( builder != null ) {
            final GroupElement ce = (GroupElement) builder.build( context,
                                                                  ruleDescr.getLhs() );
            context.getRule().setLhs( ce );
        } else {
            throw new RuntimeDroolsException( "BUG: builder not found for descriptor class " + ruleDescr.getLhs().getClass() );
        }

        // Build the consequence and generate it's invoker/s
        // generate the main rule from the previously generated s.
        if ( !(ruleDescr instanceof QueryDescr) ) {
            // do not build the consequence if we have a query

            context.getDialect().getConsequenceBuilder().build( context,
                                                                ruleDescr );
        }
        context.getDialect().getRuleClassBuilder().buildRule( context,
                                                              ruleDescr );
    }
}