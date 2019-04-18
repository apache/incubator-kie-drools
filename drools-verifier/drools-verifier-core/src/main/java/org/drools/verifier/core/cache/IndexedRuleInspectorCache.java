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

package org.drools.verifier.core.cache;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.verifier.core.cache.inspectors.IndexRuleInspector;
import org.drools.verifier.core.cache.inspectors.RuleInspector;
import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.matchers.UUIDMatcher;
import org.drools.verifier.core.index.model.Action;
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.Condition;
import org.drools.verifier.core.index.model.ConditionParents;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.meta.ConditionParent;
import org.drools.verifier.core.util.PortablePreconditions;

public class IndexedRuleInspectorCache
        extends RuleInspectorCache {

    protected final Index index;

    public IndexedRuleInspectorCache(final Index index,
                                     final AnalyzerConfiguration configuration) {
        super(index.getRules()
                      .where(Rule.uuid().any())
                      .select()
                      .all(),
              configuration);
        this.index = PortablePreconditions.checkNotNull("index",
                                                        index);
    }

    public void newColumn(final Column column) {
        index.getColumns()
                .add(column);
    }

    @Override
    protected Collection<Rule> getRules() {
        return index.getRules()
                .where(Rule.uuid().any())
                .select()
                .all();
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

        final ConditionParents.FieldSelector fieldSelector =
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

        for (final ConditionParent field : fieldSelector.all()) {
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

        final RuleInspector ruleInspector = addInspector(rule);

        return ruleInspector;
    }

    @Override
    protected RuleInspector makeRuleInspector(final Rule rule) {
        return new IndexRuleInspector(rule,
                                      checkStorage,
                                      this,
                                      configuration);
    }

    public RuleInspector getRuleInspector(final int row) {
        return ruleInspectors.get(getRule(row));
    }
}