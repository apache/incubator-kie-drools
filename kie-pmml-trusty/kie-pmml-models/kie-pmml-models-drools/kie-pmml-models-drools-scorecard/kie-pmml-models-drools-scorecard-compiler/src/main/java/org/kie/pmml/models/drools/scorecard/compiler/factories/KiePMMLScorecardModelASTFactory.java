package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Field;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getTargetFieldType;

/**
 * Class used to generate a <code>KiePMMLDroolsAST</code> out of a
 * <code>DataDictionary</code> and a <code>Scorecard</code>
 */
public class KiePMMLScorecardModelASTFactory extends KiePMMLAbstractModelASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelASTFactory.class.getName());

    private KiePMMLScorecardModelASTFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>KiePMMLDroolsAST</code> built out of the given parameters.
     * It also <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between
     * original field' name and <b>original type/generated type</b> tupla
     *
     * @param fields
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                       final Scorecard model,
                                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                       final List<KiePMMLDroolsType> types) {
        logger.trace("getKiePMMLDroolsAST {} {} {}", fields, model, fieldTypeMap);
        DATA_TYPE targetType = getTargetFieldType(fields, model);
        List<OutputField> outputFields = model.getOutput() != null ? model.getOutput().getOutputFields() :
                Collections.emptyList();
        KiePMMLScorecardModelCharacteristicASTFactory factory =
                KiePMMLScorecardModelCharacteristicASTFactory.factory(fieldTypeMap, outputFields, targetType);
        if (model.isUseReasonCodes()) {
            factory = factory.withReasonCodes(model.getBaselineScore(), REASONCODE_ALGORITHM.byName(model.getReasonCodeAlgorithm().value()));
        }
        final List<KiePMMLDroolsRule> rules = factory
                .declareRulesFromCharacteristics(model.getCharacteristics(), "", model.getInitialScore());
        return new KiePMMLDroolsAST(types, rules);
    }
}
