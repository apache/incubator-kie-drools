/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.api.score.holder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.common.AgendaItem;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.event.rule.ActivationUnMatchListener;
import org.kie.api.runtime.rule.RuleContext;
import org.kie.api.runtime.rule.Session;
import org.optaplanner.core.api.score.constraint.bigdecimal.BigDecimalConstraintMatch;
import org.optaplanner.core.api.score.constraint.bigdecimal.BigDecimalConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.primdouble.DoubleConstraintMatch;
import org.optaplanner.core.api.score.constraint.primdouble.DoubleConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.primint.IntConstraintMatch;
import org.optaplanner.core.api.score.constraint.primint.IntConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.primlong.LongConstraintMatch;
import org.optaplanner.core.api.score.constraint.primlong.LongConstraintMatchTotal;

/**
 * Abstract superclass for {@link ScoreHolder}.
 */
public abstract class AbstractScoreHolder implements ScoreHolder, Serializable {

    protected final boolean constraintMatchEnabled;
    protected final Map<List<Object>, ConstraintMatchTotal> constraintMatchTotalMap;

    protected AbstractScoreHolder(boolean constraintMatchEnabled) {
        this.constraintMatchEnabled = constraintMatchEnabled;
        // TODO Can we set the initial capacity of this map more accurately? For example: number of rules
        constraintMatchTotalMap = constraintMatchEnabled
                ? new LinkedHashMap<List<Object>, ConstraintMatchTotal>() : null;
    }

    public boolean isConstraintMatchEnabled() {
        return constraintMatchEnabled;
    }

