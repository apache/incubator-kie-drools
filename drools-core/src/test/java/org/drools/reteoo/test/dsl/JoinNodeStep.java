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

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Map;

import org.drools.common.SingleBetaConstraints;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.MockObjectSource;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.BehaviorManager;
import org.drools.rule.Declaration;
import org.drools.spi.BetaNodeFieldConstraint;

public class JoinNodeStep
    implements
    Step {

    private ReteTesterHelper reteTesterHelper;

    public JoinNodeStep(ReteTesterHelper reteTesterHelper) {
        this.reteTesterHelper = reteTesterHelper;
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );

        if ( args.size() != 0 ) {
            String[] a = args.get( 0 );
            String name = a[0].trim();
            String leftInput = a[1].trim();
            String rightInput = a[2].trim();

            LeftTupleSource leftTupleSource;
            if ( "mock".equals( leftInput ) ) {
                leftTupleSource = new MockTupleSource( buildContext.getNextId() );
            } else {
                leftTupleSource = (LeftTupleSource) context.get( leftInput );
            }

            ObjectSource rightObjectSource;
            if ( "mock".equals( rightInput ) ) {
                rightObjectSource = new MockObjectSource( buildContext.getNextId() );
            } else {
                rightObjectSource = (ObjectSource) context.get( rightInput );
            }

            a = args.get( 1 );
            String fieldName = a[0].trim();
            String operator = a[1].trim();
            String var = a[2].trim();

            Declaration declr = (Declaration) context.get( var );

            BetaNodeFieldConstraint betaConstraint;
            try {
                betaConstraint = this.reteTesterHelper.getBoundVariableConstraint( declr.getPattern(),
                                                                                   fieldName,
                                                                                   declr,
                                                                                   operator );
            } catch ( IntrospectionException e ) {
                throw new IllegalArgumentException();
            }

            SingleBetaConstraints constraints = new SingleBetaConstraints( betaConstraint,
                                                                           buildContext.getRuleBase().getConfiguration() );

            JoinNode joinNode = new JoinNode( buildContext.getNextId(),
                                              leftTupleSource,
                                              rightObjectSource,
                                              constraints,
                                              BehaviorManager.NO_BEHAVIORS,
                                              buildContext );
            joinNode.attach();
            context.put( name,
                         joinNode );

        } else {
            throw new IllegalArgumentException( "Cannot arguments " + args );

        }
    }
}