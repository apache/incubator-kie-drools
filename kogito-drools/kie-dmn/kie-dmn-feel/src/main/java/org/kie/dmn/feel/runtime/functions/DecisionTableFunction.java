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

import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.ConcreteDTFunction;
import org.kie.dmn.feel.runtime.decisiontables.DecisionRule;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

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

}
