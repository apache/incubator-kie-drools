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
import org.drools.verifier.core.index.model.Field;
import org.drools.verifier.core.index.model.meta.ConditionParentType;

public class StructureFieldResolver {

    private final AnalyzerConfiguration analyzerConfiguration;

    public StructureFieldResolver(final AnalyzerConfiguration analyzerConfiguration) {
        this.analyzerConfiguration = analyzerConfiguration;
    }

    public StructureField resolve(final HeaderDefinition headerDefinition) {

        final ConditionParentType first = headerDefinition.getFields()
                .where(Field.name().is(headerDefinition.getType()))
                .select()
                .first();

        if (first == null) {
            final StructureField value = new StructureField(headerDefinition.getType(),
                                                            headerDefinition.getType(),
                                                            analyzerConfiguration);
            headerDefinition.getFields().add(value);
            return value;
        } else {
            return (StructureField) first;
        }
    }
}
