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

import org.drools.core.WorkingMemory;
import org.drools.core.reteoo.EvalConditionNode;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EvalCondition;
import org.drools.core.spi.EvalExpression;
import org.drools.core.spi.Tuple;
import org.mockito.Mockito;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvalNodeStep
    implements
    Step {

    public EvalNodeStep(ReteTesterHelper reteTesterHelper) {
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        BuildContext buildContext = (BuildContext) context.get( "BuildContext" );
        String name;
        String source;
        String expr;

        if ( args.size() == 1 ) {
            String[] c = args.get( 0 );
            if ( c.length != 3 ) {
                throw new IllegalArgumentException( "Cannot execute arguments " + Arrays.toString( c ) );
            }
            name = c[0].trim();
            source = c[1].trim();
            expr = c[2].trim();
        } else {
            throw new IllegalArgumentException( "Cannot execute arguments " + args );
        }
        LeftTupleSource pnode = null;
        if ( source.startsWith( "mock" ) ) {
            pnode = Mockito.mock( LeftTupleSource.class );
        } else {
            pnode = (LeftTupleSource) context.get( source );
        }

        EvalCondition eval = new EvalCondition( new InstrumentedEvalExpression( expr,
                                                                                context ),
                                                new Declaration[0] );
        EvalConditionNode evalNode = new EvalConditionNode( buildContext.getNextId(),
                                                            pnode,
                                                            eval,
                                                            buildContext );
        evalNode.attach(buildContext);
        context.put( name,
                     evalNode );
    }

    public static class InstrumentedEvalExpression
        implements
        EvalExpression {
        private String              expr;
        private Map<String, Object> context;

        public InstrumentedEvalExpression(String expr,
                                          Map<String, Object> context) {
            super();
            this.expr = expr.replaceAll( "h(\\d+)",
                                         "Handles[$1]" );
            this.context = context;
        }

        public Object createContext() {
            return null;
        }

        public boolean evaluate(Tuple tuple,
                                Declaration[] requiredDeclarations,
                                WorkingMemory workingMemory,
                                Object ctx) throws Exception {
            // create a map with all captured tuples as variables
            Map<String, Object> vars = new HashMap<String, Object>();
            // add all context variables, just in case
            vars.putAll( this.context );
            vars.put( "tuple",
                      tuple.toFactHandles() );

            // compile MVEL expression
            ParserContext mvelctx = new ParserContext();
            Serializable compiled = MVEL.compileExpression( this.expr,
                                                            mvelctx );
            // execute the expression
            Boolean result = (Boolean) MVEL.executeExpression( compiled,
                                                               vars );
            return result.booleanValue();
        }

        public Declaration[] getRequiredDeclarations() {
            return null;
        }

        public void replaceDeclaration(Declaration declaration,
                                       Declaration resolved) {
        }
        
        public EvalExpression clone() {
            return this;
        }

    }
}
