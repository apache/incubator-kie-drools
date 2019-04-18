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
import org.drools.verifier.core.index.model.ColumnType;
import org.drools.verifier.core.index.model.Rule;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.validation.dtanalysis.DMNAction;

public class OutputBuilder {

    private int columnIndex;
    private Util util;
    private ResolverProvider resolverProvider;
    private AnalyzerConfiguration configuration;
    private LiteralExpression literalExpression;
    private Rule rule;

    public OutputBuilder(final Util util,
                         final ResolverProvider resolverProvider,
                         final AnalyzerConfiguration configuration) {
        this.util = util;
        this.resolverProvider = resolverProvider;
        this.configuration = configuration;
    }

    public DMNAction build() {

        final DMNAction dmnAction = new DMNAction(resolverProvider.getColumnResolver().resolve(columnIndex, ColumnType.RHS),
                                                  util.valuesFromNode(literalExpression),
                                                  configuration);

        final DMNCell dmnCell = resolverProvider.getDMNCellResolver()
                .with(columnIndex)
                .with(rule)
                .resolver();

        dmnCell.getActions().add(dmnAction);

        return dmnAction;
    }

    public OutputBuilder with(final int columnIndex) {
        this.columnIndex = columnIndex;
        return this;
    }

    public OutputBuilder with(final LiteralExpression literalExpression) {
        this.literalExpression = literalExpression;
        return this;
    }

    public OutputBuilder with(final Rule rule) {
        this.rule = rule;
        return this;
    }
}
