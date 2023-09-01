package org.kie.pmml.compiler.commons.factories;

import org.dmg.pmml.SimpleSetPredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomSimpleSetPredicate;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLSimpleSetPredicate;

public class KiePMMLSimpleSetPredicateInstanceFactoryTest {

    @Test
    void getKiePMMLSimpleSetPredicate() {
        final SimpleSetPredicate toConvert = getRandomSimpleSetPredicate();
        final KiePMMLSimpleSetPredicate retrieved = KiePMMLSimpleSetPredicateInstanceFactory.getKiePMMLSimpleSetPredicate(toConvert);
        commonVerifyKiePMMLSimpleSetPredicate(retrieved, toConvert);
    }
}