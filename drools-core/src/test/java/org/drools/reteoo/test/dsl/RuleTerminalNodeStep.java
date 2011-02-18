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

package org.drools.reteoo.test.dsl;

import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;

public class RuleTerminalNodeStep
    implements
    Step {

    public RuleTerminalNodeStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );

        if ( args.size() != 0 ) {
            String[] a = args.get( 0 );
            String name = a[0].trim();
            String leftInput = a[1].trim();

            LeftTupleSource leftTupleSource;
            if ( "mock".equals( leftInput ) ) {
                leftTupleSource = new MockTupleSource( buildContext.getNextId() );
            } else {
                leftTupleSource = (LeftTupleSource) context.get( leftInput );
            }

            Rule rule = new Rule( name );

            final RuleTerminalNode rtn = new RuleTerminalNode( buildContext.getNextId(),
                                                               leftTupleSource,
                                                               rule,
                                                               null,
                                                               buildContext );
            Consequence consequence = new Consequence() {
                public void evaluate(KnowledgeHelper knowledgeHelper,
                                     WorkingMemory workingMemory) throws Exception {

                }
                
                public String getName() {
                    return "default";
                }
            };

            rule.setConsequence( consequence );

            rtn.attach();
            context.put( name,
                         rtn );

        } else {
            throw new IllegalArgumentException( "Cannot arguments " + args );

        }
    }
}