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

import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.model.v1_1.LiteralExpression;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.IntStream.range;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import java.math.BigDecimal;

public enum HitPolicy {
    UNIQUE( "U", "UNIQUE", HitPolicy::unique ),
    FIRST( "F", "FIRST", HitPolicy::first ),
    PRIORITY( "P", "PRIORITY", HitPolicy::priority ),
    ANY( "A", "ANY", HitPolicy::any ),
    COLLECT( "C", "COLLECT", HitPolicy::ruleOrder ),    // Collect – return a list of the outputs in arbitrary order 
    COLLECT_SUM( "C+", "COLLECT SUM", HitPolicy::sumCollect ),
    COLLECT_COUNT( "C#", "COLLECT COUNT", HitPolicy::countCollect ),
    COLLECT_MIN( "C<", "COLLECT MIN", HitPolicy::minCollect ),
    COLLECT_MAX( "C>", "COLLECT MAX", HitPolicy::maxCollect ),
    RULE_ORDER( "R", "RULE ORDER", HitPolicy::ruleOrder ),
    OUTPUT_ORDER( "O", "OUTPUT ORDER", HitPolicy::outputOrder );

    private final String shortName;
    private final String longName;
    private final HitPolicyDTI dti;

    private static final FEEL feel = FEEL.newInstance();

    HitPolicy(final String shortName, final String longName) {
        this( shortName, longName, HitPolicy::notImplemented );
    }
    
    HitPolicy(final String shortName, final String longName, final HitPolicyDTI dti) {
        this.shortName = shortName;
        this.longName = longName;
        this.dti = dti;
    }
    
    @FunctionalInterface
    public static interface HitPolicyDTI {
        Object dti(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs);
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }
    
    public HitPolicyDTI getDti() {
        return dti;
    }

    public static HitPolicy fromString(String policy) {
        policy = policy.toUpperCase();
        for ( HitPolicy c : HitPolicy.values() ) {
            if ( c.shortName.equals( policy ) || c.longName.equals( policy ) ) {
                return c;
            }
        }
        throw new IllegalArgumentException( "Unknown hit policy: " + policy );
    }
    
    public static Object notImplemented(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        throw new RuntimeException("Not implemented");
    }
    
    public static List<DTDecisionRule> matchingDecisionRules(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs) {
        List<DTDecisionRule> matchingDecisionRules = new ArrayList<>();
        for ( DTDecisionRule decisionRule : decisionRules ) {
            if ( DTInvokerFunction.match(params, decisionRule, inputs) ) {
                matchingDecisionRules.add( decisionRule );
            }
        }
        return matchingDecisionRules;
    }
    
    /**
     *  Each hit results in one output value (multiple outputs are collected into a single context value)
     */
    private static Object hitToOutput(EvaluationContext ctx, DTDecisionRule hit, List<DTOutputClause> outputs) {
        List<LiteralExpression> outputEntry = hit.getOutputEntry();
        Map<String, Object> values = ctx.getAllValues();
        if ( outputEntry.size() == 1 ) {
            Object value = feel.evaluate( outputEntry.get( 0 ).getText(), values );
            return value;
        } else {
            // zip outputEntry with its name:
            return IntStream.range(0, outputs.size()).boxed()
                    .collect( toMap( i -> outputs.get(i).getName(), i -> feel.evaluate( hit.getOutputEntry().get(i).getText(), values ) ) );
        }
    }
    
    /**
     * Unique – only a single rule can be matched
     */
    public static Object unique(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules(ctx, params, decisionRules, inputs);
        
        if ( matchingDecisionRules.size() > 1 ) {
            throw new RuntimeException("only a single rule can be matched");
        }
            
        if ( matchingDecisionRules.size() == 1 ) {
            return hitToOutput( ctx, matchingDecisionRules.get(0), outputs );
        }
        
        return null;
    }
    
    /**
     * First – return the first match in rule order 
     */
    public static Object first(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules(ctx, params, decisionRules, inputs);
            
        if ( matchingDecisionRules.size() >= 1 ) {
            return hitToOutput( ctx, matchingDecisionRules.get(0), outputs );
        }
        
        return null;
    }
    
    /**
     * Any – multiple rules can match, but they all have the same output
     */
    public static Object any(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules(ctx, params, decisionRules, inputs);
        
        if ( matchingDecisionRules.size() > 1 ) {
            // TODO revise.
            long distinctOutputEntry = matchingDecisionRules.stream()
                .map( dr -> dr.getOutputEntry() )
                .distinct()
                .count();
            if ( distinctOutputEntry > 1 ) {
                throw new RuntimeException("multiple rules can match, but they [must] all have the same output");    
            }
        }
            
        if ( matchingDecisionRules.size() >= 1 ) {
            return hitToOutput( ctx, matchingDecisionRules.get(0), outputs );
        }
        
        return null;
    }
    
