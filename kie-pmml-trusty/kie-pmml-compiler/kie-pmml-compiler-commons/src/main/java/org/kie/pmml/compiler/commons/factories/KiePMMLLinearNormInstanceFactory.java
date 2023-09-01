package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dmg.pmml.LinearNorm;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLLinearNorm</code> instance
 * out of <code>LinearNorm</code>s
 */
public class KiePMMLLinearNormInstanceFactory {

    private KiePMMLLinearNormInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLLinearNorm> getKiePMMLLinearNorms(List<LinearNorm> linearNorms) {
        return linearNorms.stream().map(KiePMMLLinearNormInstanceFactory::getKiePMMLLinearNorm).collect(Collectors.toList());
    }

    static KiePMMLLinearNorm getKiePMMLLinearNorm(LinearNorm linearNorm) {
        return new KiePMMLLinearNorm(UUID.randomUUID().toString(), getKiePMMLExtensions(linearNorm.getExtensions()),
                                     linearNorm.getOrig().doubleValue(),
                                     linearNorm.getNorm().doubleValue());
    }
}
