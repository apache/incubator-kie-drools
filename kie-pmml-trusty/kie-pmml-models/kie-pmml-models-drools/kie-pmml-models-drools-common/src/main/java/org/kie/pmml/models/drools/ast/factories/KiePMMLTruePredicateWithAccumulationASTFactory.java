package org.kie.pmml.models.drools.ast.factories;

import java.util.List;

import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate a <code>KiePMMLDroolsRule</code> out of a <code>True</code> predicate to be used with <b>accumulation</b>
 */
public class KiePMMLTruePredicateWithAccumulationASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicateWithAccumulationASTFactory.class.getName());

    public static void declareRuleFromTruePredicate(KiePMMLDroolsRule.Builder builder,
                                                    final List<KiePMMLDroolsRule> rules,
                                                    final String statusToSet,
                                                    final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                    boolean isLastCharacteristic) {
        logger.trace("declareRuleFromTruePredicate {} {} {}", builder, statusToSet, isLastCharacteristic);
        if (isLastCharacteristic) {
            builder = builder.withAccumulationResult(true)
                    .withResultCode(ResultCode.OK);

        }
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        rules.add(builder.build());
    }
}
