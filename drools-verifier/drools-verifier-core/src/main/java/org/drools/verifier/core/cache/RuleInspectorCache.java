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
package org.drools.verifier.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.verifier.api.reporting.Issue;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.checks.SingleRangeCheck;
import org.drools.verifier.core.checks.base.Check;
import org.drools.verifier.core.checks.base.CheckFactory;
import org.drools.verifier.core.checks.base.CheckStorage;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.matchers.UUIDMatcher;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.Fields;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.util.PortablePreconditions;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class RuleInspectorCache {

    private final Map<Rule, RuleInspector> ruleInspectors = new HashMap<>();
    private final Set<Check> generalChecks = new HashSet<>();
    protected final Index index;
    private final CheckStorage checkStorage;
    private final AnalyzerConfiguration configuration;

    public RuleInspectorCache(final Index index,
                              final AnalyzerConfiguration configuration) {
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
        this.checkStorage = new CheckStorage(new CheckFactory(PortablePreconditions.checkNotNull("configuration",
                                                                                                 configuration)));
        this.configuration = configuration;
    }

    public void reset() {

        for (final RuleInspector ruleInspector : ruleInspectors.values()) {
            ruleInspector.clearChecks();
        }

        ruleInspectors.clear();
        generalChecks.clear();

        for (final Rule rule : index.getRules()
                .where(Rule.uuid().any())
                .select()
                .all()) {
            add(new RuleInspector(rule,
                                  checkStorage,
                                  this,
                                  configuration));
        }

        generalChecks.add(new SingleRangeCheck(configuration,
                                               ruleInspectors.values()));
    }

    public Set<Check> getGeneralChecks() {
        return generalChecks;
    }

    public void newColumn(final Column column) {
        index.getColumns()
                .add(column);
    }

    public Collection<RuleInspector> all() {
        return ruleInspectors.values();
    }

    public Set<Issue> getAllIssues() {
        Set<Issue> issues = new HashSet<>();
        issues.addAll(
                all().stream()
                        .flatMap(inspector -> inspector.getChecks().stream())
                        .filter(Check::hasIssues)
                        .map(Check::getIssue)
                        .collect(toSet())
        );
        issues.addAll(generalChecks.stream()
                              .filter(Check::hasIssues)
                              .map(Check::getIssue)
                              .collect(toSet())
        );
        return issues;
    }

    public Collection<RuleInspector> all(Predicate<RuleInspector> filter) {
        return all().stream().filter(filter).collect(toList());
    }

    private void add(final RuleInspector ruleInspector) {
        ruleInspectors.put(ruleInspector.getRule(),
                           ruleInspector);
    }

    public RuleInspector removeRow(final int rowNumber) {

        final Rule rule = getRule(rowNumber);

        final RuleInspector remove = ruleInspectors.remove(rule);

        index.getRules()
                .remove(rule);

        return remove;
    }

    private Rule getRule(final int rowNumber) {
        return index.getRules()
                .where(Rule.index()
                               .is(rowNumber))
                .select()
                .first();
    }

    public void deleteColumns(final int firstColumnIndex) {
        final Collection<Column> all = index.getColumns()
                .where(Column.index()
                               .is(firstColumnIndex))
                .select()
                .all();

        final Fields.FieldSelector fieldSelector =
                index.getRules()
                        .where(UUIDMatcher.uuid()
                                       .any())
                        .select()
                        .patterns()
                        .where(UUIDMatcher.uuid()
                                       .any())
                        .select()
                        .fields()
                        .where(UUIDMatcher.uuid()
                                       .any())
                        .select();

        final ArrayList<Action> actions = new ArrayList<>();
        final ArrayList<Condition> conditions = new ArrayList<>();

        for (final Field field : fieldSelector.all()) {
            for (final Column column : all) {
                final Collection<Action> all1 = field.getActions()
                        .where(Action.columnUUID()
                                       .is(column.getUuidKey()))
                        .select()
                        .all();
                final Collection<Condition> all2 = field.getConditions()
                        .where(Condition.columnUUID()
                                       .is(column.getUuidKey()))
                        .select()
                        .all();
                actions.addAll(all1);
                conditions.addAll(all2);
            }
        }

        for (final Action action : actions) {
            action.getUuidKey()
                    .retract();
        }

        for (final Condition condition : conditions) {
            condition.getUuidKey()
                    .retract();
        }

        for (final Column column : all) {
            column.getUuidKey()
                    .retract();
        }

        reset();
    }

    public RuleInspector addRule(final Rule rule) {
        this.index.getRules()
                .add(rule);

        final RuleInspector ruleInspector = new RuleInspector(rule,
                                                              checkStorage,
                                                              this,
                                                              configuration);

        add(ruleInspector);

        return ruleInspector;
    }

    public RuleInspector getRuleInspector(final int row) {
        return ruleInspectors.get(getRule(row));
    }

    public AnalyzerConfiguration getConfiguration() {
        return configuration;
    }
}