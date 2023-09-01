package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.False;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;

import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLFalsePredicate;

public class KiePMMLFalsePredicateInstanceFactoryTest {

    @Test
    void getKiePMMLFalsePredicate() {
        False toConvert = new False();
        KiePMMLFalsePredicate retrieved = KiePMMLFalsePredicateInstanceFactory.getKiePMMLFalsePredicate(toConvert);
        commonVerifyKiePMMLFalsePredicate(retrieved, toConvert);
    }
}