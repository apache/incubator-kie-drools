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
package org.kie.pmml.models.drools.ast.factories;

import java.util.List;
import java.util.Map;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code>s out of a <code>Predicate</code>
 */
public class KiePMMLPredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredicateASTFactory.class.getName());

    private KiePMMLPredicateASTFactory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        super(fieldTypeMap, outputFields, rules);
    }

    public static KiePMMLPredicateASTFactory factory(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        return new KiePMMLPredicateASTFactory(fieldTypeMap, outputFields, rules);
    }

    /**
     * Manage the given <code>Predicate</code>. At this point of the execution, <b>predicate</b> could be:
     *
     * <p>1) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_True">True</a><p>
     * <p>2) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate">SimplePredicate</a><p>
     * <p>3) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_CompoundPredicate">CompoundPredicate</a><p>
     * <p>4) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimpleSetPredicate">SimpleSetPredicate</a><p>
     * @param predicate
     * @param parentPath
     * @param currentRule
     * @param result
     * @param isFinalLeaf
     */
    public void declareRuleFromPredicate(final Predicate predicate,
                                         final String parentPath,
                                         final String currentRule,
                                         final Object result,
                                         final boolean isFinalLeaf) {
        logger.trace("declareRuleFromPredicate {} {} {} {}", predicate, parentPath, currentRule, result);
        if (predicate instanceof True) {
            KiePMMLTruePredicateASTFactory.factory((True) predicate, outputFields, rules).declareRuleFromTruePredicate(parentPath, currentRule, result, isFinalLeaf);
        } else if (predicate instanceof SimplePredicate) {
            KiePMMLSimplePredicateASTFactory.factory((SimplePredicate) predicate, fieldTypeMap, outputFields, rules).declareRuleFromSimplePredicate(parentPath, currentRule, result, isFinalLeaf);
        } else if (predicate instanceof SimpleSetPredicate) {
            KiePMMLSimpleSetPredicateASTFactory.factory((SimpleSetPredicate) predicate, fieldTypeMap, outputFields, rules).declareRuleFromSimpleSetPredicate(parentPath, currentRule, result, isFinalLeaf);
        } else if (predicate instanceof CompoundPredicate) {
            KiePMMLCompoundPredicateASTFactory.factory((CompoundPredicate) predicate, fieldTypeMap, outputFields, rules).declareRuleFromCompoundPredicate(parentPath, currentRule, result, isFinalLeaf);
        }
    }
}
