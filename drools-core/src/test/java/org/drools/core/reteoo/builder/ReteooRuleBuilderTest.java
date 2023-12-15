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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.GroupElementFactory;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.consequence.Consequence;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.conf.CompositeBaseConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

public class ReteooRuleBuilderTest {
    private ReteooRuleBuilder builder;
    private KnowledgeBaseImpl rulebase;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        this.builder = new ReteooRuleBuilder();
        rulebase = new KnowledgeBaseImpl("default", (CompositeBaseConfiguration)  RuleBaseFactory.newKnowledgeBaseConfiguration());
    }

    @Test
    public void testAddRuleWithPatterns() {
        final RuleImpl rule = new RuleImpl( "only patterns" );
        final Pattern c1 = new Pattern( 0,
                                new ClassObjectType( String.class ) );
        final Pattern c2 = new Pattern( 1,
                                new ClassObjectType( String.class ) );
        final Pattern c3 = new Pattern( 2,
                                new ClassObjectType( String.class ) );

        final GroupElement lhsroot = GroupElementFactory.newAndInstance();
        lhsroot.addChild( c1 );
        lhsroot.addChild( c2 );
        lhsroot.addChild( c3 );

        rule.setLhs( lhsroot );

        final Consequence consequence = new Consequence<KnowledgeHelper>() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 ValueResolver valueResolver) throws Exception {
                System.out.println( "Consequence!" );
            }

            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }
        };

        rule.setConsequence( consequence );

        final List terminals = this.builder.addRule( rule, this.rulebase, Collections.emptyList() );

        assertThat(terminals.size()).as("Rule must have a single terminal node").isEqualTo(1);

        final RuleTerminalNode terminal = (RuleTerminalNode) terminals.get( 0 );

    }


}
