/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.util.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toMap;

public class DecisionTableImpl implements DecisionTable {
    private static final Logger logger = LoggerFactory.getLogger( DecisionTableImpl.class );

    private String               name;
    private List<String>         parameterNames;
    private List<CompiledExpression> compiledParameterNames;
    private List<DTInputClause>  inputs;
    private List<DTOutputClause> outputs;
    private List<DTDecisionRule> decisionRules;
    private HitPolicy            hitPolicy;
    private boolean              hasDefaultValues;

    private FEEL feel;

    public DecisionTableImpl(String name,
                             List<String> parameterNames,
                             List<DTInputClause> inputs,
                             List<DTOutputClause> outputs,
                             List<DTDecisionRule> decisionRules,
                             HitPolicy hitPolicy,
                             FEEL feel) {
        this.name = name;
        this.parameterNames = parameterNames;
        this.inputs = inputs;
        this.outputs = outputs;
        this.decisionRules = decisionRules;
        this.hitPolicy = hitPolicy;
        this.hasDefaultValues = outputs.stream().allMatch( o -> o.getDefaultValue() != null );
        this.feel = feel;
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
        
        Object[] actualInputs = resolveActualInputs( ctx, feel );

        Either<FEELEvent, Object> actualInputMatch = actualInputsMatchInputValues( ctx, actualInputs );
        if ( actualInputMatch.isLeft() ) {
            return actualInputMatch.cata( e -> FEELFnResult.ofError(e), e -> FEELFnResult.ofError(null) );
        }

        List<DTDecisionRule> matches = findMatches( ctx, actualInputs );
        if( !matches.isEmpty() ) {
            List<Object> results = evaluateResults( ctx, feel, actualInputs, matches );
            Map<Integer, String> msgs = checkResults( ctx, matches, results );
            if( msgs.isEmpty() ) {
                Object result = hitPolicy.getDti().dti( ctx, this, matches, results );
                return FEELFnResult.ofResult( result );
            } else {
                List<Integer> offending = msgs.keySet().stream().collect( Collectors.toList());
                return FEELFnResult.ofError( new HitPolicyViolationEvent(
                        Severity.ERROR,
                        "Errors found evaluating decision table '"+getName()+"': \n"+(msgs.values().stream().collect( Collectors.joining( "\n" ) )),
                        name,
                        offending ) );
            }
        } else {
            // check if there is a default value set for the outputs
            if( hasDefaultValues ) {
                Object result = defaultToOutput( ctx, feel );
                return FEELFnResult.ofResult( result );
            } else {
                if( hitPolicy.getDefaultValue() != null ) {
                    return FEELFnResult.ofResult( hitPolicy.getDefaultValue() );
                }
                return FEELFnResult.ofError( new HitPolicyViolationEvent(
                                                    Severity.WARN,
                                                    "No rule matched for decision table '" + name + "' and no default values were defined. Setting result to null.",
                                                    name,
                                                    Collections.EMPTY_LIST ) );
            }
        }
    }

    private Map<Integer, String> checkResults(EvaluationContext ctx, List<DTDecisionRule> matches, List<Object> results) {
        return checkResults(outputs, ctx, matches, results);
    }

    public static Map<Integer, String> checkResults(List<? extends DecisionTable.OutputClause> outputs, EvaluationContext ctx, List<? extends Indexed> matches, List<Object> results) {
        Map<Integer, String> msgs = new TreeMap<>(  );
        int i = 0;
        for( Object result : results ) {
            if( outputs.size() == 1 ) {
                checkOneResult( ctx, matches.get( i ), msgs, outputs.get( 0 ), result, 1 );
            } else if( outputs.size() > 1 ) {
                Map<String, Object> r = (Map<String, Object>) result;
                int outputIndex = 1;
                for ( DecisionTable.OutputClause output : outputs ) {
                    checkOneResult( ctx, matches.get( i ), msgs, output, r.get( output.getName() ), outputIndex++ );
                }
            }
            i++;
        }
        return msgs;
    }

    /**
     * This checks one "column" of the decision table output(s).
     */
    private static void checkOneResult(EvaluationContext ctx, Indexed rule, Map<Integer, String> msgs, DecisionTable.OutputClause dtOutputClause, Object result, int index) {
        if (dtOutputClause.isCollection() && result instanceof Collection) {
            for (Object value : (Collection) result) {
                checkOneValue(ctx, rule, msgs, dtOutputClause, value, index);
            }
        } else {
            checkOneValue(ctx, rule, msgs, dtOutputClause, result, index);
        }
    }

