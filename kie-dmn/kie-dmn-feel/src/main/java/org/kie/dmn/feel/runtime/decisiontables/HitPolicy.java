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

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.kie.dmn.feel.runtime.events.InvalidInputEvent;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
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

    HitPolicy(final String shortName, final String longName) {
        this( shortName, longName, HitPolicy::notImplemented );
    }

    HitPolicy(final String shortName, final String longName, final HitPolicyDTI dti) {
        this.shortName = shortName;
        this.longName = longName;
        this.dti = dti;
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

    /* ---------------------------------------
            HIT POLICY IMPLEMENTATION
       --------------------------------------- */
    @FunctionalInterface
    public interface HitPolicyDTI {
        Object dti(EvaluationContext ctx,
                   DecisionTableImpl dt,
                   Object[] params,
                   List<DTDecisionRule> matches,
                   List<Object> results);
    }

    public static Object notImplemented(EvaluationContext ctx,
                                        DecisionTableImpl dt,
                                        Object[] params,
                                        List<DTDecisionRule> matches,
                                        List<Object> results) {
        throw new RuntimeException( "Not implemented" );
    }

    /**
     * Unique – only a single rule can be matched
     */
    public static Object unique(EvaluationContext ctx,
                                DecisionTableImpl dt,
                                Object[] params,
                                List<DTDecisionRule> matches,
                                List<Object> results) {
        if ( matches.size() > 1 ) {
            FEELEventListenersManager.notifyListeners(ctx.getEventsManager(), () -> {
                List<Integer> ruleMatches = matches.stream().map( m -> m.getIndex() ).collect(toList());
                return new HitPolicyViolationEvent( FEELEvent.Severity.ERROR,
                                                    "UNIQUE hit policy decision tables can only have one matching rule. "+
                                                    "Multiple matches found for decision table '"+dt.getName()+"'. Matched rules: "+ruleMatches,
                                                    dt.getName(),
                                                    ruleMatches );
                }
            );
            return null;
        }
        if ( matches.size() == 1 ) {
            return results.get( 0 );
        }
        return null;
    }

    /**
     * First – return the first match in rule order 
     */
    public static Object first(EvaluationContext ctx,
                               DecisionTableImpl dt,
                               Object[] params,
                               List<DTDecisionRule> matches,
                               List<Object> results) {
        if ( matches.size() >= 1 ) {
            return results.get( 0 );
        }
        return null;
    }

    /**
     * Any – multiple rules can match, but they all have the same output
     */
    public static Object any(EvaluationContext ctx,
                             DecisionTableImpl dt,
                             Object[] params,
                             List<DTDecisionRule> matches,
                             List<Object> results) {
        if ( matches.size() > 1 ) {
            long distinctOutputEntry = results.stream()
                    .distinct()
                    .count();
            if ( distinctOutputEntry > 1 ) {
                throw new RuntimeException( "multiple rules can match, but they [must] all have the same output" );
            }

            return results.get( 0 );
        }
        return null;
    }

    /**
     * Priority – multiple rules can match, with different outputs. The output that comes first in the supplied output values list is returned
     */
    public static Object priority(EvaluationContext ctx,
                                  DecisionTableImpl dt,
                                  Object[] params,
                                  List<DTDecisionRule> matches,
                                  List<Object> results) {
        Object result = outputOrder( ctx, dt, params, matches, results );
        return result != null ? ((List) result).get( 0 ) : null;
    }

    /**
     * Output order – return a list of outputs in the order of the output values list 
     */
    public static Object outputOrder(EvaluationContext ctx,
                                     DecisionTableImpl dt,
                                     Object[] params,
                                     List<DTDecisionRule> matches,
                                     List<Object> results) {
        if ( matches.isEmpty() ) {
            return null;
        }

        if ( dt.getOutputs().size() == 1 && !dt.getOutputs().get( 0 ).getOutputValues().isEmpty() ) {
            // single output, just sort the results
            List<String> outs = dt.getOutputs().get( 0 ).getOutputValues();
            results.sort( (r1, r2) -> {
                return sortByOutputsOrder( outs, r1, r2 );
            } );
        } else if ( dt.getOutputs().size() > 1 ) {
            // multiple outputs, collect the ones that have values listed
            List<DTOutputClause> priorities = dt.getOutputs().stream().filter( o -> !o.getOutputValues().isEmpty() ).collect( toList() );
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
    public static Object ruleOrder(EvaluationContext ctx,
                                   DecisionTableImpl dt,
                                   Object[] params,
                                   List<DTDecisionRule> matches,
                                   List<Object> results) {
        if ( matches.isEmpty() ) {
            return null;
        }
        return results;
    }

    public static <T> Collector<T, ?, Object> singleValueOrContext(List<DTOutputClause> outputs) {
        return new SingleValueOrContextCollector<T>( outputs.stream().map( DTOutputClause::getName ).collect( toList() ) );
    }

    public static Object generalizedCollect(EvaluationContext ctx,
                                            DecisionTableImpl dt,
                                            List<?> results,
                                            Function<Stream<Object>, Object> resultCollector) {
        final List<Map<String, Object>> raw;
        final List<String> names = dt.getOutputs().stream().map( o -> o.getName() != null ? o.getName() : dt.getName() ).collect(toList());
        if( names.size() > 1 ) {
            raw = (List<Map<String, Object> >) results;
        } else {
            raw = results.stream().map( (Object r) -> Collections.singletonMap( names.get( 0 ), r ) ).collect( toList() );
        }
        return range( 0, names.size() )
                .mapToObj( index -> names.get( index ) )
                .map( name ->resultCollector.apply( raw.stream().map( r -> r.get( name ) ) ) )
                .collect( singleValueOrContext( dt.getOutputs() ) );
    }

    /**
     * C# – return the count of the outputs
     */
    public static Object countCollect(EvaluationContext ctx,
                                      DecisionTableImpl dt,
                                      Object[] params,
                                      List<DTDecisionRule> matches,
                                      List<Object> results) {
        return generalizedCollect( ctx,
                                   dt,
                                   results,
                                   x -> new BigDecimal( x.collect( toSet() ).size() ) );
    }

    /**
     * C< – return the minimum-valued output
     */
    public static Object minCollect(EvaluationContext ctx,
                                    DecisionTableImpl dt,
                                    Object[] params,
                                    List<DTDecisionRule> matches,
                                    List<Object> results) {
        return generalizedCollect( ctx,
                                   dt,
                                   results,
                                   x -> x.map( y -> (Comparable) y ).collect( minBy( Comparator.naturalOrder() ) ).orElse( null ) );
    }

    /**
     * C> – return the maximum-valued output
     */
    public static Object maxCollect(EvaluationContext ctx,
                                    DecisionTableImpl dt,
                                    Object[] params,
                                    List<DTDecisionRule> matches,
                                    List<Object> results) {
        return generalizedCollect( ctx,
                                   dt,
                                   results,
                                   x -> x.map( y -> (Comparable) y ).collect( maxBy( Comparator.naturalOrder() ) ).orElse( null ) );
    }

    /**
     * C+ – return the sum of the outputs 
     */
    public static Object sumCollect(EvaluationContext ctx,
                                    DecisionTableImpl dt,
                                    Object[] params,
                                    List<DTDecisionRule> matches,
                                    List<Object> results) {
        return generalizedCollect( ctx,
                                   dt,
                                   results,
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
