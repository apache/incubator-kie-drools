package org.kie.pmml.models.drools.ast.factories;

import java.util.List;

import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate a <code>KiePMMLDroolsRule</code> out of a <code>True</code> predicate to be used with <b>result</b>
 */
public class KiePMMLTruePredicateWithResultASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicateWithResultASTFactory.class.getName());

    public static void declareRuleFromTruePredicate(
            KiePMMLDroolsRule.Builder builder,
            final List<KiePMMLDroolsRule> rules,
            final Object result,
            final boolean isFinalLeaf) {
        logger.trace("declareRuleFromTruePredicate {} {} {}", builder, result, isFinalLeaf);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
