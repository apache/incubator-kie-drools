package org.kie.pmml.compiler.commons.factories;

import java.util.UUID;

import org.dmg.pmml.MapValues;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.expressions.KiePMMLMapValues;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLFieldColumnPairInstanceFactory.getKiePMMLFieldColumnPairs;
import static org.kie.pmml.compiler.commons.factories.KiePMMLInlineTableInstanceFactory.getKiePMMLInlineTable;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLMapValues</code> instance
 * out of <code>MapValues</code>s
 */
public class KiePMMLMapValuesInstanceFactory {

    private KiePMMLMapValuesInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLMapValues getKiePMMLMapValues(final MapValues mapValues) {
        DATA_TYPE dataType = mapValues.getDataType() != null ? DATA_TYPE.byName(mapValues.getDataType().value()) : null;
        KiePMMLMapValues.Builder builder = KiePMMLMapValues.builder(UUID.randomUUID().toString(),
                                                                    getKiePMMLExtensions(mapValues.getExtensions()),
                                                                    mapValues.getOutputColumn())
                .withKiePMMLInlineTable(getKiePMMLInlineTable(mapValues.getInlineTable()))
                .withDataType(dataType);
        if (mapValues.getDefaultValue() != null) {
            builder = builder.withDefaultValue(mapValues.getDefaultValue().toString());
        }
        if (mapValues.getMapMissingTo() != null) {
            builder = builder.withMapMissingTo(mapValues.getMapMissingTo().toString());
        }
        if (mapValues.hasFieldColumnPairs()) {
            builder = builder.withKiePMMLFieldColumnPairs(getKiePMMLFieldColumnPairs(mapValues.getFieldColumnPairs()));
        }
        return builder.build();
    }
}
