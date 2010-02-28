/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.reteoo.test.dsl;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.SingleBetaConstraints;
import org.drools.reteoo.CollectNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.BehaviorManager;
import org.drools.rule.Collect;
import org.drools.rule.Declaration;
import org.drools.rule.Pattern;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.mockito.Mockito;

public class CollectNodeStep
    implements
    Step {

    private ReteTesterHelper reteTesterHelper;

    public CollectNodeStep(ReteTesterHelper reteTesterHelper) {
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
            String returnType = a[3].trim();

            LeftTupleSource leftTupleSource;
            if ( "mock".equals( leftInput ) ) {
                leftTupleSource = Mockito.mock( LeftTupleSource.class );
            } else {
                leftTupleSource = (LeftTupleSource) context.get( leftInput );
            }

            ObjectSource rightObjectSource;
            if ( "mock".equals( rightInput ) ) {
                rightObjectSource = Mockito.mock( ObjectSource.class );;
            } else {
                rightObjectSource = (ObjectSource) context.get( rightInput );
            }

            a = args.get( 1 );
            String fieldName = a[0].trim();
            String operator = a[1].trim();
            String var = a[2].trim();

            Declaration declr = (Declaration) context.get( var );

            Pattern sourcePattern;
            Pattern resultPattern;
            try {
                sourcePattern = reteTesterHelper.getPattern( 0,
                                                             ((ClassObjectType) ((ObjectTypeNode) rightObjectSource).getObjectType()).getClassName() );
                resultPattern = reteTesterHelper.getPattern( buildContext.getNextId(),
                                                             returnType );
            } catch ( Exception e ) {
                throw new IllegalArgumentException();
            }

            BetaNodeFieldConstraint betaConstraint;
            try {
                betaConstraint = this.reteTesterHelper.getBoundVariableConstraint( sourcePattern,
                                                                                   fieldName,
                                                                                   declr,
                                                                                   operator );
            } catch ( IntrospectionException e ) {
                throw new IllegalArgumentException();
            }

            SingleBetaConstraints constraints = new SingleBetaConstraints( betaConstraint,
                                                                           buildContext.getRuleBase().getConfiguration() );

            Collect collect = new Collect( sourcePattern,
                                           resultPattern );

            CollectNode collectNode = new CollectNode( buildContext.getNextId(),
                                                       leftTupleSource,
                                                       rightObjectSource,
                                                       new AlphaNodeFieldConstraint[0],
                                                       constraints,
                                                       new EmptyBetaConstraints(),
                                                       BehaviorManager.NO_BEHAVIORS,
                                                       collect,
                                                       false,
                                                       buildContext );
            collectNode.attach();
            context.put( name,
                         collectNode );

        } else {
            throw new IllegalArgumentException( "Cannot arguments " + args );

        }
    }
}