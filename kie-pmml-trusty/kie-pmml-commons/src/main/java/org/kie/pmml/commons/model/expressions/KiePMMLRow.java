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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.kie.pmml.commons.model.expressions.KiePMMLTextIndex.DEFAULT_TOKENIZER;
import static org.kie.pmml.commons.model.expressions.KiePMMLTextIndex.evaluateLevenshteinDistance;
import static org.kie.pmml.commons.model.expressions.KiePMMLTextIndex.splitText;

/**
 * KiePMML representation of an InlineTable <b>Row</b>
 */
public class KiePMMLRow implements Serializable {

    private static final long serialVersionUID = -5245266051098683475L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRow.class);
    private final Map<String, Object> columnValues;

    public KiePMMLRow(Map<String, Object> columnValues) {
        this.columnValues = columnValues;
    }

    public Map<String, Object> getColumnValues() {
        return Collections.unmodifiableMap(columnValues);
    }

    public Optional<Object> evaluate(final Map<String, Object> columnPairsMap, final String outputColumn,
                                     final String regexField) {
        boolean matching = true;
        boolean isRegex =
                regexField != null && columnValues.containsKey(regexField) && (boolean) columnValues.get(regexField);
        for (Map.Entry<String, Object> columnPairEntry : columnPairsMap.entrySet()) {
            Object value = columnValues.get(columnPairEntry.getKey());
            matching = isRegex ? isRegexMatching(value.toString(), (String) columnPairEntry.getValue()) :
                    isMatching(value, columnPairEntry.getValue());
            if (!matching) {
                break;
            }
        }
        return matching ? Optional.ofNullable(columnValues.get(outputColumn)) : Optional.empty();
    }

    public void replace(final AtomicReference<String> text,
                        final String inField,
                        final String outField,
                        final String regexField,
                        final boolean isCaseSensitive,
                        final int maxLevenshteinDistance,
                        final boolean tokenize,
                        final String wordSeparatorCharacterRE) {
        boolean isRegex =
                regexField != null && columnValues.containsKey(regexField) &&  Boolean.parseBoolean((String)columnValues.get(regexField));
        String replaced = isRegex ? regexReplace(text.get(), (String) columnValues.get(outField), (String) columnValues.get(inField))
                : replace(text.get(), (String) columnValues.get(outField), (String)  columnValues.get(inField), isCaseSensitive, maxLevenshteinDistance, tokenize, wordSeparatorCharacterRE);
        text.set(replaced);
    }

    boolean isMatching(Object original, Object value) {
        return Objects.equals(original, value) || (original != null && value != null && Objects.equals(original.toString(), value.toString()));
    }

    boolean isRegexMatching(String original, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(original).find();
    }

    String replace(String original, String replacement, String term, boolean isCaseSensitive, int maxLevenshteinDistance, boolean tokenize, String wordSeparatorCharacterRE) {
        logger.debug("replace {} {} {} {} {}", original, replacement, term, isCaseSensitive, maxLevenshteinDistance);
        int caseSensitiveFlag = isCaseSensitive ? 0 :  CASE_INSENSITIVE;
        Pattern pattern = tokenize ? Pattern.compile(wordSeparatorCharacterRE, caseSensitiveFlag) : Pattern.compile(DEFAULT_TOKENIZER);
        List<String> terms = splitText(replacement, pattern);
        String replacementToUse = String.join(" ", terms);
        List<String> texts = splitText(original, pattern);
        int batchSize = terms.size();
        int limit = texts.size() - batchSize + 1;
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(maxLevenshteinDistance);
        String toReturn = original;
        for (int i = 0; i < limit; i++) {
            String text = String.join(" ", texts.subList(i, i + batchSize));
            int distance = evaluateLevenshteinDistance(levenshteinDistance, term, text);
            if (distance > -1) {
                toReturn = toReturn.replace(text, replacementToUse);
            }
        }
        return toReturn;
    }

    String regexReplace(String original, String replacement, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(original).replaceAll(replacement);
    }
}
