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
        return KiePMMLTextIndex.builder(textIndex.getTextField().getValue(),
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
