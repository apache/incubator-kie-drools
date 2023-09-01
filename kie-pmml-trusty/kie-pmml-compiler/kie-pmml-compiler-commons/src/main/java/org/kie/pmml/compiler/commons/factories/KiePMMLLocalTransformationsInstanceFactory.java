package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dmg.pmml.Field;
import org.dmg.pmml.LocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;

import static org.kie.pmml.compiler.commons.factories.KiePMMLDerivedFieldInstanceFactory.getKiePMMLDerivedField;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>KiePMMLLocalTransformations</code> code-generators
 * out of <code>LocalTransformations</code>s
 */
public class KiePMMLLocalTransformationsInstanceFactory {

    private KiePMMLLocalTransformationsInstanceFactory() {
        // Avoid instantiation
    }

    public static KiePMMLLocalTransformations getKiePMMLLocalTransformations(final LocalTransformations localTransformations,
                                                                      final List<Field<?>> fields) {
        final List<KiePMMLDerivedField> kiePMMLDerivedFields =
                localTransformations.getDerivedFields().stream()
                        .map(derivedField -> getKiePMMLDerivedField(derivedField, fields))
                        .collect(Collectors.toList());
        return KiePMMLLocalTransformations.builder(UUID.randomUUID().toString(), Collections.emptyList())
                .withDerivedFields(kiePMMLDerivedFields)
                .build();
    }

}
