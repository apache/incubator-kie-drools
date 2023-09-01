package org.kie.pmml.compiler.commons.factories;

import java.util.List;
import java.util.UUID;

import org.dmg.pmml.Apply;
import org.kie.pmml.commons.model.expressions.KiePMMLApply;
import org.kie.pmml.commons.model.expressions.KiePMMLExpression;

import static org.kie.pmml.compiler.commons.factories.KiePMMLExpressionInstanceFactory.getKiePMMLExpressions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionInstanceFactory.getKiePMMLExtensions;

/**
 * Class meant to provide <i>helper</i> method to retrieve <code>KiePMMLApply</code> instance
 * out of <code>Apply</code>s
 */
public class KiePMMLApplyInstanceFactory {

    private KiePMMLApplyInstanceFactory() {
        // Avoid instantiation
    }

    static KiePMMLApply getKiePMMLApply(final Apply apply) {
        final String invalidValueTreatment = apply.getInvalidValueTreatment() != null ?
                apply.getInvalidValueTreatment().value() : null;
        final List<KiePMMLExpression> kiePMMLExpressions = getKiePMMLExpressions(apply.getExpressions());
        final KiePMMLApply.Builder builder = KiePMMLApply.builder(UUID.randomUUID().toString(),
                                                                  getKiePMMLExtensions(apply.getExtensions()),
                                                                  apply.getFunction())
                .withKiePMMLExpressions(kiePMMLExpressions)
                .withMapMissingTo(apply.getMapMissingTo())
                .withDefaultValue(apply.getDefaultValue())
                .withInvalidValueTreatmentMethod(invalidValueTreatment);
        return builder.build();
    }

}
