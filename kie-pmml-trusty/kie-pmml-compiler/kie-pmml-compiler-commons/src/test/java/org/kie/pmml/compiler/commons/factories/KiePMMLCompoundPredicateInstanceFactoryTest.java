package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Field;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomCompoundPredicate;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKKiePMMLCompoundPredicate;

public class KiePMMLCompoundPredicateInstanceFactoryTest {

    @Test
    void getKiePMMLCompoundPredicate() {
        List<Field<?>> fields = IntStream.range(0, 3).mapToObj(i -> getRandomDataField()).collect(Collectors.toList());
        final CompoundPredicate toConvert = getRandomCompoundPredicate(fields);
        final KiePMMLCompoundPredicate retrieved =
                KiePMMLCompoundPredicateInstanceFactory.getKiePMMLCompoundPredicate(toConvert, fields);
        commonVerifyKKiePMMLCompoundPredicate(retrieved, toConvert);
    }
}