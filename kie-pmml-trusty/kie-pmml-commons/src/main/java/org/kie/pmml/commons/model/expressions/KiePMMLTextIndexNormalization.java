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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;


public class KiePMMLTextIndexNormalization extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = -1816258381871863674L;
    private KiePMMLInlineTable inlineTable;
    private String inField = "string";
    private String outField = "stem";
    private String regexField = "regex";
    private boolean recursive = false;
    private Boolean isCaseSensitive = null;
    private Integer maxLevenshteinDistance = null;
    private String wordSeparatorCharacterRE = null;
    private Boolean tokenize = null;


    private KiePMMLTextIndexNormalization(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public String replace(final String text,
                          final boolean isCaseSensitiveParam,
                          final int maxLevenshteinDistanceParam,
                          final boolean tokenizeParam,
                          final String wordSeparatorCharacterREParam) {
        Optional<String> retrieved = Optional.empty();
        if (inlineTable != null) {
            AtomicReference<String> toEdit = new AtomicReference<>(text);
            boolean isCaseSensitiveToUse = isCaseSensitive == null ? isCaseSensitiveParam :  isCaseSensitive;
            int maxLevenshteinDistanceToUse = maxLevenshteinDistance == null ? maxLevenshteinDistanceParam : maxLevenshteinDistance;
            boolean tokenizeToUse = tokenize == null ? tokenizeParam :  tokenize;
            String wordSeparatorCharacterREToUse = wordSeparatorCharacterRE == null ? wordSeparatorCharacterREParam : wordSeparatorCharacterRE;
            inlineTable.replace(toEdit, inField, outField, regexField, isCaseSensitiveToUse, maxLevenshteinDistanceToUse, tokenizeToUse, wordSeparatorCharacterREToUse);
            boolean replaced = !text.equals(toEdit.get());
            if (recursive) {
                while (replaced) {
                    String original = toEdit.get();
                    inlineTable.replace(toEdit, inField, outField, regexField, isCaseSensitiveToUse, maxLevenshteinDistanceToUse, tokenizeToUse, wordSeparatorCharacterREToUse);
                    replaced = !original.equals(toEdit.get());
                }
            }
            retrieved = Optional.of(toEdit.get());
        }
        return retrieved.orElse(text);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTextIndexNormalization> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("TextIndexNormalization-", () -> new KiePMMLTextIndexNormalization(name, extensions));
        }

        public Builder withInField(String inField) {
            if (inField != null) {
                toBuild.inField = inField;
            }
            return this;
        }

        public Builder withOutField(String outField) {
            if (outField != null) {
                toBuild.outField = outField;
            }
            return this;
        }

        public Builder withKiePMMLInlineTable(KiePMMLInlineTable inlineTable) {
            if (inlineTable != null) {
                toBuild.inlineTable = inlineTable;
            }
            return this;
        }

        public Builder withRegexField(String regexField) {
            if (regexField != null) {
                toBuild.regexField = regexField;
            }
            return this;
        }

        public Builder withRecursive(boolean recursive) {
            toBuild.recursive = recursive;
            return this;
        }

        public Builder withIsCaseSensitive(Boolean isCaseSensitive) {
            if (isCaseSensitive != null) {
                toBuild.isCaseSensitive = isCaseSensitive;
            }
            return this;
        }

        public Builder withMaxLevenshteinDistance(Integer maxLevenshteinDistance) {
            if (maxLevenshteinDistance != null) {
                toBuild.maxLevenshteinDistance = maxLevenshteinDistance;
            }
            return this;
        }

        public Builder withWordSeparatorCharacterRE(String wordSeparatorCharacterRE) {
            if (wordSeparatorCharacterRE != null) {
                toBuild.wordSeparatorCharacterRE = wordSeparatorCharacterRE;
            }
            return this;
        }

        public Builder withTokenize(Boolean tokenize) {
            if (tokenize != null) {
                toBuild.tokenize = tokenize;
            }
            return this;
        }
    }
}