    private static void checkOneValue(EvaluationContext ctx, Indexed rule, Map<Integer, String> msgs, DecisionTable.OutputClause dtOutputClause, Object value, int index) {
        if (((EvaluationContextImpl) ctx).isPerformRuntimeTypeCheck() && !dtOutputClause.getType().isAssignableValue(value)) {
            // invalid type
            msgs.put( index,
                      "Invalid result type on rule #" + (rule.getIndex()+1) + ", output " +
                      (dtOutputClause.getName() != null ? "'"+dtOutputClause.getName()+"'" : "#" + index) +
                            ". Value " + value + " is not of type " + dtOutputClause.getType().getName() + ".");
            return;
        }
        if( dtOutputClause.getOutputValues() != null && ! dtOutputClause.getOutputValues().isEmpty() ) {
            boolean found = false;
            for( UnaryTest test : dtOutputClause.getOutputValues() ) {
                Boolean succeeded = test.apply(ctx, value);
                if( succeeded != null && succeeded ) {
                    found = true;
                }
            }
            if( ! found ) {
                // invalid result
                msgs.put( index,
                          "Invalid result value on rule #"+(rule.getIndex()+1)+", output "+
                          (dtOutputClause.getName() != null ? "'"+dtOutputClause.getName()+"'" : "#"+index ) +
                                ". Value " + value + " does not match list of allowed values.");
            }
        }
    }

    private Object[] resolveActualInputs(EvaluationContext ctx, FEEL feel) {
        Object[] actualInputs = new Object[ inputs.size() ];
        for( int i = 0; i < inputs.size(); i++ ) {
            CompiledExpression compiledInput = inputs.get( i ).getCompiledInput();
            if( compiledInput != null ) {
                actualInputs[i] = feel.evaluate(compiledInput, ctx);
            } else {
                actualInputs[i] = feel.evaluate(inputs.get(i).getInputExpression(), ctx);
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
                boolean satisfies = true;
                if (input.isCollection() && parameter instanceof Collection) {
                    for (Object parameterItem : (Collection<?>) parameter) {
                        satisfies &= input.getInputValues().stream().map(ut -> ut.apply(ctx, parameterItem)).filter(x -> x != null && x).findAny().orElse(false);
                    }
                } else {
                    satisfies = input.getInputValues().stream().map(ut -> ut.apply(ctx, parameter)).filter(x -> x != null && x).findAny().orElse(false);
                }

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
        ctx.notifyEvt( () -> {
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
            CompiledExpression compiledInput = inputs.get(i).getCompiledInput();
            if ( compiledInput instanceof CompiledFEELExpression) {
                ctx.setValue("?", ((CompiledFEELExpression) compiledInput).apply(ctx));
            }
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
    	Stream<Object> s = matchingDecisionRules.stream().map( dr -> hitToOutput( ctx, feel, dr ) );
        List<Object> results = hitPolicy == HitPolicy.FIRST ? s.limit(1).collect(Collectors.toList()) : s.collect(Collectors.toList()); // as hitToOutput might return nulls, use .limit(1) instead of .findFirst()
        return results;
    }

    /**
     *  Each hit results in one output value (multiple outputs are collected into a single context value)
     */
    private Object hitToOutput(EvaluationContext ctx, FEEL feel, DTDecisionRule rule) {
        List<CompiledExpression> outputEntries = rule.getOutputEntry();
        if ( outputEntries.size() == 1 ) {
            Object value = feel.evaluate(outputEntries.get(0), ctx);
            return value;
        } else {
            Map<String, Object> output = new HashMap<>();
            for (int i = 0; i < outputs.size(); i++) {
                output.put(outputs.get(i).getName(), feel.evaluate(outputEntries.get(i), ctx));
            }
            return output;
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

    /**
     * This is leveraged from the DMN layer, and currently unused from a pure FEEL layer perspective (DT FEEL expression deprecated anyway from the DMN spec itself).
     */
    public void setCompiledParameterNames(List<CompiledExpression> compiledParameterNames) {
        this.compiledParameterNames = compiledParameterNames;
    }

    /**
     * This is leveraged from the DMN layer, and currently unused from a pure FEEL layer perspective (DT FEEL expression deprecated anyway from the DMN spec itself).
     */
    public List<CompiledExpression> getCompiledParameterNames() {
        return compiledParameterNames;
    }

    public String getSignature() {
        return getName() + "( " + parameterNames.stream().collect( Collectors.joining( ", " ) ) + " )";
    }

}
