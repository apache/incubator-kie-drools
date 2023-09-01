package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.UUID;

import org.dmg.pmml.Constant;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.expressions.KiePMMLConstant;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLConstant</code> code-generators
 * out of <code>Constant</code>s
 */
public class KiePMMLConstantInstanceFactory {

    private KiePMMLConstantInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLConstant getKiePMMLConstant(final Constant constant) {
        DATA_TYPE dataType = constant.getDataType() != null ? DATA_TYPE.byName(constant.getDataType().value()) : null;
        return new KiePMMLConstant(UUID.randomUUID().toString(), Collections.emptyList(), constant.getValue(), dataType);
    }

}
