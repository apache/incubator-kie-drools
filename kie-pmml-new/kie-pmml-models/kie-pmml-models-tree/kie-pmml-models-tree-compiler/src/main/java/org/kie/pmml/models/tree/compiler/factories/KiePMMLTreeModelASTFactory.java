/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.tree.compiler.factories;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledAST;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledType;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldType;

/**
 * Class used to generate a <code>KiePMMLDrooledAST</code> out of a <code>DataDictionary</code> and a <code>TreeModel</code>
 */
public class KiePMMLTreeModelASTFactory {

    public static final String SURROGATE_RULENAME_PATTERN = "%s_surrogate_%s";
    public static final String SURROGATE_GROUP_PATTERN = "%s_surrogate";
    public static final String STATUS_NULL = "status == null";
    public static final String STATUS_PATTERN = "status == \"%s\"";
    public static final String PATH_PATTERN = "%s_%s";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelASTFactory.class.getName());

    private KiePMMLTreeModelASTFactory() {
        // Avoid instantiation
    }

    /**
     * Returns the <code>KiePMMLDrooledAST</code> built out of the given parameters.
     * It also <b>populate</b> the <b>fieldNameTypeNameMap</b> with mapping between original field' name and <b>original type/generated type</b> tupla
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @return
     */
    public static KiePMMLDrooledAST getKiePMMLDrooledAST(DataDictionary dataDictionary, TreeModel model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.debug("getKiePMMLDrooledAST {} {}", dataDictionary, model);
        DATA_TYPE targetType = getTargetFieldType(dataDictionary, model);
        Queue<KiePMMLDrooledType> types = KiePMMLTreeModelDataDictionaryASTFactory.factory(fieldTypeMap).declareTypes(dataDictionary);
        final List<KiePMMLOutputField> outputFields = getOutputFields(model);
        Queue<KiePMMLDrooledRule> rules = KiePMMLTreeModelNodeASTFactory.factory(fieldTypeMap, outputFields, model.getNoTrueChildStrategy(), targetType).declareRulesFromRootNode(model.getNode(), "");
        return new KiePMMLDrooledAST(types, rules);
    }
}
