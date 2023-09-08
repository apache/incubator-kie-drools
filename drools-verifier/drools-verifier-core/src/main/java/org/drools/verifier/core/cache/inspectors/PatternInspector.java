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

import org.drools.verifier.core.cache.inspectors.action.ActionsInspectorMultiMap;
import org.drools.verifier.core.cache.inspectors.condition.ConditionsInspectorMultiMap;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.maps.InspectorList;
import org.drools.verifier.core.maps.util.HasConflicts;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.relations.Conflict;
import org.drools.verifier.core.relations.HumanReadable;
import org.drools.verifier.core.relations.IsConflicting;
import org.drools.verifier.core.relations.IsRedundant;
import org.drools.verifier.core.relations.IsSubsuming;
import org.drools.verifier.core.relations.RelationResolver;
import org.drools.verifier.core.util.PortablePreconditions;

public class PatternInspector
        implements HasConflicts,
                   IsConflicting,
                   IsSubsuming,
                   IsRedundant,
                   HumanReadable,
                   HasKeys {

    private final UUIDKey uuidKey;

    private final Pattern pattern;
    private final AnalyzerConfiguration configuration;

    private final InspectorList<FieldInspector> inspectorList;
    private final RelationResolver relationResolver;

    public PatternInspector(final Pattern pattern,
                            final RuleInspectorUpdater ruleInspectorUpdater,
                            final AnalyzerConfiguration configuration) {
        this.pattern = PortablePreconditions.checkNotNull("pattern",
                                                          pattern);
        this.configuration = PortablePreconditions.checkNotNull("configuration",
                                                                configuration);

        uuidKey = configuration.getUUID(this);
        inspectorList = new InspectorList<>(configuration);

        relationResolver = new RelationResolver(inspectorList);

        makeFieldInspectors(pattern.getFields()
                                    .where(Field.uuid()
                                                   .any())
                                    .select()
                                    .all(),
                            ruleInspectorUpdater);
    }

    private void makeFieldInspectors(final Collection<Field> fields,
                                     final RuleInspectorUpdater ruleInspectorUpdater) {

        inspectorList.clear();

        for (final Field field : fields) {
            inspectorList.add(new FieldInspector(field,
                                                 ruleInspectorUpdater,
                                                 configuration));
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean conflicts(final Object other) {
        if (other instanceof PatternInspector) {
            if (pattern.getObjectType()
                    .getType()
                    .equals(((PatternInspector) other).getPattern()
                                    .getObjectType()
                                    .getType())) {
                return inspectorList.conflicts(((PatternInspector) other).inspectorList);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean isRedundant(final Object other) {
        if (other instanceof PatternInspector) {
            if (pattern.getObjectType()
                    .getType()
                    .equals(((PatternInspector) other).getPattern()
                                    .getObjectType()
                                    .getType())) {
                return inspectorList.isRedundant(((PatternInspector) other).inspectorList);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean subsumes(final Object other) {
        if (other instanceof PatternInspector) {
            if (pattern.getObjectType()
                    .getType()
                    .equals(((PatternInspector) other).getPattern()
                                    .getObjectType()
                                    .getType())) {
                return inspectorList.subsumes(((PatternInspector) other).inspectorList);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Conflict hasConflicts() {
        return relationResolver.resolveConflict(inspectorList);
    }

    public ActionsInspectorMultiMap getActionsInspector() {
        final ActionsInspectorMultiMap<Comparable> actionsInspector = new ActionsInspectorMultiMap<>(configuration);

        for (final FieldInspector fieldInspector : inspectorList) {
            actionsInspector.addAllValues(fieldInspector.getObjectField(),
                                          fieldInspector.getActionInspectorList());
        }

        return actionsInspector;
    }

    public ConditionsInspectorMultiMap getConditionsInspector() {
        final ConditionsInspectorMultiMap conditionsInspector = new ConditionsInspectorMultiMap(configuration);

        for (final FieldInspector fieldInspector : inspectorList) {
            if (!fieldInspector.getConditionInspectorList().isEmpty()) {
                conditionsInspector.addAllValues(fieldInspector.getObjectField(),
                                                 fieldInspector.getConditionInspectorList());
            }
        }

        return conditionsInspector;
    }

    @Override
    public String toHumanReadableString() {
        return pattern.getName();
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
