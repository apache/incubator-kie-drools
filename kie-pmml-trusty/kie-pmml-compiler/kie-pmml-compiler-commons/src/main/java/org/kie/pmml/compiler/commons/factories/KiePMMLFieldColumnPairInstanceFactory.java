package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.stream.Collectors;

import org.dmg.pmml.FieldColumnPair;
import org.kie.pmml.commons.model.expressions.KiePMMLFieldColumnPair;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFieldColumnPair</code> instance
 * out of <code>FieldColumnPair</code>s
 */
public class KiePMMLFieldColumnPairInstanceFactory {

    private KiePMMLFieldColumnPairInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLFieldColumnPair> getKiePMMLFieldColumnPairs(List<FieldColumnPair> fieldColumnPairs) {
        return fieldColumnPairs.stream().map(KiePMMLFieldColumnPairInstanceFactory::getKiePMMLFieldColumnPair).collect(Collectors.toList());
    }

    static KiePMMLFieldColumnPair getKiePMMLFieldColumnPair(final FieldColumnPair fieldColumnPair) {
        return new KiePMMLFieldColumnPair(fieldColumnPair.getField().getValue(),
                                          getKiePMMLExtensions(fieldColumnPair.getExtensions()),
                                          fieldColumnPair.getColumn());
    }
}
