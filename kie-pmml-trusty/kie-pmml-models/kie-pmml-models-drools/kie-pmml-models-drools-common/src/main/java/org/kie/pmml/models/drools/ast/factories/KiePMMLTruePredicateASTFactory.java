package org.kie.pmml.models.drools.ast.factories;

import org.drools.util.StringUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;

/**
 * Class used to generate a <code>KiePMMLDroolsRule</code> out of a <code>True</code> predicate
 */
public class KiePMMLTruePredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicateASTFactory.class.getName());

    protected KiePMMLTruePredicateASTFactory(final PredicateASTFactoryData predicateASTFactoryData) {
        super(predicateASTFactoryData);
    }

    public static KiePMMLTruePredicateASTFactory factory(final PredicateASTFactoryData predicateASTFactoryData) {
        return new KiePMMLTruePredicateASTFactory(predicateASTFactoryData);
    }

    public void declareRuleFromTruePredicateWithResult(final Object result,
                                                       final boolean isFinalLeaf) {
        String statusToSet = isFinalLeaf ? DONE : predicateASTFactoryData.getCurrentRule();
        KiePMMLDroolsRule.Builder builder = getRuleBuilder(statusToSet);
        KiePMMLTruePredicateWithResultASTFactory.declareRuleFromTruePredicate(builder, predicateASTFactoryData.getRules(), result, isFinalLeaf);
    }

    public void declareRuleFromTruePredicateWithAccumulation(final Number toAccumulate,
                                                             final String statusToSet,
                                                             final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                             final boolean isLastCharacteristic) {
        KiePMMLDroolsRule.Builder builder = getRuleBuilder(statusToSet)
                .withAccumulation(toAccumulate);
        KiePMMLTruePredicateWithAccumulationASTFactory.declareRuleFromTruePredicate(builder, predicateASTFactoryData.getRules(), statusToSet, reasonCodeAndValue, isLastCharacteristic);
    }

    protected KiePMMLDroolsRule.Builder getRuleBuilder(final String statusToSet) {
        logger.trace("getRuleBuilder {} {} {} {}", predicateASTFactoryData.getPredicate(), predicateASTFactoryData.getParentPath(), predicateASTFactoryData.getCurrentRule(), statusToSet);
        String statusConstraint = StringUtils.isEmpty(predicateASTFactoryData.getParentPath()) ? KiePMMLAbstractModelASTFactory.STATUS_NULL : String.format(KiePMMLAbstractModelASTFactory.STATUS_PATTERN, predicateASTFactoryData.getParentPath());
        return KiePMMLDroolsRule.builder(predicateASTFactoryData.getCurrentRule(), statusToSet, predicateASTFactoryData.getOutputFields())
                .withStatusConstraint(statusConstraint);
    }
}
