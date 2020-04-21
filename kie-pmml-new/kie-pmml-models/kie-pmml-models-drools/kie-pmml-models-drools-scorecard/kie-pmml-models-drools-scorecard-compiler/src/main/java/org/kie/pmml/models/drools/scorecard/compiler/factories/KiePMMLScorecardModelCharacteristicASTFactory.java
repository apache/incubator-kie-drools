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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.False;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.scorecard.Attribute;
import org.dmg.pmml.scorecard.Characteristic;
import org.dmg.pmml.scorecard.Characteristics;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.factories.KiePMMLPredicateASTFactory;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.PATH_PATTERN;
import static org.kie.pmml.models.drools.commons.utils.KiePMMLDroolsModelUtils.getCorrectlyFormattedResult;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code>s out of a <code>Characteristic</code>
 */
public class KiePMMLScorecardModelCharacteristicASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelCharacteristicASTFactory.class.getName());

    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    private final List<KiePMMLOutputField> outputFields;
    private final DATA_TYPE targetType;

    private KiePMMLScorecardModelCharacteristicASTFactory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final DATA_TYPE targetType) {
        this.fieldTypeMap = fieldTypeMap;
        this.outputFields = outputFields;
        this.targetType = targetType;
    }

    public static KiePMMLScorecardModelCharacteristicASTFactory factory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final DATA_TYPE targetType) {
        return new KiePMMLScorecardModelCharacteristicASTFactory(fieldTypeMap, outputFields, targetType);
    }

    public List<KiePMMLDroolsRule> declareRulesFromCharacteristics(final Characteristics characteristics, final String parentPath) {
        logger.trace("declareRulesFromCharacteristics {} {}", characteristics, parentPath);
        List<KiePMMLDroolsRule> toReturn = new ArrayList<>();
        for (Characteristic characteristic : characteristics) {
            declareRuleFromCharacteristic(characteristic, parentPath, toReturn);
        }
        return toReturn;
    }

    protected void declareRuleFromCharacteristic(final Characteristic characteristic, final String parentPath,
                                                 final List<KiePMMLDroolsRule> rules) {
        logger.trace("declareRuleFromCharacteristic {} {}", characteristic, parentPath);
        String currentRule = String.format(PATH_PATTERN, parentPath, characteristic.getName());
        final List<Attribute> attributes = characteristic.getAttributes();
        for (int i = 0; i < attributes.size(); i++) {
            declareRuleFromAttribute(attributes.get(i), currentRule, i, rules);
        }
    }

    protected void declareRuleFromAttribute(final Attribute attribute, final String parentPath, final int attributeIndex,
                                            final List<KiePMMLDroolsRule> rules) {
        logger.trace("declareRuleFromAttribute {} {}", attribute, parentPath);
        final Predicate predicate = attribute.getPredicate();
        // This means the rule should not be created at all.
        // Different semantics has to be implemented if the "False"/"True" predicates are declared inside
        // an XOR compound predicate
        if (predicate instanceof False) {
            return;
        }
        String currentRule = String.format(PATH_PATTERN, parentPath, attributeIndex);
        KiePMMLPredicateASTFactory.factory(fieldTypeMap, outputFields, rules).declareRuleFromPredicate(predicate, parentPath, currentRule, getCorrectlyFormattedResult(attribute.getPartialScore(), targetType), false);
    }
}
