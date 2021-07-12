/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.commons.model.expressions;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * KiePMML representation of an InlineTable <b>Row</b>
 */
public class KiePMMLRow implements Serializable {

    private static final long serialVersionUID = -5245266051098683475L;
    private final Map<String, Object> columnValues;

    public KiePMMLRow(Map<String, Object> columnValues) {
        this.columnValues = columnValues;
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

    public void replace(final AtomicReference<String> text, final String inField, final String outField, final String regexField) {
        boolean isRegex =
                regexField != null && columnValues.containsKey(regexField) && (boolean) columnValues.get(regexField);
        String replaced = isRegex ? regexReplace(text.get(), (String) columnValues.get(outField), (String) columnValues.get(inField))
                : replace(text.get(), (String) columnValues.get(outField), (String)  columnValues.get(inField));
        text.set(replaced);
    }

    boolean isMatching(Object original, Object value) {
        return Objects.equals(original, value);
    }

    boolean isRegexMatching(String original, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(original).find();
    }

    String replace(String original, String replacement, String value) {
        return original.replaceAll(value, replacement);
    }

    String regexReplace(String original, String replacement, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(original).replaceAll(replacement);
    }
}
