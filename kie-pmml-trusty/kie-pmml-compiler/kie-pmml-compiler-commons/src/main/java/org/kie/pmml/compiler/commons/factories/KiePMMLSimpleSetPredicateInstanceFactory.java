package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.SimpleSetPredicate;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getObjectsFromArray;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLSimpleSetPredicate</code> instance
 * out of <code>SimpleSetPredicate</code>s
 */
public class KiePMMLSimpleSetPredicateInstanceFactory {

    private KiePMMLSimpleSetPredicateInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLSimpleSetPredicate getKiePMMLSimpleSetPredicate(final SimpleSetPredicate simpleSetPredicate) {
        return KiePMMLSimpleSetPredicate.builder(simpleSetPredicate.getField().getValue(),
                                                 getKiePMMLExtensions(simpleSetPredicate.getExtensions()),
                                                 ARRAY_TYPE.byName(simpleSetPredicate.getArray().getType().value()),
                                                 IN_NOTIN.byName(simpleSetPredicate.getBooleanOperator().value()))
                .withValues(getObjectsFromArray(simpleSetPredicate.getArray()))
                .build();
    }
}
