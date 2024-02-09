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
package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.KeyDefinition;

public class ExactMatcher
        extends Matcher {

    private final Value value;

    private final boolean negate;

    public ExactMatcher(final KeyDefinition keyDefinition,
                        final Comparable value) {
        this(keyDefinition,
             value,
             false);
    }

    public ExactMatcher(final KeyDefinition keyDefinition,
                        final Comparable value,
                        final boolean negate) {
        super(keyDefinition);
        this.value = new Value(value);
        this.negate = negate;
    }

    public Value getValue() {
        return value;
    }

    public boolean isNegate() {
        return negate;
    }
}
