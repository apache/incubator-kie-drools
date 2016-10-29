/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.runtime.functions;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DecisionTableFunction
        extends BaseFEELFunction {

    private static final Logger LOG = LoggerFactory.getLogger( DecisionTableFunction.class );

    public DecisionTableFunction() {
        super( "decision table" );
    }

    public Object apply(
            @ParameterName("outputs") Object outputs, @ParameterName("input expression list") Object inputExpressionList,
            @ParameterName("rule list") List<List> ruleList, @ParameterName("hit policy") String hitPolicy) {
        // input expression list can have a single element or be a list
        List<String> input = inputExpressionList instanceof List ? (List) inputExpressionList : Collections.singletonList( (String) inputExpressionList );

        List<DecisionRule> decisionRules = ruleList.stream()
                .map( o -> DecisionTableFunction.toDecisionRule( o, input.size() ) )
                .collect( Collectors.toList() );

        return new ConcreteDTFunction( UUID.randomUUID().toString(), input, decisionRules, HitPolicy.fromString( hitPolicy ) );
    }

    public static DecisionRule toDecisionRule(List<?> rule, int inputSize) {
        // TODO should be check indeed block of inputSize n inputs, followed by block of outputs.
        DecisionRule dr = new DecisionRule();
        for ( int i = 0; i < rule.size(); i++ ) {
            Object o = rule.get( i );
            if ( i < inputSize ) {
                if ( o instanceof UnaryTest ) {
                    dr.getInputEntry().add( (UnaryTest) o );
                } else if ( o instanceof Range ) {
                    dr.getInputEntry().add( x -> ((Range) o).includes( (Comparable<?>) x ) );
                } else {
                    dr.getInputEntry().add( x -> x.equals( o ) );
                }
            } else {
                dr.getOutputEntry().add( o );
            }
        }
        return dr;
    }

    public static class DecisionRule {
        private List<UnaryTest> inputEntry;
        private List<Object>    outputEntry;

        public List<UnaryTest> getInputEntry() {
            if ( inputEntry == null ) {
                inputEntry = new ArrayList<>();
            }
            return this.inputEntry;
        }

        public List<Object> getOutputEntry() {
            if ( outputEntry == null ) {
                outputEntry = new ArrayList<>();
            }
            return this.outputEntry;
        }
    }

    public static enum HitPolicy {
        UNIQUE( "U" ),
        FIRST( "F" ),
        PRIORITY( "P" ),
        ANY( "A" ),
        COLLECT( "C" ),
        COLLECT_SUM( "C+" ),
        COLLECT_COUNT( "C#" ),
        COLLECT_MIN( "C<" ),
        COLLECT_MAX( "C>" ),
        RULE_ORDER( "R" ),
        OUTPUT_ORDER( "O" );

        private final String value;

        HitPolicy(final String v) {
            value = v;
        }

        public String value() {
            return value;
        }

        public static HitPolicy fromString(final String policy) {
            for ( HitPolicy c : HitPolicy.values() ) {
                if ( c.value.equals( policy ) ) {
                    return c;
                }
            }
            throw new IllegalArgumentException( "Unknown hit policy: " + policy );
        }
    }

    public static class ConcreteDTFunction
            extends BaseFEELFunction {
        private       List<DecisionRule> decisionRules;
        private       List<String>       inputs;
        private final HitPolicy          hitPolicy;

        public ConcreteDTFunction(String name, List<String> inputs, List<DecisionRule> decisionRules, HitPolicy hitPolicy) {
            super( name );
            this.decisionRules = decisionRules;
            this.inputs = inputs;
            this.hitPolicy = hitPolicy;
        }

        public Object apply(EvaluationContext ctx, Object[] params) {
            if ( decisionRules.isEmpty() ) {
                return null;
            }

            if ( params.length != decisionRules.get( 0 ).getInputEntry().size() ) {
                LOG.error( "The parameters supplied does not match input expression list" );
                return null;
            }

            for ( DecisionRule decisionRule : decisionRules ) {
                Boolean ruleMatches = IntStream.range( 0, params.length )                         // TODO could short-circuit by using for/continue
                        .mapToObj( i -> decisionRule.getInputEntry().get( i ).apply( params[i] ) )
                        .reduce( (a, b) -> a && b )
                        .orElse( false );
                if ( ruleMatches ) {
                    if ( decisionRule.getOutputEntry().size() == 1 ) {
                        return decisionRule.getOutputEntry().get( 0 );
                    } else {
                        return decisionRule.getOutputEntry();
                    }
                }
            }

            return null;
        }

        @Override
        protected boolean isCustomFunction() {
            return true;
        }

        public List<List<String>> getParameterNames() {
            return Arrays.asList( inputs );
        }

        public HitPolicy getHitPolicy() {
            return hitPolicy;
        }
    }
}
