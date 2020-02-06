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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.events.DecisionTableRulesSelectedEvent;
import org.kie.dmn.feel.runtime.events.HitPolicyViolationEvent;
import org.kie.dmn.feel.util.Pair;

import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;

public enum HitPolicy {
    UNIQUE( "U", "UNIQUE", HitPolicy::unique, null ),
    FIRST( "F", "FIRST", HitPolicy::first, null ),
    PRIORITY( "P", "PRIORITY", HitPolicy::priority, null ),
    ANY( "A", "ANY", HitPolicy::any, null ),
    COLLECT( "C", "COLLECT", HitPolicy::ruleOrder, Collections.EMPTY_LIST ),    // Collect – return a list of the outputs in arbitrary order
    COLLECT_SUM( "C+", "COLLECT SUM", HitPolicy::sumCollect, null ),
    COLLECT_COUNT( "C#", "COLLECT COUNT", HitPolicy::countCollect, BigDecimal.ZERO ),
    COLLECT_MIN( "C<", "COLLECT MIN", HitPolicy::minCollect, null ),
    COLLECT_MAX( "C>", "COLLECT MAX", HitPolicy::maxCollect, null ),
    RULE_ORDER( "R", "RULE ORDER", HitPolicy::ruleOrder, null ),
    OUTPUT_ORDER( "O", "OUTPUT ORDER", HitPolicy::outputOrder, null );

    private final String       shortName;
    private final String       longName;
    private final HitPolicyDTI dti;
    private final Object       defaultValue;

    HitPolicy(final String shortName, final String longName) {
        this( shortName, longName, HitPolicy::notImplemented, null );
    }

    HitPolicy(final String shortName, final String longName, final HitPolicyDTI dti, Object defaultValue ) {
        this.shortName = shortName;
        this.longName = longName;
        this.dti = dti;
        this.defaultValue = defaultValue;
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

    public Object getDefaultValue() { return defaultValue; }

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
        Object dti(
                EvaluationContext ctx,
                DecisionTable dt,
                List<? extends Indexed> matches,
                List<Object> results);
    }

    public static Object notImplemented(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        throw new RuntimeException( "Not implemented" );
    }

    /**
     * Unique – only a single rule can be matched
     */
    public static Object unique(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        if ( matches.size() > 1 ) {
            ctx.notifyEvt( () -> {
                                       List<Integer> ruleMatches = matches.stream().map( m -> m.getIndex() + 1 ).collect( toList() );
                                       return new HitPolicyViolationEvent(
                                               FEELEvent.Severity.ERROR,
                                               "UNIQUE hit policy decision tables can only have one matching rule. " +
                                               "Multiple matches found for decision table '" + dt.getName() + "'. Matched rules: " + ruleMatches,
                                               dt.getName(),
                                               ruleMatches );
                                   }
            );
            return null;
        }
        if ( matches.size() == 1 ) {
            ctx.notifyEvt( () -> {
                                       int index = matches.get( 0 ).getIndex() + 1;
                                       return new DecisionTableRulesSelectedEvent(
                                               FEELEvent.Severity.INFO,
                                               "Rule fired for decision table '" + dt.getName() + "': " + index,
                                               dt.getName(),
                                               dt.getName(),
                                               Collections.singletonList( index ) );
                                   }
            );
            return results.get( 0 );
        }
        return null;
    }

    /**
     * First – return the first match in rule order 
     */
    public static Object first(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        if ( matches.size() >= 1 ) {
            ctx.notifyEvt( () -> {
                                       int index = matches.get( 0 ).getIndex() + 1;
                                       return new DecisionTableRulesSelectedEvent(
                                               FEELEvent.Severity.INFO,
                                               "Rule fired for decision table '" + dt.getName() + "': " + index,
                                               dt.getName(),
                                               dt.getName(),
                                               Collections.singletonList( index ) );
                                   }
            );
            return results.get( 0 );
        }
        return null;
    }

