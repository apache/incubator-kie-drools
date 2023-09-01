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
package org.drools.verifier.core.index.model;

import java.util.Arrays;
import java.util.Collection;

import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.KeyTreeMap;

public class Conditions {

    public final KeyTreeMap<Condition> map = new KeyTreeMap<>(Condition.keyDefinitions());

    public Conditions() {

    }

    public Conditions(final Collection<Condition> conditions) {
        for (final Condition condition : conditions) {
            add(condition);
        }
    }

    public void add(final Condition condition) {
        map.put(condition);
    }

    public Conditions(final Condition... conditions) {
        this(Arrays.asList(conditions));
    }

    public Where<ConditionSelector, ConditionListen> where(final Matcher matcher) {
        return new Where<ConditionSelector, ConditionListen>() {
            @Override
            public ConditionSelector select() {
                return new ConditionSelector(matcher);
            }

            @Override
            public ConditionListen listen() {
                return new ConditionListen(matcher);
            }
        };
    }

    public void merge(final Conditions conditions) {
        map.merge(conditions.map);
    }

    public void remove(final Column column) {
        final ExactMatcher matcher = Condition.columnUUID().is(column.getUuidKey());
        for (final Condition condition : where(matcher).select().all()) {
            condition.getUuidKey().retract();
        }
    }

    public class ConditionSelector
            extends Select<Condition> {

        public ConditionSelector(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }

    public class ConditionListen
            extends Listen<Condition> {

        public ConditionListen(final Matcher matcher) {
            super(map.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}