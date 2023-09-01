package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;

import org.dmg.pmml.True;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLTruePredicate</code> instance
 * out of <code>True</code>
 */
public class KiePMMLTruePredicateInstanceFactory {

    private KiePMMLTruePredicateInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLTruePredicate getKiePMMLTruePredicate(final True truePre) {
        return KiePMMLTruePredicate.builder(Collections.emptyList()).build();
    }
}
