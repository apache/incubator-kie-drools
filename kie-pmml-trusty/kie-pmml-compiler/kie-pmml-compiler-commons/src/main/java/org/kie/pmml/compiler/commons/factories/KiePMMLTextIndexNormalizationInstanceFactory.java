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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.dmg.pmml.TextIndexNormalization;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndexNormalization;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLInlineTableInstanceFactory.getKiePMMLInlineTable;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTextIndexNormalization</code> instance
 * out of <code>TextIndexNormalization</code>s
 */
public class KiePMMLTextIndexNormalizationInstanceFactory {

    private KiePMMLTextIndexNormalizationInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLTextIndexNormalization> getKiePMMLTextIndexNormalizations(final List<TextIndexNormalization> textIndexNormalizations) {
        return textIndexNormalizations != null ?
                textIndexNormalizations.stream().map(KiePMMLTextIndexNormalizationInstanceFactory::getKiePMMLTextIndexNormalization).collect(Collectors.toList()) : Collections.emptyList();
    }

    static KiePMMLTextIndexNormalization getKiePMMLTextIndexNormalization(final TextIndexNormalization textIndexNormalization) {
        boolean isCaseSensitive = textIndexNormalization.isCaseSensitive() != null ?
                textIndexNormalization.isCaseSensitive() : false;
        final String wordSeparatorCharacterRE = textIndexNormalization.getWordSeparatorCharacterRE() != null ?
                StringEscapeUtils.escapeJava(textIndexNormalization.getWordSeparatorCharacterRE()) : null;
        boolean isTokenize = textIndexNormalization.isTokenize() != null ? textIndexNormalization.isTokenize() : false;

        return KiePMMLTextIndexNormalization.builder(UUID.randomUUID().toString(),
                                                     getKiePMMLExtensions(textIndexNormalization.getExtensions()))
                .withKiePMMLInlineTable(getKiePMMLInlineTable(textIndexNormalization.getInlineTable()))
                .withInField(textIndexNormalization.getInField())
                .withOutField(textIndexNormalization.getOutField())
                .withKiePMMLInlineTable(getKiePMMLInlineTable(textIndexNormalization.getInlineTable()))
                .withRegexField(textIndexNormalization.getRegexField())
                .withRecursive(textIndexNormalization.isRecursive())
                .withIsCaseSensitive(isCaseSensitive)
                .withMaxLevenshteinDistance(textIndexNormalization.getMaxLevenshteinDistance())
                .withWordSeparatorCharacterRE(wordSeparatorCharacterRE)
                .withTokenize(isTokenize)
                .build();
    }
}
