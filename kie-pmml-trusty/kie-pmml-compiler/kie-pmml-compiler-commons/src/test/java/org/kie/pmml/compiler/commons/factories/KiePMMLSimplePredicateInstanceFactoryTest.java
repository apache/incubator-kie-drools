package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.DataField;
import org.dmg.pmml.Field;
import org.dmg.pmml.SimplePredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;

import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomSimplePredicate;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLSimplePredicate;

public class KiePMMLSimplePredicateInstanceFactoryTest {

    @Test
    void getKiePMMLSimplePredicate() {
        List<Field<?>> fields = IntStream.range(0, 3).mapToObj(i -> getRandomDataField()).collect(Collectors.toList());
        final SimplePredicate toConvert = getRandomSimplePredicate((DataField) fields.get(0));
        final KiePMMLSimplePredicate retrieved =
                KiePMMLSimplePredicateInstanceFactory.getKiePMMLSimplePredicate(toConvert, fields);
        commonVerifyKiePMMLSimplePredicate(retrieved, toConvert, (DataField) fields.get(0));
    }
}