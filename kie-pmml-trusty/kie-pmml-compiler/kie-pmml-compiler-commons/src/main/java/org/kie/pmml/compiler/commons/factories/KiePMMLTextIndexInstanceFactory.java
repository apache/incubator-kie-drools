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

import org.apache.commons.text.StringEscapeUtils;
import org.dmg.pmml.TextIndex;
import org.kie.pmml.api.enums.COUNT_HITS;
import org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS;
import org.kie.pmml.commons.model.expressions.KiePMMLTextIndex;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpression;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLTextIndexNormalizationInstanceFactory.getKiePMMLTextIndexNormalizations;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTextIndex</code> instance
 * out of <code>TextIndex</code>s
 */
public class KiePMMLTextIndexInstanceFactory {

    private KiePMMLTextIndexInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLTextIndex getKiePMMLTextIndex(final TextIndex textIndex) {
        final LOCAL_TERM_WEIGHTS localTermWeights = textIndex.getLocalTermWeights() != null ?
                LOCAL_TERM_WEIGHTS.byName(textIndex.getLocalTermWeights().value()) : null;
        final COUNT_HITS countHits = textIndex.getCountHits() != null ?
                COUNT_HITS.byName(textIndex.getCountHits().value()) : null;
        final String wordSeparatorCharacterRE = textIndex.getWordSeparatorCharacterRE() != null ?
                StringEscapeUtils.escapeJava(textIndex.getWordSeparatorCharacterRE()) : null;
        return KiePMMLTextIndex.builder(textIndex.getTextField(),
                                        getKiePMMLExtensions(textIndex.getExtensions()),
                                        getKiePMMLExpression(textIndex.getExpression()))
                .withTextIndexNormalizations(getKiePMMLTextIndexNormalizations(textIndex.getTextIndexNormalizations()))
                .withLocalTermWeights(localTermWeights)
                .withIsCaseSensitive(textIndex.isCaseSensitive())
                .withMaxLevenshteinDistance(textIndex.getMaxLevenshteinDistance())
                .withCountHits(countHits)
                .withWordSeparatorCharacterRE(wordSeparatorCharacterRE)
                .withTokenize(textIndex.isTokenize())
                .build();
    }
}
