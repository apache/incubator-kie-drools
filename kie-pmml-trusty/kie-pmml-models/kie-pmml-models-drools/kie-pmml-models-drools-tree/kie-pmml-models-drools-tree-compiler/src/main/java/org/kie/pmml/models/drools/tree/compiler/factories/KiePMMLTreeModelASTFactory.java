package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Field;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.api.utils.ModelUtils.getTargetFieldType;

/**
 * Class used to generate a <code>KiePMMLDroolsAST</code> out of a <code>DataDictionary</code> and a <code>TreeModel</code>
 */
public class KiePMMLTreeModelASTFactory extends KiePMMLAbstractModelASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelASTFactory.class.getName());

    private KiePMMLTreeModelASTFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>KiePMMLDroolsAST</code> built out of the given parameters.
     * It also <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     *
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(final List<Field<?>> fields,
                                                       final TreeModel model,
                                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                       final List<KiePMMLDroolsType> types) {
        logger.trace("getKiePMMLDroolsAST {} {}", fields, model);
        DATA_TYPE targetType = getTargetFieldType(fields, model);
        List<OutputField> outputFields =  model.getOutput() != null ? model.getOutput().getOutputFields() : Collections.emptyList();
        List<KiePMMLDroolsRule> rules = KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, outputFields, model.getNoTrueChildStrategy(), targetType).declareRulesFromRootNode(model.getNode(), "");
        return new KiePMMLDroolsAST(types, rules);
    }
}
