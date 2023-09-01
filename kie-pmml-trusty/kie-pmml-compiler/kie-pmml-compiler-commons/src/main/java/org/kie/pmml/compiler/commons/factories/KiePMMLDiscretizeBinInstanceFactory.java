package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.dmg.pmml.DiscretizeBin;
import org.kie.pmml.commons.model.expressions.KiePMMLDiscretizeBin;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLDiscretizeBin</code> instance
 * out of <code>DiscretizeBin</code>s
 */
public class KiePMMLDiscretizeBinInstanceFactory {

    private KiePMMLDiscretizeBinInstanceFactory() {
        // Avoid instantiation
    }

    static List<KiePMMLDiscretizeBin> getKiePMMLDiscretizeBins(final List<DiscretizeBin> discretizeBins) {
        return discretizeBins.stream().map(KiePMMLDiscretizeBinInstanceFactory::getKiePMMLDiscretizeBin).collect(Collectors.toList());
    }

    static KiePMMLDiscretizeBin getKiePMMLDiscretizeBin(final DiscretizeBin discretizeBin) {
        KiePMMLInterval interval = KiePMMLIntervalInstanceFactory.getKiePMMLInterval(discretizeBin.getInterval());
        String binValue = discretizeBin.getBinValue() != null ? discretizeBin.getBinValue().toString() : null;
        return new KiePMMLDiscretizeBin(UUID.randomUUID().toString(),
                                        getKiePMMLExtensions(discretizeBin.getExtensions()),
                                        binValue,
                                        interval);
    }
}
