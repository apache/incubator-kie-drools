/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.validation.dtanalysis.verifier;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.matchers.UUIDMatchers;
import org.drools.verifier.core.index.model.ConditionParents;
import org.drools.verifier.core.index.model.meta.ConditionMaster;
import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.util.HasKeys;
import org.drools.verifier.core.util.PortablePreconditions;

public class DMNColumnHeaderInstance
        implements HasKeys,
                   ConditionMaster {

    private static final KeyDefinition NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition()
            .withId("name")
            .build();
    private static final KeyDefinition BOUND_NAME_KEY_DEFINITION = KeyDefinition.newKeyDefinition()
            .withId("boundName")
            .build();

    private final UUIDKey uuidKey;
    private final ConditionParents conditionParents;
    private final HeaderDefinition headerDefinition;

    public DMNColumnHeaderInstance(final HeaderDefinition headerDefinition,
                                   final AnalyzerConfiguration configuration) {
        this.headerDefinition = PortablePreconditions.checkNotNull("headerDefinition",
                                                                   headerDefinition);
        this.uuidKey = configuration.getUUID(this);
        this.conditionParents = new ConditionParents(configuration);
    }

    public static Matchers boundName() {
        return new Matchers(BOUND_NAME_KEY_DEFINITION);
    }

    public static Matchers name() {
        return new Matchers(NAME_KEY_DEFINITION);
    }

    public static Matchers uuid() {
        return new UUIDMatchers();
    }

    @Override
    public UUIDKey getUuidKey() {
        return uuidKey;
    }

    @Override
    public String getName() {
        return headerDefinition.getType();
    }

    @Override
    public String getBoundName() {
        return headerDefinition.getType();
    }

    @Override
    public String getType() {
        return headerDefinition.getType();
    }

    public HeaderDefinition getColumnDefinition() {
        return headerDefinition;
    }

    @Override
    public ConditionParents getConditionParents() {
        return conditionParents;
    }

    @Override
    public Key[] keys() {
        return new Key[]{
                uuidKey,
                new Key(NAME_KEY_DEFINITION,
                        getName()),
                new Key(BOUND_NAME_KEY_DEFINITION,
                        getBoundName())
        };
    }
}
