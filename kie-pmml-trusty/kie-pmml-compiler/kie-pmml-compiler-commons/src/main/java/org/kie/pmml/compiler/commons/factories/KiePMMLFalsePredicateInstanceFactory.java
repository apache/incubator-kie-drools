package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;

import org.dmg.pmml.False;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLFalsePredicate</code> instance
 * out of <code>False</code>s
 */
public class KiePMMLFalsePredicateInstanceFactory {

    private KiePMMLFalsePredicateInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLFalsePredicate getKiePMMLFalsePredicate(final False falsePre) {
        return KiePMMLFalsePredicate.builder(Collections.emptyList()).build();
    }
}
