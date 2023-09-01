package org.kie.pmml.models.drools.ast.factories;

import java.util.List;

import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code> out of a <code>SimplePredicate</code>
 */
public class KiePMMLSimplePredicateWithAccumulationASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimplePredicateWithAccumulationASTFactory.class.getName());

    public static void declareRuleFromSimplePredicateSurrogateTrueMatcher(
            KiePMMLDroolsRule.Builder builder,
            final List<KiePMMLDroolsRule> rules,
            final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromSimplePredicateSurrogateTrueMatcher {} {} {}", builder, rules, isLastCharacteristic);
        if (isLastCharacteristic) {
            builder = builder.withAccumulationResult(true)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }

    public static void declareRuleFromSimplePredicateSurrogateFalseMatcher(
            KiePMMLDroolsRule.Builder builder,
            final List<KiePMMLDroolsRule> rules) {
        logger.trace("declareRuleFromSimplePredicateSurrogateFalseMatcher {} {}", builder, rules);
        rules.add(builder.build());
    }

    /**
     * This method will create a <b>rule</b> that, in the RHS,
     * 1) update the status (used for flowing between rules)
     * 2) add <i>outputfields</i> to result variables
     * 3) eventually set the value to accumulate
     * <p>
     * rule "_ResidenceStateScore_1"
     * when
     * $statusHolder : KiePMMLStatusHolder( status == "_ResidenceStateScore" )
     * <p>
     * RESIDENCESTATE( value == "KN" )
     * then
     * <p>
     * $statusHolder.setStatus("_ResidenceStateScore_1");
     * $statusHolder.accumulate("10.0");
     * update($statusHolder);
     * <p>
     * end
     * <p>
     * end
     * @param builder
     * @param rules
     * @param isLastCharacteristic
     */
    public static void declareRuleFromSimplePredicate(KiePMMLDroolsRule.Builder builder,
                                                      final List<KiePMMLDroolsRule> rules,
                                                      final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromSimplePredicate {} {} {}", builder, rules, isLastCharacteristic);
        if (isLastCharacteristic) {
            builder = builder.withAccumulationResult(true)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
