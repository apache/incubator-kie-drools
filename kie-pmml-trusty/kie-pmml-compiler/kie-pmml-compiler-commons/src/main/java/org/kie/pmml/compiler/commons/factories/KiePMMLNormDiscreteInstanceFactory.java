package org.kie.pmml.compiler.commons.factories;

import java.util.List;

import org.dmg.pmml.NormDiscrete;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.expressions.KiePMMLNormDiscrete;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLNormDiscrete</code> instance
 * out of <code>NormDiscrete</code>s
 */
public class KiePMMLNormDiscreteInstanceFactory {

    private KiePMMLNormDiscreteInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLNormDiscrete getKiePMMLNormDiscrete(final NormDiscrete normDiscrete) {
        List<KiePMMLExtension> extensions = getKiePMMLExtensions(normDiscrete.getExtensions());
        return new KiePMMLNormDiscrete(normDiscrete.getField().getValue(),
                                       extensions,
                                       normDiscrete.getValue().toString(),
                                       normDiscrete.getMapMissingTo());
    }
}
