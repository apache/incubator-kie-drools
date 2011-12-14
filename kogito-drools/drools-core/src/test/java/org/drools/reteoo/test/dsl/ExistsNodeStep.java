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

package org.drools.reteoo.test.dsl;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.common.BetaConstraints;
import org.drools.common.DefaultBetaConstraints;
import org.drools.common.DoubleBetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.QuadroupleBetaConstraints;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.reteoo.ExistsNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.MockObjectSource;
import org.drools.reteoo.MockTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.spi.BetaNodeFieldConstraint;

public class ExistsNodeStep
    implements
    Step {

    private ReteTesterHelper reteTesterHelper;

    public ExistsNodeStep(ReteTesterHelper reteTesterHelper) {
        this.reteTesterHelper = reteTesterHelper;
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );

        if ( args.size() != 0 ) {
            String[] a = args.get( 0 );
            String name = a[0];
            String leftInput = a[1];
            String rightInput = a[2];

            Class cls = null;
            
            LeftTupleSource leftTupleSource;
            if ( leftInput.startsWith( "mock" ) ) {
                leftTupleSource = new MockTupleSource( buildContext.getNextId() );                
            } else {
                leftTupleSource = (LeftTupleSource) context.get( leftInput );
            }

            ObjectSource rightObjectSource;
            if ( rightInput.startsWith( "mock" ) ) {
                String type = rightInput.substring( 5, rightInput.length() -1 );                
                try {
                    cls = reteTesterHelper.getTypeResolver().resolveType( type );
                } catch ( ClassNotFoundException e ) {
                    throw new RuntimeException( e );
                }
                rightObjectSource = new MockObjectSource( buildContext.getNextId() );
            } else {
                rightObjectSource = (ObjectSource) context.get( rightInput );
                ObjectSource source = rightObjectSource;
                while ( !( source instanceof ObjectTypeNode ) ) {
                    source = source.getParentObjectSource();
                }
                cls = ((ClassObjectType)((ObjectTypeNode)source).getObjectType()).getClassType();
            }

            List<BetaNodeFieldConstraint> list = new ArrayList<BetaNodeFieldConstraint>();
            for ( int i = 1; i < args.size(); i++ ) {
                a = args.get( i );
                String fieldName = a[0].trim();
                String operator = a[1].trim();
                String var = a[2].trim();

                Declaration declr = (Declaration) context.get( var );

                BetaNodeFieldConstraint betaConstraint;
                try {
                    betaConstraint = this.reteTesterHelper.getBoundVariableConstraint( cls,
                                                                                         fieldName,
                                                                                         declr,
                                                                                         operator );
                    list.add( betaConstraint );
                } catch ( IntrospectionException e ) {
                    throw new IllegalArgumentException();
                }
            }
            
            BetaConstraints constraints;
            switch ( list.size() ) {
                case 0:
                    constraints = new EmptyBetaConstraints();
                    break;
                case 1:
                  constraints = new SingleBetaConstraints( list.get(0),
                                                           buildContext.getRuleBase().getConfiguration() );                    
                  break;
                case 2:
                    constraints = new DoubleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                             buildContext.getRuleBase().getConfiguration() );                    
                    break;                    
                case 3:
                    constraints = new TripleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                             buildContext.getRuleBase().getConfiguration() );                    
                    break;                    
                case 4:
                    constraints = new QuadroupleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                                 buildContext.getRuleBase().getConfiguration() );                    
                    break;                                        
                default:
                    constraints = new DefaultBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                              buildContext.getRuleBase().getConfiguration() );                    
                    break;                                        
                        
            }

            ExistsNode existsNode = new ExistsNode( buildContext.getNextId(),
                                                    leftTupleSource,
                                                    rightObjectSource,
                                                    constraints,
                                                    buildContext );
            existsNode.attach();
            context.put( name,
                         existsNode );

        } else {
            throw new IllegalArgumentException( "Cannot arguments " + args );

        }
    }
}