    public Collection<ConstraintMatchTotal> getConstraintMatchTotals() {
        return constraintMatchTotalMap.values();
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    protected void registerIntConstraintMatch(RuleContext kcontext, int scoreLevel, int weight,
            final Runnable undoListener) {
        AgendaItem agendaItem = prepareAgendaItemForUnMatchListener(kcontext);
        if (!constraintMatchEnabled) {
            // Fast code
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                }
            });
        } else {
            // Add and remove ConstraintMatch
            final IntConstraintMatchTotal constraintMatchTotal = findIntConstraintMatchTotal(kcontext, scoreLevel);
            final IntConstraintMatch constraintMatch = constraintMatchTotal.addConstraintMatch(kcontext, weight);
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                    constraintMatchTotal.removeConstraintMatch(constraintMatch);
                }
            });
        }
    }

    private IntConstraintMatchTotal findIntConstraintMatchTotal(RuleContext kcontext, int scoreLevel) {
        Rule rule = kcontext.getRule();
        String constraintPackage = rule.getPackageName();
        String constraintName = rule.getName();
        List<Object> key = Arrays.<Object>asList(constraintPackage, constraintName, scoreLevel);
        IntConstraintMatchTotal matchTotal = (IntConstraintMatchTotal) constraintMatchTotalMap.get(key);
        if (matchTotal == null) {
            matchTotal = new IntConstraintMatchTotal(constraintPackage, constraintName, scoreLevel);
            constraintMatchTotalMap.put(key, matchTotal);
        }
        return matchTotal;
    }

    protected void registerLongConstraintMatch(RuleContext kcontext, int scoreLevel, long weight,
            final Runnable undoListener) {
        AgendaItem agendaItem = prepareAgendaItemForUnMatchListener(kcontext);
        if (!constraintMatchEnabled) {
            // Fast code
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                }
            });
        } else {
            // Add and remove ConstraintMatch
            final LongConstraintMatchTotal constraintMatchTotal = findLongConstraintMatchTotal(kcontext, scoreLevel);
            final LongConstraintMatch constraintMatch = constraintMatchTotal.addConstraintMatch(kcontext, weight);
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                    constraintMatchTotal.removeConstraintMatch(constraintMatch);
                }
            });
        }
    }

    private LongConstraintMatchTotal findLongConstraintMatchTotal(RuleContext kcontext, int scoreLevel) {
        Rule rule = kcontext.getRule();
        String constraintPackage = rule.getPackageName();
        String constraintName = rule.getName();
        List<Object> key = Arrays.<Object>asList(constraintPackage, constraintName, scoreLevel);
        LongConstraintMatchTotal matchTotal = (LongConstraintMatchTotal) constraintMatchTotalMap.get(key);
        if (matchTotal == null) {
            matchTotal = new LongConstraintMatchTotal(constraintPackage, constraintName, scoreLevel);
            constraintMatchTotalMap.put(key, matchTotal);
        }
        return matchTotal;
    }

    protected void registerDoubleConstraintMatch(RuleContext kcontext, int scoreLevel, double weight,
            final Runnable undoListener) {
        AgendaItem agendaItem = prepareAgendaItemForUnMatchListener(kcontext);
        if (!constraintMatchEnabled) {
            // Fast code
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                }
            });
        } else {
            // Add and remove ConstraintMatch
            final DoubleConstraintMatchTotal constraintMatchTotal = findDoubleConstraintMatchTotal(kcontext, scoreLevel);
            final DoubleConstraintMatch constraintMatch = constraintMatchTotal.addConstraintMatch(kcontext, weight);
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                    constraintMatchTotal.removeConstraintMatch(constraintMatch);
                }
            });
        }
    }

    private DoubleConstraintMatchTotal findDoubleConstraintMatchTotal(RuleContext kcontext, int scoreLevel) {
        Rule rule = kcontext.getRule();
        String constraintPackage = rule.getPackageName();
        String constraintName = rule.getName();
        List<Object> key = Arrays.<Object>asList(constraintPackage, constraintName, scoreLevel);
        DoubleConstraintMatchTotal matchTotal = (DoubleConstraintMatchTotal) constraintMatchTotalMap.get(key);
        if (matchTotal == null) {
            matchTotal = new DoubleConstraintMatchTotal(constraintPackage, constraintName, scoreLevel);
            constraintMatchTotalMap.put(key, matchTotal);
        }
        return matchTotal;
    }

    protected void registerBigDecimalConstraintMatch(RuleContext kcontext, int scoreLevel, BigDecimal weight,
            final Runnable undoListener) {
        AgendaItem agendaItem = prepareAgendaItemForUnMatchListener(kcontext);
        if (!constraintMatchEnabled) {
            // Fast code
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                }
            });
        } else {
            // Add and remove ConstraintMatch
            final BigDecimalConstraintMatchTotal constraintMatchTotal = findBigDecimalConstraintMatchTotal(kcontext, scoreLevel);
            final BigDecimalConstraintMatch constraintMatch = constraintMatchTotal.addConstraintMatch(kcontext, weight);
            agendaItem.setActivationUnMatchListener(new ActivationUnMatchListener() {
                public void unMatch(Session wm, Match activation) {
                    undoListener.run();
                    constraintMatchTotal.removeConstraintMatch(constraintMatch);
                }
            });
        }
    }

    private BigDecimalConstraintMatchTotal findBigDecimalConstraintMatchTotal(RuleContext kcontext, int scoreLevel) {
        Rule rule = kcontext.getRule();
        String constraintPackage = rule.getPackageName();
        String constraintName = rule.getName();
        List<Object> key = Arrays.<Object>asList(constraintPackage, constraintName, scoreLevel);
        BigDecimalConstraintMatchTotal matchTotal = (BigDecimalConstraintMatchTotal) constraintMatchTotalMap.get(key);
        if (matchTotal == null) {
            matchTotal = new BigDecimalConstraintMatchTotal(constraintPackage, constraintName, scoreLevel);
            constraintMatchTotalMap.put(key, matchTotal);
        }
        return matchTotal;
    }

    private AgendaItem prepareAgendaItemForUnMatchListener(RuleContext kcontext) {
        AgendaItem agendaItem = (AgendaItem) kcontext.getMatch();
        if (agendaItem.getActivationUnMatchListener() != null) {
            // Both parameters null because they are not used by the ActivationUnMatchListener created below anyway
            agendaItem.getActivationUnMatchListener().unMatch(null, null);
        }
        return agendaItem;
    }

}
