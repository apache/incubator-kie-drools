/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.core.cache.inspectors;

import java.util.Collection;

import org.drools.verifier.core.cache.RuleInspectorCache;
import org.drools.verifier.core.checks.base.CheckStorage;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.ActionSuperType;
import org.drools.verifier.core.index.model.BRLCondition;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.ConditionSuperType;
import org.drools.verifier.core.index.model.Conditions;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.FieldCondition;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.select.AllListener;
import org.drools.verifier.core.relations.IsDeficient;
import org.drools.verifier.core.util.PortablePreconditions;

public class IndexRuleInspector
        extends RuleInspector
        implements IsDeficient<IndexRuleInspector> {

    private final Rule rule;

    public IndexRuleInspector(final Rule rule,
                              final CheckStorage checkStorage,
                              final RuleInspectorCache cache,
                              final AnalyzerConfiguration configuration) {
        super(new IndexedInspectedRule(rule),
              checkStorage,
              cache,
              configuration);
        this.rule = PortablePreconditions.checkNotNull("rule",
                                                       rule);
        rule.getConditions()
                .where(Condition.superType()
                               .is(ConditionSuperType.BRL_CONDITION))
                .listen()
                .all(new AllListener<Condition>() {
                    @Override
                    public void onAllChanged(final Collection<Condition> all) {
                        updateBRLConditionInspectors(all);
                    }
                });
        rule.getActions()
                .where(Action.superType()
                               .is(ActionSuperType.BRL_ACTION))
                .listen()
                .all(new AllListener<Action>() {
                    @Override
                    public void onAllChanged(final Collection<Action> all) {
                        updateBRLActionInspectors(all);
                    }
                });
    }

    public Rule getRule() {
        return rule;
    }

    @Override
    public boolean isDeficient(final IndexRuleInspector other) {

        if (other.atLeastOneActionHasAValue() && !getActionsInspectors().conflicts(other.getActionsInspectors())) {
            return false;
        }

        final Collection<Condition> allConditionsFromTheOtherRule = other.rule.getConditions()
                .where(Condition.value()
                               .any())
                .select()
                .all();

        if (allConditionsFromTheOtherRule.isEmpty()) {
            return true;
        } else {

            for (final Condition condition : allConditionsFromTheOtherRule) {

                if (condition.getValues() == null) {
                    continue;
                }

                if (condition instanceof BRLCondition) {
                    final BRLCondition brlCondition = (BRLCondition) condition;

                    if (rule.getConditions().where(Condition.columnUUID().is(brlCondition.getColumn().getUuidKey())).select().exists()) {
                        return false;
                    }
                } else if (condition instanceof FieldCondition) {
                    final FieldCondition fieldCondition = (FieldCondition) condition;
                    if (fieldCondition.getField() instanceof Field) {
                        final Field field = (Field) fieldCondition.getField();
                        final Conditions conditions = rule.getPatterns()
                                .where(Pattern.name()
                                               .is(field.getFactType()))
                                .select()
                                .fields()
                                .where(Field.name()
                                               .is(field.getName()))
                                .select()
                                .conditions();
                        if (conditions
                                .where(Condition.value()
                                               .any())
                                .select()
                                .exists()) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }
}