    /**
     * Priority – multiple rules can match, with different outputs. The output that comes first in the supplied output values list is returned
     * TODO what about if the set of {different outputs} is not contained at all in the set of {the supplied output values} ?
     * TODO I think there is conflict in specs between hitpolicy Priority as defined in FEEL Vs the broader DMN scope
     *      in the FEEL scope, is ok.
     *      in the broader DMN scope it reads:
     *      "Priority: multiple rules can match, with different output entries. This policy returns the matching rule with the
 highest output priority."
            WHAT-IF in the broader DMN scope I have 2+ outputs, and I define outputValueList in such a way to make "conflicting" priority between the *matching rules*
            instead of the granularity of the single output? 
     */
    public static Object priority(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules(ctx, params, decisionRules, inputs);
        
        Map<String, Object> resultOrdered = new HashMap<>();
        for ( int i = 0; i < outputs.size(); i++ ) {
            final int outIndex = i;
            DTOutputClause out = outputs.get( outIndex );
            for ( Object outValue : out.getOutputValues() ) {
                boolean inMatchedRules = matchingDecisionRules.stream()
                    .map( dr -> dr.getOutputEntry().get( outIndex ) )
                    .anyMatch( outN -> outN.equals( outValue ) );
                if ( inMatchedRules ) {
                    resultOrdered.put(out.getName(), outValue);
                    break; // outValue found, now move fwd to the next outputs[i]
                }
            }
        }
        
        if (resultOrdered.size() == 1) {
            return resultOrdered.entrySet().iterator().next().getValue();
        } else {
            return resultOrdered;
        }
    }

    /**
     * Output order – return a list of outputs in the order of the output values list 
     */
    public static Object outputOrder(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules(ctx, params, decisionRules, inputs);
        
        Map<String, List<Object>> resultOrdered = new HashMap<>();
        for ( int i = 0; i < outputs.size(); i++ ) {
            final int outIndex = i;
            DTOutputClause out = outputs.get( outIndex );
            for ( Object outValue : out.getOutputValues() ) {
                boolean inMatchedRules = matchingDecisionRules.stream()
                    .map( dr -> dr.getOutputEntry().get( outIndex ) )
                    .anyMatch( outN -> outN.equals( outValue ) );
                if ( inMatchedRules ) {
                    resultOrdered.computeIfAbsent(out.getName(), k->new ArrayList<>()).add(outValue);
                    // similar to hitpolicy "priority" but in this case I continue to evaluate all elements of .getOutputValues() ..
                }
            }
        }
        
        if (resultOrdered.size() == 1) {
            if ( matchingDecisionRules.size() == 1 ) {
                return resultOrdered.entrySet().iterator().next().getValue().get(0);
            }
            return resultOrdered.entrySet().iterator().next().getValue();
        } else {
            if ( matchingDecisionRules.size() == 1 ) {
                Map<String, Object> res = new HashMap<>();
                for ( Entry<String, List<Object>> kv : resultOrdered.entrySet() ) {
                    res.put(kv.getKey(), kv.getValue().get(0));
                }
                return res;
            }
            return resultOrdered;
        }
    }
    
    /**
     * Rule order – return a list of outputs in rule order
     * Collect – return a list of the outputs in arbitrary order 
     */
    public static Object ruleOrder(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
         List<List<?>> collectedOuts = matchingDecisionRules(ctx, params, decisionRules, inputs).stream()
                .map( DTDecisionRule::getOutputEntry )
                .collect( toList() );
         if ( outputs.size() == 1 ) {
             return collectedOuts.stream().map(outputEntryList->outputEntryList.get(0)).collect(toList());
         } else {
             // zip outputEntry with its name; do not use .collect( Collectors.toMap ) as it does not support null for values.
             Map<String, Object> res = new HashMap<>();
             for ( int i=0 ; i < outputs.size(); i++  ) {
                 final int index = i;
                 res.put(outputs.get(i).getName(), collectedOuts.stream().map(outputEntryList->outputEntryList.get(index)).collect(toList()));
             }
             return res;
         }
    }
    
    public static <T> Collector<T, ?, Object> singleValueOrContext(List<DTOutputClause> outputs) {
        return new SingleValueOrContextCollector<T>( outputs.stream().map(DTOutputClause::getName).collect(toList()) );
    }
    
    public static Object generalizedCollect(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs,
            Function<Stream<Object>, Object> resultCollector) {
        Map<String, Object> variables = ctx.getAllValues();
        List<List<Object>> raw = matchingDecisionRules(ctx, params, decisionRules, inputs).stream()
                 .map( DTDecisionRule::getOutputEntry )
                 .map( lle -> lle.stream().map( le -> feel.evaluate( le.getText(), variables ) ) .collect( toList() ) )
                 .collect( toList() );
        return range(0, outputs.size()).mapToObj( c ->
            resultCollector.apply( raw.stream().map( r -> r.get(c) ) )
        ).collect( singleValueOrContext( outputs ) );
    }
    
    /**
     * C# – return the count of the outputs
     */
    public static Object countCollect(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
       return generalizedCollect(ctx, params, decisionRules, inputs, outputs,
               x -> new BigDecimal(x.collect( toSet() ).size()) );
    }

    /**
     * C< – return the minimum-valued output
     */
    public static Object minCollect(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect(ctx, params, decisionRules, inputs, outputs,
                x -> x.map( y -> (Comparable) y ).collect( minBy( Comparator.naturalOrder() ) ).orElse(null) );
    }
    
    /**
     * C> – return the maximum-valued output
     */
    public static Object maxCollect(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect(ctx, params, decisionRules, inputs, outputs,
                x -> x.map( y -> (Comparable) y ).collect( maxBy( Comparator.naturalOrder() ) ).orElse(null) );
    }
    
    /**
     * C+ – return the sum of the outputs 
     */
    public static Object sumCollect(EvaluationContext ctx, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect(ctx, params, decisionRules, inputs, outputs,
                x -> x.reduce( BigDecimal.ZERO , (a, b) -> {
                    if ( !( a instanceof Number && b instanceof Number ) ) {
                        return null;
                    } else {
                        BigDecimal aB = new BigDecimal( ((Number) a).toString() );
                        BigDecimal bB = new BigDecimal( ((Number) b).toString() );
                        return aB.add( bB );
                    }
                }) );
    }
}
