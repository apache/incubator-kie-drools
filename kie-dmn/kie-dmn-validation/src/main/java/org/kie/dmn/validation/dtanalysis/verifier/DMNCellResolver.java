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
import org.drools.verifier.core.index.model.Rule;
import org.drools.verifier.core.index.model.meta.ConditionParent;

public class DMNCellResolver {

    private int columnIndex;
    private Rule rule;
    private ResolverProvider resolverProvider;
    private AnalyzerConfiguration analyzerConfiguration;
    private DMNColumnHeaderInstance dmnColumnHeaderInstance;

    public DMNCellResolver(final ResolverProvider resolverProvider,
                           final AnalyzerConfiguration analyzerConfiguration) {
        this.resolverProvider = resolverProvider;
        this.analyzerConfiguration = analyzerConfiguration;
    }

    public DMNCellResolver with(final Rule rule) {
        this.rule = rule;
        return this;
    }

    public DMNCellResolver with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    public DMNCellResolver with(final DMNColumnHeaderInstance dmnColumnHeaderInstance) {
        this.dmnColumnHeaderInstance = dmnColumnHeaderInstance;
        return this;
    }

    public DMNCell resolver() {
        if (dmnColumnHeaderInstance != null) {
            return resolver(dmnColumnHeaderInstance);
        } else {
            return resolver(getDMNColumnInstance());
        }
    }

    private DMNColumnHeaderInstance getDMNColumnInstance() {
        return resolverProvider.getDMNColumnHeaderInstanceResolver()
                .with(columnIndex)
                .with(rule)
                .resolve();
    }

    public DMNCell resolver(final DMNColumnHeaderInstance dmnColumnHeaderInstance) {

        final ConditionParent conditionParent = dmnColumnHeaderInstance.getConditionParents()
                .where(Field.name().is(dmnColumnHeaderInstance.getName()))
                .select()
                .first();

        if (conditionParent == null) {

            final DMNCell dmnCell = new DMNCell(dmnColumnHeaderInstance.getName(),
                                                resolverProvider.getStructureFieldResolver().resolve(dmnColumnHeaderInstance.getColumnDefinition()),
                                                analyzerConfiguration);

            dmnColumnHeaderInstance.getConditionParents()
                    .add(dmnCell);
            return dmnCell;
        } else {
            return (DMNCell) conditionParent;
        }
    }
}
