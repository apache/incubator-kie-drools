package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.NormContinuous;
import org.kie.pmml.api.enums.OUTLIER_TREATMENT_METHOD;
import org.kie.pmml.commons.model.expressions.KiePMMLLinearNorm;
import org.kie.pmml.commons.model.expressions.KiePMMLNormContinuous;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLLinearNormInstanceFactory.getKiePMMLLinearNorms;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLNormContinuous</code> instance
 * out of <code>NormContinuous</code>s
 */
public class KiePMMLNormContinuousInstanceFactory {

    private KiePMMLNormContinuousInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLNormContinuous getKiePMMLNormContinuous(final NormContinuous normContinuous) {
        final List<KiePMMLLinearNorm> linearNorms = normContinuous.hasLinearNorms() ?
                getKiePMMLLinearNorms(normContinuous.getLinearNorms()) : Collections.emptyList();
        final OUTLIER_TREATMENT_METHOD outlierTreatmentMethod = normContinuous.getOutliers() != null ? OUTLIER_TREATMENT_METHOD.byName(normContinuous.getOutliers().value()) : null;
        return new KiePMMLNormContinuous(normContinuous.getField().getValue(), getKiePMMLExtensions(normContinuous.getExtensions()), linearNorms, outlierTreatmentMethod, normContinuous.getMapMissingTo());
    }

}