    /**
     * Any – multiple rules can match, but they all have the same output
     */
    public static Object any(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        if ( matches.size() >= 1 ) {
            long distinctOutputEntry = results.stream()
                    .distinct()
                    .count();
            if ( distinctOutputEntry > 1 ) {
                ctx.notifyEvt( () -> {
                                        List<Integer> ruleMatches = matches.stream().map( m -> m.getIndex() + 1 ).collect( toList() );
                                        return new HitPolicyViolationEvent(
                                                FEELEvent.Severity.ERROR,
                                                "'Multiple rules can match, but they [must] all have the same output '"  + dt.getName() + "'. Matched rules: " + ruleMatches,
                                                dt.getName(),
                                                ruleMatches );
                                }
                );
                return null;
            }

            ctx.notifyEvt( () -> {
                                       int index = matches.get( 0 ).getIndex() + 1;
                                       return new DecisionTableRulesSelectedEvent(
                                               FEELEvent.Severity.INFO,
                                               "Rule fired for decision table '" + dt.getName() + "': " + index,
                                               dt.getName(),
                                               dt.getName(),
                                               Collections.singletonList( index ) );
                                   }
            );
            return results.get( 0 );
        }
        return null;
    }

    /**
     * Priority – multiple rules can match, with different outputs. The output that comes first in the supplied output values list is returned
     */
    public static Object priority(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        if ( matches.isEmpty() ) {
            return null;
        }
        List<Pair<? extends Indexed, Object>> pairs = sortPairs( ctx, dt, matches, results );
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = Collections.singletonList( pairs.get( 0 ).getLeft().getIndex() + 1 );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );

        return pairs.get( 0 ).getRight();
    }

    /**
     * Output order – return a list of outputs in the order of the output values list 
     */
    public static Object outputOrder(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results ) {
        if ( matches.isEmpty() ) {
            return null;
        }
        List<Pair<? extends Indexed, Object>> pairs = sortPairs( ctx, dt, matches, results );
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = pairs.stream().map( p -> p.getLeft().getIndex() + 1 ).collect( toList() );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );

        return pairs.stream().map( p -> p.getRight() ).collect( Collectors.toList() );
    }

    private static List<Pair<? extends Indexed, Object>> sortPairs( EvaluationContext ctx, DecisionTable dt, List<? extends Indexed> matches, List<Object> results) {
        List<Pair<? extends Indexed,Object>> pairs = new ArrayList<>(  );
        for( int i = 0; i < matches.size(); i++ ) {
            pairs.add( new Pair<>( matches.get( i ), results.get( i ) ) );
        }

        if ( dt.getOutputs().size() == 1 && !dt.getOutputs().get( 0 ).getOutputValues().isEmpty() ) {
            // single output, just sort the results
            List<UnaryTest> outs = dt.getOutputs().get( 0 ).getOutputValues();
            pairs.sort( (r1, r2) -> {
                return sortByOutputsOrder( ctx, outs, r1.getRight(), r2.getRight() );
            } );
        } else if ( dt.getOutputs().size() > 1 ) {
            // multiple outputs, collect the ones that have values listed
            List<? extends DecisionTable.OutputClause> priorities = dt.getOutputs().stream().filter( o -> !o.getOutputValues().isEmpty() ).collect( toList() );
            pairs.sort( (r1, r2) -> {
                Map<String, Object> m1 = (Map<String, Object>) r1.getRight();
                Map<String, Object> m2 = (Map<String, Object>) r2.getRight();
                for ( DecisionTable.OutputClause oc : priorities ) {
                    int o = sortByOutputsOrder( ctx, oc.getOutputValues(), m1.get( oc.getName() ), m2.get( oc.getName() ) );
                    if ( o != 0 ) {
                        return o;
                    }
                }
                // unable to sort, so keep order
                return 0;
            } );
        }
        return pairs;
    }

    private static int sortByOutputsOrder(EvaluationContext ctx, List<UnaryTest> outs, Object r1, Object r2) {
        boolean r1found = false;
        boolean r2found = false;
        for( int index = 0; index < outs.size() && !r1found && !r2found; index++ ) {
            UnaryTest ut = outs.get( index );
            if( ut.apply( ctx, r1 ) ) {
                r1found = true;
            }
            if( ut.apply( ctx, r2 ) ) {
                r2found = true;
            }

        }
        if ( r1found && r2found ) {
            return 0;
        } else if ( r1found ) {
            return -1;
        } else if ( r2found ) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Rule order – return a list of outputs in rule order
     * Collect – return a list of the outputs in arbitrary order 
     */
    public static Object ruleOrder(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        if ( matches.isEmpty() ) {
            return null;
        }
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = matches.stream().map( m -> m.getIndex() + 1 ).collect( toList() );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );
        return results;
    }

    public static <T> Collector<T, ?, Object> singleValueOrContext(List<? extends DecisionTable.OutputClause> outputs) {
        return new SingleValueOrContextCollector<T>( outputs.stream().map( DecisionTable.OutputClause::getName ).collect( toList() ) );
    }

    public static Object generalizedCollect(
            EvaluationContext ctx,
            DecisionTable dt,
            List<?> results,
            Function<Stream<Object>, Object> resultCollector) {
        final List<Map<String, Object>> raw;
        final List<String> names = dt.getOutputs().stream().map( o -> o.getName() != null ? o.getName() : dt.getName() ).collect( toList() );
        if ( names.size() > 1 ) {
            raw = (List<Map<String, Object>>) results;
        } else {
            raw = results.stream().map( (Object r) -> Collections.singletonMap( names.get( 0 ), r ) ).collect( toList() );
        }
        return range( 0, names.size() )
                .mapToObj( index -> names.get( index ) )
                .map( name -> resultCollector.apply( raw.stream().map( r -> r.get( name ) ) ) )
                .collect( singleValueOrContext( dt.getOutputs() ) );
    }

    /**
     * C# – return the count of the outputs
     */
    public static Object countCollect(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = matches.stream().map( m -> m.getIndex() + 1 ).collect( toList() );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );
        return generalizedCollect(
                ctx,
                dt,
                results,
                x -> new BigDecimal( x.collect( toSet() ).size() ) );
    }

    /**
     * C< – return the minimum-valued output
     */
    public static Object minCollect(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        Object result = generalizedCollect(
                ctx,
                dt,
                results,
                x -> x.map( y -> (Comparable) y ).collect( minBy( Comparator.naturalOrder() ) ).orElse( null ) );
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = Collections.singletonList( matches.get( results.indexOf( result ) ).getIndex() + 1 );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );
        return result;
    }

    /**
     * C> – return the maximum-valued output
     */
    public static Object maxCollect(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        Object result = generalizedCollect(
                ctx,
                dt,
                results,
                x -> x.map( y -> (Comparable) y ).collect( maxBy( Comparator.naturalOrder() ) ).orElse( null ) );
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = Collections.singletonList( matches.get( results.indexOf( result ) ).getIndex() + 1 );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );
        return result;
    }

    /**
     * C+ – return the sum of the outputs 
     */
    public static Object sumCollect(
            EvaluationContext ctx,
            DecisionTable dt,
            List<? extends Indexed> matches,
            List<Object> results) {
        ctx.notifyEvt( () -> {
                                   List<Integer> indexes = matches.stream().map( m -> m.getIndex() + 1 ).collect( toList() );
                                   return new DecisionTableRulesSelectedEvent(
                                           FEELEvent.Severity.INFO,
                                           "Rules fired for decision table '" + dt.getName() + "': " + indexes,
                                           dt.getName(),
                                           dt.getName(),
                                           indexes );
                               }
        );
        return generalizedCollect(
                ctx,
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
