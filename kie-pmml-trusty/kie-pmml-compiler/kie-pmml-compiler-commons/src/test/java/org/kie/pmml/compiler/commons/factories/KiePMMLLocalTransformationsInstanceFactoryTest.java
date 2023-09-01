package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.dmg.pmml.DerivedField;
import org.dmg.pmml.LocalTransformations;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomLocalTransformations;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDerivedField;

public class KiePMMLLocalTransformationsInstanceFactoryTest {

    @Test
    void getKiePMMLLocalTransformations() {
        final LocalTransformations toConvert = getRandomLocalTransformations();
        KiePMMLLocalTransformations retrieved =
                KiePMMLLocalTransformationsInstanceFactory.getKiePMMLLocalTransformations(toConvert,
                        Collections.emptyList());
        assertThat(retrieved).isNotNull();

        List<DerivedField> derivedFields = toConvert.getDerivedFields();
        List<KiePMMLDerivedField> derivedFieldsToVerify = retrieved.getDerivedFields();
        assertThat(derivedFieldsToVerify).hasSameSizeAs(derivedFields);
        derivedFields.forEach(derivedFieldSource -> {
            Optional<KiePMMLDerivedField> derivedFieldToVerify =
                    derivedFieldsToVerify.stream().filter(param -> param.getName().equals(derivedFieldSource.getName().getValue()))
                            .findFirst();
            assertThat(derivedFieldToVerify).isPresent();
            commonVerifyKiePMMLDerivedField(derivedFieldToVerify.get(), derivedFieldSource);
        });
    }
}