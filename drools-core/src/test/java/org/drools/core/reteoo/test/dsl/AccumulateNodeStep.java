/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.test.dsl;

import org.antlr.tool.Rule;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.accumulators.MVELAccumulatorFunctionExecutor;
import org.drools.core.base.accumulators.SumAccumulateFunction;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.DefaultBetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.EmptyBetaConstraints;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.MockObjectSource;
import org.drools.core.reteoo.MockTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.SingleAccumulate;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.runtime.rule.AccumulateFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AccumulateNodeStep
    implements
    Step {

    private ReteTesterHelper reteTesterHelper;

    public AccumulateNodeStep(ReteTesterHelper reteTesterHelper) {
        this.reteTesterHelper = reteTesterHelper;
    }

    public void execute( Map<String, Object> context,
                         List<String[]> args ) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );

        if ( args.size() >= 1 ) {

            // The first argument list is the node parameters
            String[] a = args.get( 0 );
            String name = a[0];
            String leftInput = a[1];
            String rightInput = a[2];
            String sourceType = a[3];
            String expr = a[4];

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

            Pattern sourcePattern;
            Pattern resultPattern;
            try {
                sourcePattern = reteTesterHelper.getPattern( 0,
                                                             sourceType );

                // we always use the accumulate function "sum", so return type is always Number
                resultPattern = reteTesterHelper.getPattern( buildContext.getNextId(),
                                                             Number.class.getName() );
            } catch ( Exception e ) {
                throw new IllegalArgumentException( "Not possible to process arguments: " + Arrays.toString( a ) );
            }

            List<BetaNodeFieldConstraint> list = new ArrayList<BetaNodeFieldConstraint>();
            
            AlphaNodeFieldConstraint[] alphaResultConstraint = new AlphaNodeFieldConstraint[0];
            // the following arguments are constraints
            for ( int i = 1; i < args.size(); i++ ) {
                a = args.get( i );
                String type = a[0];
                String fieldName = a[1];
                String operator = a[2];
                String val = a[3];

                if ( "source".equals( type ) ) {
                    Declaration declr = (Declaration) context.get( val );
                        BetaNodeFieldConstraint sourceBetaConstraint = this.reteTesterHelper.getBoundVariableConstraint( cls,
                                                                                                                         fieldName,
                                                                                                                         declr,
                                                                                                                         operator );
                        list.add( sourceBetaConstraint );
                } else if ( "result".equals( type ) ) {
                    alphaResultConstraint = new AlphaNodeFieldConstraint[1];
                        alphaResultConstraint[0] = this.reteTesterHelper.getLiteralConstraint( resultPattern,
                                                                                               fieldName,
                                                                                               operator,
                                                                                               val );
                }
            }
            
            BetaConstraints betaSourceConstraints;
            switch ( list.size() ) {
                case 0:
                    betaSourceConstraints = new EmptyBetaConstraints();
                    break;
                case 1:
                    betaSourceConstraints = new SingleBetaConstraints( list.get(0),
                                                           buildContext.getKnowledgeBase().getConfiguration() );
                  break;
                case 2:
                    betaSourceConstraints = new DoubleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                             buildContext.getKnowledgeBase().getConfiguration() );
                    break;                    
                case 3:
                    betaSourceConstraints = new TripleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                             buildContext.getKnowledgeBase().getConfiguration() );
                    break;                    
                case 4:
                    betaSourceConstraints = new QuadroupleBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                                 buildContext.getKnowledgeBase().getConfiguration() );
                    break;                                        
                default:
                    betaSourceConstraints = new DefaultBetaConstraints( list.toArray( new BetaNodeFieldConstraint[2] ),
                                                              buildContext.getKnowledgeBase().getConfiguration() );
                    break;                                        
                        
            }            

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) buildContext.getKnowledgeBase().getPackage( buildContext.getRule().getPackageName() ).getDialectRuntimeRegistry().getDialectData( "mvel" );
            data.onAdd( null, buildContext.getKnowledgeBase().getRootClassLoader() );
            //MvelD data = (MVELDialectRuntimeData) buildContext.getRuleBase().getPackage( buildContext.getRule().getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" );
            
            NodeTestCase testCase = (NodeTestCase) context.get( "TestCase" );

            try {
                for ( String imp : testCase.getImports() ) {
                    if ( imp.endsWith( ".*" ) ) {
                        data.addPackageImport( imp.substring( 0,
                                                imp.lastIndexOf( '.' ) ) );
                    } else {
                        //classImports.add( imp );
                        cls = data.getRootClassLoader().loadClass( imp ) ;
                        data.addImport( cls.getSimpleName(),  cls);
                    }
                }  
            } catch (Exception e) {
                throw new RuntimeException("Unable to load class",e );
            }

            Declaration decl = (Declaration) context.get( expr );
            // build an external function executor
            MVELCompilationUnit compilationUnit = new MVELCompilationUnit( name,
                                                                           expr,
                                                                           new String[]{}, // global identifiers
                                                                           new EvaluatorWrapper[]{}, // operator identifiers
                                                                           new Declaration[]{}, // previous declarations
                                                                           new Declaration[]{decl}, // local declarations
                                                                           new String[]{}, // other identifiers
                                                                           new String[]{"this", "drools", "kcontext", "rule", decl.getIdentifier()}, // input identifiers
                                                                           new String[]{Object.class.getName(), KnowledgeHelper.class.getName(), KnowledgeHelper.class.getName(), Rule.class.getName(), decl.getValueType().getClassType().getName()}, // input types
                                                                           4,
                                                                           false,
                                                                           false );

            AccumulateFunction accFunction = new SumAccumulateFunction();

            Accumulator accumulator = new MVELAccumulatorFunctionExecutor( compilationUnit,
                                                                           accFunction );
            ((MVELCompileable) accumulator).compile( data );

            Accumulate accumulate = new SingleAccumulate( sourcePattern,
                                                          new Declaration[]{}, // required declaration
                                                          accumulator );
            AccumulateNode accNode = new AccumulateNode( buildContext.getNextId(),
                                                         leftTupleSource,
                                                         rightObjectSource,
                                                         alphaResultConstraint,
                                                         betaSourceConstraints,
                                                         new EmptyBetaConstraints(),
                                                         accumulate,
                                                         false,
                                                         buildContext );

            accNode.attach(buildContext);
            context.put( name,
                         accNode );

        } else {
            StringBuilder msgBuilder = new StringBuilder();
            msgBuilder.append( "Can not parse AccumulateNode step arguments: \n" );
            for ( String[] arg : args ) {
                msgBuilder.append( "    " );
                msgBuilder.append( Arrays.toString( arg ) );
                msgBuilder.append( "\n" );
            }
            throw new IllegalArgumentException( msgBuilder.toString() );
        }
    }

}
