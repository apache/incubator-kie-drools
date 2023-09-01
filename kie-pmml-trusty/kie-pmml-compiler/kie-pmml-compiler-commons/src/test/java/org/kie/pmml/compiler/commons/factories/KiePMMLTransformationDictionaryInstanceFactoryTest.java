package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.TransformationDictionary;
import org.junit.jupiter.api.Test;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTransformationDictionary;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDefineFunction;
import static org.kie.pmml.compiler.commons.factories.InstanceFactoriesTestCommon.commonVerifyKiePMMLDerivedField;

public class KiePMMLTransformationDictionaryInstanceFactoryTest {

    @Test
    void getKiePMMLTransformationDictionary() {
        final TransformationDictionary toConvert = getRandomTransformationDictionary();
        KiePMMLTransformationDictionary retrieved =
                KiePMMLTransformationDictionaryInstanceFactory.getKiePMMLTransformationDictionary(toConvert,
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

        List<DefineFunction> defineFunctions = toConvert.getDefineFunctions();
        List<KiePMMLDefineFunction> defineFunctionsToVerify = retrieved.getDefineFunctions();
        assertThat(defineFunctionsToVerify).hasSameSizeAs(defineFunctions);
        defineFunctions.forEach(defineFunctionSource -> {
            Optional<KiePMMLDefineFunction> defineFunctionToVerify =
                    defineFunctionsToVerify.stream().filter(param -> param.getName().equals(defineFunctionSource.getName()))
                            .findFirst();
            assertThat(defineFunctionToVerify).isPresent();
            commonVerifyKiePMMLDefineFunction(defineFunctionToVerify.get(), defineFunctionSource);
        });
    }
}