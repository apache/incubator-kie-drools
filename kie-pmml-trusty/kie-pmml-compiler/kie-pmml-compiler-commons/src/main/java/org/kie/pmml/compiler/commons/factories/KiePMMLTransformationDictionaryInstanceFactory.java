package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.UUID;

import org.dmg.pmml.Field;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;

import static org.kie.pmml.compiler.commons.factories.KiePMMLDefineFunctionInstanceFactory.getKiePMMLDefineFunctions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLDerivedFieldInstanceFactory.getKiePMMLDerivedFields;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>KiePMMLTransformationDictionary</code> instance
 * out of <code>TransformationDictionary</code>s
 */
public class KiePMMLTransformationDictionaryInstanceFactory {

    private KiePMMLTransformationDictionaryInstanceFactory() {
        // Avoid instantiation
    }

    public static KiePMMLTransformationDictionary getKiePMMLTransformationDictionary(final TransformationDictionary toConvert,
                                                                                     final List<Field<?>> fields) {
        final List<KiePMMLDerivedField> kiePMMLDerivedFields = getKiePMMLDerivedFields(toConvert.getDerivedFields(),
                                                                                       fields);
        final List<KiePMMLDefineFunction> kiePMMLDefineFunctions =
                getKiePMMLDefineFunctions(toConvert.getDefineFunctions());
        return KiePMMLTransformationDictionary.builder(UUID.randomUUID().toString(), getKiePMMLExtensions(toConvert.getExtensions()))
                .withDefineFunctions(kiePMMLDefineFunctions)
                .withDerivedFields(kiePMMLDerivedFields)
                .build();
    }
}
