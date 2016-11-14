/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.spi;

import org.drools.core.WorkingMemory;
import org.kie.api.definition.rule.Rule;

import java.io.Serializable;

public interface Salience extends Serializable {

    int DEFAULT_SALIENCE_VALUE = 0;

    int getValue(final KnowledgeHelper khelper,
                 final Rule rule,
                 final WorkingMemory workingMemory);

    int getValue();

    boolean isDynamic();

    default boolean isDefault() {
        return getValue() == DEFAULT_SALIENCE_VALUE;
    }
}
