/**
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

package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBaseConfiguration;
import org.drools.base.SalienceInteger;
import org.drools.rule.Rule;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

public class ReteooBuilderTest {

    @Test
    public void testOrder() {
        //ReteooBuilder
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        conf.setSequential( true );
        ReteooRuleBase ruleBase = new ReteooRuleBase( conf );
        ReteooBuilder builder = new ReteooBuilder( ruleBase );


        final Consequence consequence = new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper,
                                 WorkingMemory workingMemory) throws Exception {
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

        Rule rule0 = new Rule( "rule0" );
        rule0.setAgendaGroup( "group 0" );
        rule0.setConsequence( consequence );
        builder.addRule( rule0 );

        Rule rule1 = new Rule( "rule1" );
        rule1.setAgendaGroup( "group 0" );
        rule1.setConsequence( consequence );
        builder.addRule( rule1 );

        Rule rule2 = new Rule( "rule2" );
        rule2.setAgendaGroup( "group 1" );
        rule2.setConsequence( consequence );
        builder.addRule( rule2 );

        Rule rule3 = new Rule( "rule3" );
        rule3.setAgendaGroup( "group 0" );
        rule3.setConsequence( consequence );
        builder.addRule( rule3 );

        Rule rule4 = new Rule( "rule4" );
        rule4.setAgendaGroup( "group 2" );
        rule4.setConsequence( consequence );
        builder.addRule( rule4 );

        Rule rule5 = new Rule( "rule5" );
        rule5.setAgendaGroup( "group 1" );
        rule5.setConsequence( consequence );
        builder.addRule( rule5 );

        Rule rule6 = new Rule( "rule6" );
        rule6.setSalience( new SalienceInteger( 5 ) );
        rule6.setAgendaGroup( "group 2" );
        rule6.setConsequence( consequence );
        builder.addRule( rule6 );

        Rule rule7 = new Rule( "rule7" );
        rule7.setAgendaGroup( "group 0" );
        rule7.setConsequence( consequence );
        builder.addRule( rule7 );

        Rule rule8 = new Rule( "rule8" );
        rule8.setSalience( new SalienceInteger( 10 ) );
        rule8.setAgendaGroup( "group 2" );
        rule8.setConsequence( consequence );
        builder.addRule( rule8 );

        builder.order();

        assertEquals( 0, ((RuleTerminalNode) builder.getTerminalNodes( rule0 )[0]).getSequence() );
        assertEquals( 1, ((RuleTerminalNode) builder.getTerminalNodes( rule1 )[0]).getSequence() );
        assertEquals( 0, ((RuleTerminalNode) builder.getTerminalNodes( rule2 )[0]).getSequence() );
        assertEquals( 2, ((RuleTerminalNode) builder.getTerminalNodes( rule3 )[0]).getSequence() );
        assertEquals( 2, ((RuleTerminalNode) builder.getTerminalNodes( rule4 )[0]).getSequence() );
        assertEquals( 1, ((RuleTerminalNode) builder.getTerminalNodes( rule5 )[0]).getSequence() );
        assertEquals( 1, ((RuleTerminalNode) builder.getTerminalNodes( rule6 )[0]).getSequence() );
        assertEquals( 3, ((RuleTerminalNode) builder.getTerminalNodes( rule7 )[0]).getSequence() );
        assertEquals( 0, ((RuleTerminalNode) builder.getTerminalNodes( rule8 )[0]).getSequence() );

    }
}
