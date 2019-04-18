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
import org.drools.verifier.core.index.Index;
import org.drools.verifier.core.index.model.ObjectType;
import org.drools.verifier.core.index.model.Pattern;
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.meta.ConditionMaster;
import org.kie.soup.commons.validation.PortablePreconditions;

public class DMNColumnHeaderInstanceResolver {

    private final AnalyzerConfiguration configuration;
    private final Index index;
    private HeaderMetadata headerMetadata;
    private Rule rule;
    private int columnIndex;

    public DMNColumnHeaderInstanceResolver(final Index index,
                                           final HeaderMetadata headerMetadata,
                                           final AnalyzerConfiguration configuration) {
        this.index = index;
        this.headerMetadata = headerMetadata;
        this.configuration = configuration;
    }

    public DMNColumnHeaderInstanceResolver with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public DMNColumnHeaderInstance resolve() {
        PortablePreconditions.checkNotNull("rule",
                                           rule);

        final ConditionMaster dmnColumnHeaderInstance = rule.getPatterns()
                .where(Pattern.boundName()
                               .is(getInputName()))
                .select()
                .first();

        if (dmnColumnHeaderInstance == null) {
            final DMNColumnHeaderInstance build = new DMNColumnHeaderInstance(resolveInputType(getInputName()),
                                                                              configuration);

            rule.getPatterns().add(build);

            return build;
        } else {
            return (DMNColumnHeaderInstance) dmnColumnHeaderInstance;
        }
    }

    private String getInputName() {
        return headerMetadata.getInputName(columnIndex);
    }

    private HeaderDefinition resolveInputType(final String factType) {
        final ObjectType first = index.getObjectTypes()
                .where(ObjectType.type()
                               .is(factType))
                .select()
                .first();

        if (first == null) {
            final HeaderDefinition objectType = new HeaderDefinition(factType,
                                                                     configuration);
            index.getObjectTypes()
                    .add(objectType);
            return objectType;
        } else {
            return (HeaderDefinition) first;
        }
    }

    public DMNColumnHeaderInstanceResolver with(int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }
}
