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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.antlr.tool.Rule;
import org.drools.base.EvaluatorWrapper;
import org.drools.base.accumulators.MVELAccumulatorFunctionExecutor;
import org.drools.base.accumulators.SumAccumulateFunction;
import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.base.mvel.MVELCompileable;
import org.drools.common.BetaConstraints;
import org.drools.common.EmptyBetaConstraints;
import org.drools.common.SingleBetaConstraints;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ObjectSource;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Accumulate;
import org.drools.rule.Behavior;
import org.drools.rule.Declaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Pattern;
import org.drools.runtime.rule.AccumulateFunction;
import org.drools.spi.Accumulator;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.KnowledgeHelper;
import org.mockito.Mockito;

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

            LeftTupleSource leftTupleSource;
            if ( "mock".equals( leftInput ) ) {
                leftTupleSource = Mockito.mock( LeftTupleSource.class );
            } else {
                leftTupleSource = (LeftTupleSource) context.get( leftInput );
            }

            ObjectSource rightObjectSource;
            if ( "mock".equals( rightInput ) ) {
                rightObjectSource = Mockito.mock( ObjectSource.class );
            } else {
                rightObjectSource = (ObjectSource) context.get( rightInput );
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

            BetaConstraints betaSourceConstraints = new EmptyBetaConstraints();
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
                    try {
                        BetaNodeFieldConstraint sourceBetaConstraint = this.reteTesterHelper.getBoundVariableConstraint( sourcePattern,
                                                                                                                         fieldName,
                                                                                                                         declr,
                                                                                                                         operator );
                        betaSourceConstraints = new SingleBetaConstraints( sourceBetaConstraint,
                                                                           buildContext.getRuleBase().getConfiguration() );
                    } catch ( IntrospectionException e ) {
                        throw new IllegalArgumentException();
                    }
                } else if ( "result".equals( type ) ) {
                    alphaResultConstraint = new AlphaNodeFieldConstraint[1];
                    try {
                        alphaResultConstraint[0] = this.reteTesterHelper.getLiteralConstraint( resultPattern,
                                                                                               fieldName,
                                                                                               operator,
                                                                                               val );
                    } catch ( IntrospectionException e ) {
                        throw new IllegalArgumentException( "Unable to configure alpha constraint: " + Arrays.toString( a ),
                                                            e );
                    }
                }
            }

            NodeTestCase testCase = (NodeTestCase) context.get( "TestCase" );
            List<String> classImports = new ArrayList<String>();
            List<String> pkgImports = new ArrayList<String>();
            for ( String imp : testCase.getImports() ) {
                if ( imp.endsWith( ".*" ) ) {
                    pkgImports.add( imp.substring( 0,
                                                   imp.lastIndexOf( '.' ) ) );
                } else {
                    classImports.add( imp );
                }

            }

            Declaration decl = (Declaration) context.get( expr );
            // build an external function executor
            MVELCompilationUnit compilationUnit = new MVELCompilationUnit( name,
                                                                           expr,
                                                                           pkgImports.toArray( new String[0] ), // pkg imports
                                                                           classImports.toArray( new String[0] ), // imported classes
                                                                           new String[]{}, // imported methods
                                                                           new String[]{}, // imported fields
                                                                           new String[]{}, // global identifiers
                                                                           new EvaluatorWrapper[]{}, // operator identifiers
                                                                           new Declaration[]{}, // previous declarations
                                                                           new Declaration[]{decl}, // local declarations
                                                                           new String[]{}, // other identifiers
                                                                           new String[]{"this", "drools", "kcontext", "rule", decl.getIdentifier()}, // input identifiers
                                                                           new String[]{Object.class.getName(), KnowledgeHelper.class.getName(), KnowledgeHelper.class.getName(), Rule.class.getName(), decl.getValueType().getClassType().getName()}, // input types
                                                                           4,
                                                                           false );

            AccumulateFunction accFunction = new SumAccumulateFunction();

            Accumulator accumulator = new MVELAccumulatorFunctionExecutor( compilationUnit,
                                                                           accFunction );
            ((MVELCompileable) accumulator).compile(  (MVELDialectRuntimeData) buildContext.getRuleBase().getPackage( buildContext.getRule().getName() ).getDialectRuntimeRegistry().getDialectData( "mvel" ) );

            Accumulate accumulate = new Accumulate( sourcePattern,
                                                    new Declaration[]{}, // required declaration
                                                    new Declaration[]{}, // inner declarations
                                                    new Accumulator[]{accumulator},
                                                    false );
            AccumulateNode accNode = new AccumulateNode( buildContext.getNextId(),
                                                         leftTupleSource,
                                                         rightObjectSource,
                                                         alphaResultConstraint,
                                                         betaSourceConstraints,
                                                         new EmptyBetaConstraints(),
                                                         new Behavior[]{},
                                                         accumulate,
                                                         false,
                                                         buildContext );

            accNode.attach();
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
