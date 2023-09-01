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
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.index.query.Where;
import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;
import org.drools.verifier.core.maps.KeyTreeMap;
import org.drools.verifier.core.maps.MultiMap;

public class Patterns {

    public final KeyTreeMap<Pattern> patternsMap = new KeyTreeMap<>(Pattern.keyDefinitions());

    public Patterns(final Collection<Pattern> patternsMap) {
        for (final Pattern pattern : patternsMap) {
            add(pattern);
        }
    }

    public Patterns(final Pattern[] patternsMap) {
        this(Arrays.asList(patternsMap));
    }

    public Patterns() {

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

    public void add(final Pattern... patterns) {
        for (final Pattern pattern : patterns) {
            this.patternsMap.put(pattern);
        }
    }

    public class PatternsSelect
            extends Select<Pattern> {

        public PatternsSelect(final Matcher matcher) {
            super(patternsMap.get(matcher.getKeyDefinition()),
                  matcher);
        }

        public Fields fields() {
            final Fields fields = new Fields();

            final MultiMap<Value, Pattern, List<Pattern>> subMap = asMap();
            if (subMap != null) {
                final Collection<Pattern> patterns = subMap.allValues();
                for (final Pattern pattern : patterns) {
                    fields.merge(pattern.getFields());
                }
            }

            return fields;
        }
    }

    public class PatternsListen
            extends Listen<Pattern> {

        public PatternsListen(final Matcher matcher) {
            super(patternsMap.get(matcher.getKeyDefinition()),
                  matcher);
        }
    }
}
