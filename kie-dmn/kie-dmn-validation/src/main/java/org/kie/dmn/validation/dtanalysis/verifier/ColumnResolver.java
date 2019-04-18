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
import org.drools.verifier.core.index.model.Column;
import org.drools.verifier.core.index.model.ColumnType;

public class ColumnResolver {

    private Index index;
    private AnalyzerConfiguration configuration;

    public ColumnResolver(final Index index,
                          final AnalyzerConfiguration configuration) {
        this.index = index;
        this.configuration = configuration;
    }

    public Column resolve(final int columnIndex,
                          final ColumnType columnType) {
        final Column column = index.getColumns().where(Column.index().is(columnIndex)).select().first();
        if (column == null) {
            Column newColumn = new Column(columnIndex,
                                          columnType,
                                          configuration);
            index.getColumns().add(newColumn);
            return newColumn;
        } else {
            return column;
        }
    }
}
