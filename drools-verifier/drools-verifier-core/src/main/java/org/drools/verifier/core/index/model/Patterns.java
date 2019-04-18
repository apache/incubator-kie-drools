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
package org.drools.verifier.core.index.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.model.meta.ConditionMaster;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.KeyTreeMap;
import org.drools.verifier.core.maps.MultiMap;

public class Patterns {

    public final KeyTreeMap<ConditionMaster> patternsMap = new KeyTreeMap<>(Pattern.keyDefinitions());
    private final AnalyzerConfiguration configuration;

    public Patterns(final AnalyzerConfiguration configuration,
                    final Collection<ConditionMaster> patternsMap) {
        this(configuration);

        for (final ConditionMaster pattern : patternsMap) {
            add(pattern);
        }
    }

    public Patterns(final AnalyzerConfiguration configuration,
                    final ConditionMaster[] patternsMap) {
        this(configuration,
             Arrays.asList(patternsMap));
    }

    public Patterns(final AnalyzerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void merge(final Patterns patterns) {
        this.patternsMap.merge(patterns.patternsMap);
    }

    public Where<PatternsSelect, PatternsListen> where(final Matcher matcher) {
        return new Where<PatternsSelect, PatternsListen>() {
            @Override
            public PatternsSelect select() {
                return new PatternsSelect(matcher);
            }

            @Override
            public PatternsListen listen() {
                return new PatternsListen(matcher);
            }
        };
    }

    public void add(final ConditionMaster... patterns) {
        for (final ConditionMaster pattern : patterns) {
            this.patternsMap.put(pattern);
        }
    }

    public class PatternsSelect
            extends Select<ConditionMaster> {

        public PatternsSelect(final Matcher matcher) {
            super(patternsMap.get(matcher.getKeyDefinition()),
                  matcher);
        }

        public ConditionParents fields() {
            final ConditionParents conditionParents = new ConditionParents(configuration);

            final MultiMap<Value, ConditionMaster, List<ConditionMaster>> subMap = asMap();
            if (subMap != null) {
                final Collection<ConditionMaster> patterns = subMap.allValues();
                for (final ConditionMaster pattern : patterns) {
                    conditionParents.merge(pattern.getConditionParents());
                }
            }

            return conditionParents;
        }
    }

    public class PatternsListen
            extends Listen<ConditionMaster> {

        public PatternsListen(final Matcher matcher) {
            super(patternsMap.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
