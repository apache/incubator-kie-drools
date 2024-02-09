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
package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

/**
 * KiePMML representation of a <b>FieldColumnPair</b>
 */
public class KiePMMLInlineTable extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -8218151315846757917L;
    private final List<KiePMMLRow> rows;

    public KiePMMLInlineTable(String name, List<KiePMMLExtension> extensions, List<KiePMMLRow> rows) {
        super(name, extensions);
        this.rows = rows;
    }

    public List<KiePMMLRow> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public Optional<Object> evaluate(final Map<String, Object> columnPairsMap, final String outputColumn, final String regexField) {
        return rows.stream()
                .map(row -> row.evaluate(columnPairsMap, outputColumn, regexField))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }

    public void replace(final AtomicReference<String> text,
                        final String inField,
                        final String outField,
                        final String regexField,
                        final boolean isCaseSensitive,
                        final int maxLevenshteinDistance,
                        final boolean tokenize,
                        final String wordSeparatorCharacterRE) {
        rows.forEach(row -> row.replace(text, inField, outField, regexField, isCaseSensitive, maxLevenshteinDistance, tokenize, wordSeparatorCharacterRE));
    }
}
