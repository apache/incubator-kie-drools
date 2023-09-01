package org.kie.pmml.models.drools.ast.factories;

import java.util.List;

import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code> out of a <code>SimpleSetPredicate</code>
 */
public class KiePMMLSimpleSetPredicateWithResultASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimpleSetPredicateWithResultASTFactory.class.getName());

    public static void declareRuleFromSimpleSetPredicate(KiePMMLDroolsRule.Builder builder,
                                                         final List<KiePMMLDroolsRule> rules,
                                                         final Object result,
                                                         final boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimpleSetPredicate {} {} {} {}", builder, rules, result, isFinalLeaf);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
