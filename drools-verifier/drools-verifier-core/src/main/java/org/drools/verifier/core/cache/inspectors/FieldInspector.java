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
package org.drools.verifier.core.cache.inspectors;

import java.util.Collection;

import org.drools.verifier.core.cache.inspectors.action.ActionInspector;
import org.drools.verifier.core.cache.inspectors.action.ActionInspectorFactory;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspector;
import org.drools.verifier.core.cache.inspectors.condition.ConditionInspectorFactory;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.ObjectField;
import org.drools.verifier.core.index.select.AllListener;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.UpdatableInspectorList;
import org.drools.verifier.core.maps.util.HasConflicts;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.relations.Conflict;
import org.drools.verifier.core.relations.HumanReadable;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.IsRedundant;
import org.drools.verifier.core.relations.IsSubsuming;
import org.drools.verifier.core.util.PortablePreconditions;

public class FieldInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable,
                   HasKeys {

    private final ObjectField objectField;

    private final UpdatableInspectorList<ActionInspector, Action> actionInspectorList;
    private final UpdatableInspectorList<ConditionInspector, Condition> conditionInspectorList;
    private final UUIDKey uuidKey;
    private final RuleInspectorUpdater ruleInspectorUpdater;

    public FieldInspector(final Field field,
                          final RuleInspectorUpdater ruleInspectorUpdater,
                          final AnalyzerConfiguration configuration) {
        this(field.getObjectField(),
             ruleInspectorUpdater,
             configuration);

        configuration.getUUID(this);

        updateActionInspectors(field.getActions()
                                       .where(Action.value()
                                                      .any())
                                       .select()
                                       .all());
        updateConditionInspectors(field.getConditions()
                                          .where(Condition.value()
                                                         .any())
                                          .select()
                                          .all());

        setupActionsListener(field);
        setupConditionsListener(field);
    }

    public FieldInspector(final ObjectField field,
                          final RuleInspectorUpdater ruleInspectorUpdater,
                          final AnalyzerConfiguration configuration) {
        this.objectField = PortablePreconditions.checkNotNull("field",
                                                              field);
        this.ruleInspectorUpdater = PortablePreconditions.checkNotNull("ruleInspectorUpdater",
                                                                       ruleInspectorUpdater);

        uuidKey = configuration.getUUID(this);

        actionInspectorList = new UpdatableInspectorList<>(new ActionInspectorFactory(configuration),
                                                           configuration);
        conditionInspectorList = new UpdatableInspectorList<>(new ConditionInspectorFactory(configuration),
                                                              configuration);
    }

    private void setupConditionsListener(final Field field) {
        field.getConditions()
                .where(Condition.value()
                               .any())
                .listen()
                .all(new AllListener<Condition>() {
                    @Override
                    public void onAllChanged(final Collection<Condition> all) {
                        updateConditionInspectors(all);
                        ruleInspectorUpdater.resetConditionsInspectors();
                    }
                });
    }

    private void setupActionsListener(final Field field) {
        field.getActions()
                .where(Action.value()
                               .any())
                .listen()
                .all(new AllListener<Action>() {
                    @Override
                    public void onAllChanged(final Collection<Action> all) {
                        updateActionInspectors(all);
                        ruleInspectorUpdater.resetActionsInspectors();
                    }
                });
    }

    public ObjectField getObjectField() {
        return objectField;
    }

    private void updateConditionInspectors(final Collection<Condition> all) {
        conditionInspectorList.update(all);
    }

    private void updateActionInspectors(final Collection<Action> all) {
        actionInspectorList.update(all);
    }

    public InspectorList<ActionInspector> getActionInspectorList() {
        return actionInspectorList;
    }

    public InspectorList<ConditionInspector> getConditionInspectorList() {
        return conditionInspectorList;
    }

    @Override
    public Conflict hasConflicts() {
        int index = 1;
        for (final ConditionInspector conditionInspector : conditionInspectorList) {
            for (int j = index; j < conditionInspectorList.size(); j++) {
                if (conditionInspector.conflicts(conditionInspectorList.get(j))) {
                    return new Conflict(conditionInspector,
                                        conditionInspectorList.get(j));
                }
            }
            index++;
        }
        return Conflict.EMPTY;
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof FieldInspector && objectField.equals(((FieldInspector) other).objectField)) {

            final boolean conflicting = actionInspectorList.conflicts(((FieldInspector) other).actionInspectorList);
            if (conflicting) {
                return true;
            } else {
                return conditionInspectorList.conflicts(((FieldInspector) other).conditionInspectorList);
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isRedundant(final Object other) {
        if (other instanceof FieldInspector && objectField.equals(((FieldInspector) other).objectField)) {
            return actionInspectorList.isRedundant(((FieldInspector) other).actionInspectorList)
                    && conditionInspectorList.isRedundant(((FieldInspector) other).conditionInspectorList);
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes(final Object other) {
        if (other instanceof FieldInspector && objectField.equals(((FieldInspector) other).objectField)) {
            return actionInspectorList.subsumes(((FieldInspector) other).actionInspectorList)
                    && conditionInspectorList.subsumes(((FieldInspector) other).conditionInspectorList);
        } else {
            return false;
        }
    }

    @Override
    public String toHumanReadableString() {
        return objectField.getName();
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey
        };
    }
}
