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
