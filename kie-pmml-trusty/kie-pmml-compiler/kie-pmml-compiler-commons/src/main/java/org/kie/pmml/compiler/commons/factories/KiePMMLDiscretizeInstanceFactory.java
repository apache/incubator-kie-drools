package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.Discretize;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretize;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretizeBin;

import static org.kie.pmml.compiler.commons.factories.KiePMMLDiscretizeBinInstanceFactory.getKiePMMLDiscretizeBins;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDiscretize</code> instance
 * out of <code>Discretize</code>s
 */
public class KiePMMLDiscretizeInstanceFactory {

    private KiePMMLDiscretizeInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLDiscretize getKiePMMLDiscretize(final Discretize discretize) {
        List<KiePMMLDiscretizeBin> discretizeBins = discretize.hasDiscretizeBins() ?
                getKiePMMLDiscretizeBins(discretize.getDiscretizeBins()) : Collections.emptyList();
        String mapMissingTo = discretize.getMapMissingTo() != null ? discretize.getMapMissingTo().toString() : null;
        String defaultValue = discretize.getDefaultValue() != null ? discretize.getDefaultValue().toString() : null;
        DATA_TYPE dataType = discretize.getDataType() != null ? DATA_TYPE.byName(discretize.getDataType().value()) :
                null;
        return new KiePMMLDiscretize(discretize.getField().getValue(),
                                     getKiePMMLExtensions(discretize.getExtensions()),
                                     discretizeBins,
                                     mapMissingTo,
                                     defaultValue,
                                     dataType);
    }
}
