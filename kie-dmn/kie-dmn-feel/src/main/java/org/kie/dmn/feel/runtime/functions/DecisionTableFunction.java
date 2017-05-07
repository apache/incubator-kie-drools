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

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.*;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.util.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DecisionTableFunction
        extends BaseFEELFunction {

    private static final Logger LOG = LoggerFactory.getLogger( DecisionTableFunction.class );

    public DecisionTableFunction() {
        super( "decision table" );
    }

    /**
     @param inputExpressionList a list of the N>=0 input expressions in display order
     @param inputValuesList * a list of N input values, corresponding to the input expressions. Each
     list element is a unary tests literal (see below).
     @param outputs * a name (a string matching grammar rule 27) or a list of M>0 names
     @param outputValues * if outputs is a list, then output values is a list of lists of values, one list
     per output; else output values is a list of values for the one output.
     Each value is a string.
     @param ruleList a list of R>0 rules. A rule is a list of N input entries followed by M
     output entries. An input entry is a unary tests literal. An output entry is
     an expression represented as a string.
     @param hitPolicy * one of: "U", "A", “P”, “F”, "R", "O", "C", "C+", "C#", "C<", “C>”
     (default is "U")
     @param defaultOutputValue * if outputs is a list, then default output value is a context with entries
     composed of outputs and output values; else default output value is one
     of the output values.
     */
    public Object invoke(@ParameterName("ctx") EvaluationContext ctx, 
            @ParameterName("outputs") Object outputs,
            @ParameterName("input expression list") Object inputExpressionList,
            @ParameterName("input values list") List<?> inputValuesList,
            @ParameterName("output values") Object outputValues,
            @ParameterName("rule list") List<List> ruleList,
            @ParameterName("hit policy") String hitPolicy,
            @ParameterName("default output value") Object defaultOutputValue) {
        // input expression list can have a single element or be a list
        // TODO isn't ^ conflicting with the specs page 136 "input expression list: a LIST of the"
        List<String> inputExpressions = inputExpressionList instanceof List ? (List) inputExpressionList : Collections.singletonList( (String) inputExpressionList );

        List<DTInputClause> inputs;
        if ( inputValuesList != null ) {
            List<UnaryTest> inputValues = inputValuesList.stream().map( DecisionTableFunction::toUnaryTest ).collect( Collectors.toList() );
            if ( inputValues.size() != inputExpressions.size() ) {
                // TODO handle compilation error
            }
            // zip inputExpression with its inputValue
            inputs = IntStream.range( 0, inputExpressions.size() )
                    .mapToObj( i -> new DTInputClause( inputExpressions.get( i ), inputValuesList.toString(), Collections.singletonList( inputValues.get( i ) ), null ) )
                    .collect( Collectors.toList() );
        } else {
            inputs = inputExpressions.stream().map( ie -> new DTInputClause( ie, null, null, null ) ).collect( Collectors.toList() );
        }

        List<String> parseOutputs = outputs instanceof List ? (List) outputs : Collections.singletonList( (String) outputs );
        List<DTOutputClause> outputClauses;
        if ( outputValues != null ) {
            if ( parseOutputs.size() == 1 ) {
                outputClauses = new ArrayList<>();
                List<UnaryTest> outputValuesCompiled = objectToUnaryTestList( Collections.singletonList( (List<Object>) outputValues ) ).get(0);
                outputClauses.add( new DTOutputClause( parseOutputs.get( 0 ), outputValuesCompiled ) );
            } else {
                List<List<UnaryTest>> listOfList = objectToUnaryTestList( (List<List<Object>>) outputValues );
                // zip inputExpression with its inputValue
                outputClauses = IntStream.range( 0, parseOutputs.size() )
                        .mapToObj( i -> new DTOutputClause( parseOutputs.get( i ), listOfList.get( i ) ) )
                        .collect( Collectors.toList() );
            }
        } else {
            outputClauses = parseOutputs.stream().map( out -> new DTOutputClause( out, null ) ).collect( Collectors.toList() );
        }

        // TODO parse default output value.
        FEEL feel = FEEL.newInstance();
        List<DTDecisionRule> decisionRules = IntStream.range( 0, ruleList.size() )
                .mapToObj( index -> toDecisionRule( ctx, feel, index, ruleList.get( index ), inputExpressions.size() ) )
                .collect( Collectors.toList() );

        // TODO is there a way to avoid UUID and get from _evaluation_ ctx the name of the wrapping context? 
        DecisionTableImpl dti = new DecisionTableImpl( UUID.randomUUID().toString(), inputExpressions, inputs, outputClauses, decisionRules, HitPolicy.fromString( hitPolicy ) );
        return new DTInvokerFunction( dti );
    }

    protected List<List<UnaryTest>> objectToUnaryTestList(List<List<Object>> values) {
        if ( values == null || values.isEmpty() ) {
            return Collections.emptyList();
        }
        List<List<UnaryTest>> tests = new ArrayList<>();
        for ( List<Object> lo : values ) {
            List<UnaryTest> uts = new ArrayList<>(  );
            tests.add(uts);
            for( Object t : lo ) {
                uts.add( toUnaryTest( t ) );
            }
        }
        return tests;
    }

    /**
     * Convert row to DTDecisionRule
     * @param mainCtx the main context is used to identify the hosted FEELEventManager
     * @param embeddedFEEL a possibly cached embedded FEEL to compile the output expression, error will be reported up to the mainCtx
     * @param index
     * @param rule
     * @param inputSize
     * @return
     */
    private static DTDecisionRule toDecisionRule(EvaluationContext mainCtx, FEEL embeddedFEEL, int index, List<?> rule, int inputSize) {
        // TODO should be check indeed block of inputSize n inputs, followed by block of outputs.
        DTDecisionRule dr = new DTDecisionRule( index );
        for ( int i = 0; i < rule.size(); i++ ) {
            Object o = rule.get( i );
            if ( i < inputSize ) {
                dr.getInputEntry().add( toUnaryTest( o ) );
            } else {
                FEELEventListener ruleListener = event -> mainCtx.notifyEvt( () -> new FEELEventBase(event.getSeverity(),
                                                                                                     Msg.createMessage(Msg.ERROR_COMPILE_EXPR_DT_FUNCTION_RULE_IDX, index+1, event.getMessage()),
                                                                                                     event.getSourceException()));
                embeddedFEEL.addListener(ruleListener);
                CompiledExpression compiledExpression = embeddedFEEL.compile((String) o, embeddedFEEL.newCompilerContext());
                dr.getOutputEntry().add( compiledExpression );
                embeddedFEEL.removeListener(ruleListener);
            }
        }
        return dr;
    }

    private static UnaryTest toUnaryTest(Object o) {
        if ( o instanceof UnaryTest ) {
            return (UnaryTest) o;
        } else if ( o instanceof Range ) {
            return (c, x) -> ((Range) o).includes( (Comparable<?>) x );
        } else if ( o instanceof List ) {
            return (c, x) -> ((List<?>) o).contains( x );
        } else {
            return (c, x) -> x.equals( o );
        }
    }

}
