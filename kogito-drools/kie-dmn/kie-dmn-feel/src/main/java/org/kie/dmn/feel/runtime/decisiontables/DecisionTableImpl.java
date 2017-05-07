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

package org.kie.dmn.feel.runtime.decisiontables;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.util.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class DecisionTableImpl {
    private static final Logger logger = LoggerFactory.getLogger( DecisionTableImpl.class );

    private String               name;
    private List<String>         parameterNames;
    private List<DTInputClause>  inputs;
    private List<DTOutputClause> outputs;
    private List<DTDecisionRule> decisionRules;
    private HitPolicy            hitPolicy;
    private boolean              hasDefaultValues;

    public DecisionTableImpl(String name,
                             List<String> parameterNames,
                             List<DTInputClause> inputs,
                             List<DTOutputClause> outputs,
                             List<DTDecisionRule> decisionRules,
                             HitPolicy hitPolicy) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.inputs = inputs;
        this.outputs = outputs;
        this.decisionRules = decisionRules;
        this.hitPolicy = hitPolicy;
        this.hasDefaultValues = outputs.stream().allMatch( o -> o.getDefaultValue() != null );
    }

    /**
     * Evaluates this decision table returning the result
     * @param ctx
     * @param params these are the required information items, not to confuse with the columns of the
     *               decision table that are expressions derived from these parameters
     * @return
     */
    public FEELFnResult<Object> evaluate(EvaluationContext ctx, Object[] params) {
        if ( decisionRules.isEmpty() ) {
            return FEELFnResult.ofError(new FEELEventBase(Severity.WARN, "Decision table is empty", null));
        }
        
        FEEL feel = FEEL.newInstance();
        Object[] actualInputs = resolveActualInputs( ctx, feel );

        Either<FEELEvent, Object> actualInputMatch = actualInputsMatchInputValues( ctx, actualInputs );
        if ( actualInputMatch.isLeft() ) {
            return actualInputMatch.cata( e -> FEELFnResult.ofError(e), e -> FEELFnResult.ofError(null) );
        }

        List<DTDecisionRule> matches = findMatches( ctx, actualInputs );
        if( !matches.isEmpty() ) {
            List<Object> results = evaluateResults( ctx, feel, actualInputs, matches );
            Object result = hitPolicy.getDti().dti( ctx, this, actualInputs, matches, results );

            return FEELFnResult.ofResult( result );
        } else {
            // check if there is a default value set for the outputs
            if( hasDefaultValues ) {
                Object result = defaultToOutput( ctx, feel );
                return FEELFnResult.ofResult( result );
            } else {
                return FEELFnResult.ofError( new HitPolicyViolationEvent(
                                                    Severity.WARN,
                                                    "No rule matched for decision table '" + name + "' and no default values were defined. Setting result to null.",
                                                    name,
                                                    Collections.EMPTY_LIST ) );
            }
        }
    }

    private Object[] resolveActualInputs(EvaluationContext ctx, FEEL feel) {
        Map<String, Object> variables = ctx.getAllValues();
        Object[] actualInputs = new Object[ inputs.size() ];
        for( int i = 0; i < inputs.size(); i++ ) {
            CompiledExpression compiledInput = inputs.get( i ).getCompiledInput();
            if( compiledInput != null ) {
                actualInputs[i] = feel.evaluate( compiledInput, variables );
            } else {
                actualInputs[i] = feel.evaluate( inputs.get( i ).getInputExpression(), variables );
            }
        }
        return actualInputs;
    }

    /**
     * If valid input values are defined, check that all parameters match the respective valid inputs
     * @param ctx
     * @param params
     * @return
     */
    private Either<FEELEvent, Object> actualInputsMatchInputValues(EvaluationContext ctx, Object[] params) {
        // check that all the parameters match the input list values if they are defined
        for( int i = 0; i < params.length; i++ ) {
            final DTInputClause input = inputs.get( i );
            // if a list of values is defined, check the the parameter matches the value
            if ( input.getInputValues() != null && ! input.getInputValues().isEmpty() ) {
                final Object parameter = params[i];
                boolean satisfies = input.getInputValues().stream().map( ut -> ut.apply( ctx, parameter ) ).filter( Boolean::booleanValue ).findAny().orElse( false );

                if ( !satisfies ) {
                    String values = input.getInputValuesText();
                    return Either.ofLeft(new InvalidInputEvent( FEELEvent.Severity.ERROR,
                                                  input.getInputExpression()+"='" + parameter + "' does not match any of the valid values " + values + " for decision table '" + getName() + "'.",
                                                  getName(),
                                                  null,
                                                  values )
                            );
                }
            }
        }
        return Either.ofRight(true);
    }

    /**
     * Finds all rules that match a given set of parameters
     *
     * @param ctx
     * @param params
     * @return
     */
    private List<DTDecisionRule> findMatches(EvaluationContext ctx, Object[] params) {
        List<DTDecisionRule> matchingDecisionRules = new ArrayList<>();
        for ( DTDecisionRule decisionRule : decisionRules ) {
            if ( matches( ctx, params, decisionRule ) ) {
                matchingDecisionRules.add( decisionRule );
            }
        }
        FEELEventListenersManager.notifyListeners( ctx.getEventsManager() , () -> {
            List<Integer> matches = matchingDecisionRules.stream().map( dr -> dr.getIndex() + 1 ).collect( Collectors.toList() );
            return new DecisionTableRulesMatchedEvent(FEELEvent.Severity.INFO,
                                                      "Rules matched for decision table '" + getName() + "': " + matches.toString(),
                                                      getName(),
                                                      getName(),
                                                      matches );
            }
        );
        return matchingDecisionRules;
    }

    /**
     * Checks if the parameters match a single rule
     * @param ctx
     * @param params
     * @param rule
     * @return
     */
    private boolean matches(EvaluationContext ctx, Object[] params, DTDecisionRule rule) {
        for( int i = 0; i < params.length; i++ ) {
            if( ! satisfies( ctx, params[i], rule.getInputEntry().get( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks that a given parameter matches a single cell test
     * @param ctx
     * @param param
     * @param test
     * @return
     */
    private boolean satisfies(EvaluationContext ctx, Object param, UnaryTest test ) {
        return test.apply( ctx, param );
    }

    private List<Object> evaluateResults(EvaluationContext ctx, FEEL feel, Object[] params, List<DTDecisionRule> matchingDecisionRules) {
        List<Object> results = matchingDecisionRules.stream().map( dr -> hitToOutput( ctx, feel, dr ) ).collect( Collectors.toList());
        return results;
    }

    /**
     *  Each hit results in one output value (multiple outputs are collected into a single context value)
     */
    private Object hitToOutput(EvaluationContext ctx, FEEL feel, DTDecisionRule rule) {
        List<CompiledExpression> outputEntries = rule.getOutputEntry();
        Map<String, Object> values = ctx.getAllValues();
        if ( outputEntries.size() == 1 ) {
            Object value = feel.evaluate( outputEntries.get( 0 ), values );
            return value;
        } else {
            // zip outputEntries with its name:
            return IntStream.range( 0, outputs.size() ).boxed()
                    .collect( toMap( i -> outputs.get( i ).getName(), i -> feel.evaluate( outputEntries.get( i ), values ) ) );
        }
    }

    /**
     *  No hits matched for the DT, so calculate result based on default outputs
     */
    private Object defaultToOutput(EvaluationContext ctx, FEEL feel) {
        Map<String, Object> values = ctx.getAllValues();
        if ( outputs.size() == 1 ) {
            Object value = feel.evaluate( outputs.get( 0 ).getDefaultValue(), values );
            return value;
        } else {
            // zip outputEntries with its name:
            return IntStream.range( 0, outputs.size() ).boxed()
                    .collect( toMap( i -> outputs.get( i ).getName(), i -> feel.evaluate( outputs.get( i ).getDefaultValue(), values ) ) );
        }
    }



    public HitPolicy getHitPolicy() {
        return hitPolicy;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<DTOutputClause> getOutputs() {
        return outputs;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public String getSignature() {
        return getName() + "( " + parameterNames.stream().collect( Collectors.joining( ", " ) ) + " )";
    }

}
