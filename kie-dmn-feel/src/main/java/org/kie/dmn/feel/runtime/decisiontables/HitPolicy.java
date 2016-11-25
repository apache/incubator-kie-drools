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
import org.kie.dmn.feel.runtime.events.DecisionTableRulesMatchedEvent;
import org.kie.dmn.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;

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
        Object dti(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs);
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

    public static Object notImplemented(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        throw new RuntimeException( "Not implemented" );
    }

    public static List<DTDecisionRule> matchingDecisionRules(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs) {
        List<DTDecisionRule> matchingDecisionRules = new ArrayList<>();
        for ( DTDecisionRule decisionRule : decisionRules ) {
            if ( DTInvokerFunction.match( ctx, nodeName, params, decisionRule, inputs ) ) {
                matchingDecisionRules.add( decisionRule );
            }
        }
        if( ctx.getEventsManager() != null && !ctx.getEventsManager().getListeners().isEmpty() ) {
            List<Integer> matches = matchingDecisionRules.stream().map( dr -> dr.getIndex() ).collect( Collectors.toList() );
            DecisionTableRulesMatchedEvent rme = new DecisionTableRulesMatchedEvent( FEELEvent.Severity.INFO,
                                                                                     "Rules matched for decision table '" + nodeName + "': "+matches.toString(),
                                                                                     nodeName,
                                                                                     matches );
            ctx.getEventsManager().notifyListeners( rme );
        }

        return matchingDecisionRules;
    }

    /**
     *  Each hit results in one output value (multiple outputs are collected into a single context value)
     */
    private static Object hitToOutput(EvaluationContext ctx, DTDecisionRule hit, List<DTOutputClause> outputs) {
        List<String> outputEntry = hit.getOutputEntry();
        Map<String, Object> values = ctx.getAllValues();
        if ( outputEntry.size() == 1 ) {
            Object value = feel.evaluate( outputEntry.get( 0 ), values );
            return value;
        } else {
            // zip outputEntry with its name:
            return IntStream.range( 0, outputs.size() ).boxed()
                    .collect( toMap( i -> outputs.get( i ).getName(), i -> feel.evaluate( hit.getOutputEntry().get( i ), values ) ) );
        }
    }

    /**
     * Unique – only a single rule can be matched
     */
    public static Object unique(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules( ctx, nodeName, params, decisionRules, inputs );

        if ( matchingDecisionRules.size() > 1 ) {
            throw new RuntimeException( "only a single rule can be matched" );
        }

        if ( matchingDecisionRules.size() == 1 ) {
            return hitToOutput( ctx, matchingDecisionRules.get( 0 ), outputs );
        }

        return null;
    }

    /**
     * First – return the first match in rule order 
     */
    public static Object first(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules( ctx, nodeName, params, decisionRules, inputs );

        if ( matchingDecisionRules.size() >= 1 ) {
            return hitToOutput( ctx, matchingDecisionRules.get( 0 ), outputs );
        }

        return null;
    }

    /**
     * Any – multiple rules can match, but they all have the same output
     */
    public static Object any(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules( ctx, nodeName, params, decisionRules, inputs );

        if ( matchingDecisionRules.size() > 1 ) {
            // TODO revise.
            long distinctOutputEntry = matchingDecisionRules.stream()
                    .map( dr -> hitToOutput( ctx, dr, outputs ) )
                    .distinct()
                    .count();
            if ( distinctOutputEntry > 1 ) {
                throw new RuntimeException( "multiple rules can match, but they [must] all have the same output" );
            }
        }

        if ( matchingDecisionRules.size() >= 1 ) {
            return hitToOutput( ctx, matchingDecisionRules.get( 0 ), outputs );
        }

        return null;
    }

    /**
     * Priority – multiple rules can match, with different outputs. The output that comes first in the supplied output values list is returned
     */
    public static Object priority(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        Object results = outputOrder( ctx, nodeName, params, decisionRules, inputs, outputs );
        return results != null ? ((List) results).get( 0 ) : null;
    }

    /**
     * Output order – return a list of outputs in the order of the output values list 
     */
    public static Object outputOrder(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> matchingDecisionRules = matchingDecisionRules( ctx, nodeName, params, decisionRules, inputs );

        if ( matchingDecisionRules.isEmpty() ) {
            return null;
        }

        List<Object> results = matchingDecisionRules.stream().map( r -> hitToOutput( ctx, r, outputs ) )
                .collect( toList() );

        if ( outputs.size() == 1 && !outputs.get( 0 ).getOutputValues().isEmpty() ) {
            // single output, just sort the results
            List<String> outs = outputs.get( 0 ).getOutputValues();
            results.sort( (r1, r2) -> {
                return sortByOutputsOrder( outs, r1, r2 );
            } );
        } else if ( outputs.size() > 1 ) {
            // multiple outputs, collect the ones that have values listed
            List<DTOutputClause> priorities = outputs.stream().filter( o -> !o.getOutputValues().isEmpty() ).collect( toList() );
            results.sort( (r1, r2) -> {
                Map<String, Object> m1 = (Map<String, Object>) r1;
                Map<String, Object> m2 = (Map<String, Object>) r2;
                for ( DTOutputClause oc : priorities ) {
                    int o = sortByOutputsOrder( oc.getOutputValues(), m1.get( oc.getName() ), m2.get( oc.getName() ) );
                    if ( o != 0 ) {
                        return o;
                    }
                }
                // unable to sort, so keep order
                return 0;
            } );
        }
        return results;
    }

    private static int sortByOutputsOrder(List<String> outs, Object r1, Object r2) {
        int r1i = outs.indexOf( r1 );
        int r2i = outs.indexOf( r2 );
        if ( r1i >= 0 && r2i >= 0 ) {
            return r1i - r2i;
        } else if ( r1i >= 0 ) {
            return -1;
        } else if ( r2i >= 0 ) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Rule order – return a list of outputs in rule order
     * Collect – return a list of the outputs in arbitrary order 
     */
    public static Object ruleOrder(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        List<DTDecisionRule> rules = matchingDecisionRules( ctx, nodeName, params, decisionRules, inputs );

        if ( rules.isEmpty() ) {
            return null;
        }

        List<Object> results = rules.stream().map( r -> hitToOutput( ctx, r, outputs ) )
                .collect( toList() );

        return results;
    }

    public static <T> Collector<T, ?, Object> singleValueOrContext(List<DTOutputClause> outputs) {
        return new SingleValueOrContextCollector<T>( outputs.stream().map( DTOutputClause::getName ).collect( toList() ) );
    }

    public static Object generalizedCollect(
            EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs,
            Function<Stream<Object>, Object> resultCollector) {
        Map<String, Object> variables = ctx.getAllValues();
        List<List<Object>> raw = matchingDecisionRules( ctx, nodeName, params, decisionRules, inputs ).stream()
                .map( DTDecisionRule::getOutputEntry )
                .map( lle -> lle.stream().map( le -> feel.evaluate( le, variables ) ).collect( toList() ) )
                .collect( toList() );
        return range( 0, outputs.size() ).mapToObj( c ->
                                                            resultCollector.apply( raw.stream().map( r -> r.get( c ) ) )
        ).collect( singleValueOrContext( outputs ) );
    }

    /**
     * C# – return the count of the outputs
     */
    public static Object countCollect(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect( ctx, nodeName, params, decisionRules, inputs, outputs,
                                   x -> new BigDecimal( x.collect( toSet() ).size() ) );
    }

    /**
     * C< – return the minimum-valued output
     */
    public static Object minCollect(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect( ctx, nodeName, params, decisionRules, inputs, outputs,
                                   x -> x.map( y -> (Comparable) y ).collect( minBy( Comparator.naturalOrder() ) ).orElse( null ) );
    }

    /**
     * C> – return the maximum-valued output
     */
    public static Object maxCollect(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect( ctx, nodeName, params, decisionRules, inputs, outputs,
                                   x -> x.map( y -> (Comparable) y ).collect( maxBy( Comparator.naturalOrder() ) ).orElse( null ) );
    }

    /**
     * C+ – return the sum of the outputs 
     */
    public static Object sumCollect(EvaluationContext ctx, String nodeName, Object[] params, List<DTDecisionRule> decisionRules, List<DTInputClause> inputs, List<DTOutputClause> outputs) {
        return generalizedCollect( ctx, nodeName, params, decisionRules, inputs, outputs,
                                   x -> x.reduce( BigDecimal.ZERO, (a, b) -> {
                                       if ( !(a instanceof Number && b instanceof Number) ) {
                                           return null;
                                       } else {
                                           BigDecimal aB = new BigDecimal( ((Number) a).toString() );
                                           BigDecimal bB = new BigDecimal( ((Number) b).toString() );
                                           return aB.add( bB );
                                       }
                                   } ) );
    }
}
