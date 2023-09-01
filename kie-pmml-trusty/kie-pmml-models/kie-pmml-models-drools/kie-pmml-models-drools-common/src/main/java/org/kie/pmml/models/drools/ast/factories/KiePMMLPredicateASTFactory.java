/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.models.drools.ast.factories;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code>s out of a <code>Predicate</code>
 */
public class KiePMMLPredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredicateASTFactory.class.getName());

    private KiePMMLPredicateASTFactory(final PredicateASTFactoryData predicateASTFactoryData) {
        super(predicateASTFactoryData);
    }

    public static KiePMMLPredicateASTFactory factory(final PredicateASTFactoryData predicateASTFactoryData) {
        return new KiePMMLPredicateASTFactory(predicateASTFactoryData);
    }

    /**
     * Manage the given <code>Predicate</code>.
     * <p>
     * It creates rules that, in the <b>rhs</b>, set an <b>accumulation</b> value
     * <p>
     * At this point of the execution, <b>predicate</b> could be:
     *
     * <p>1) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_True">True</a><p>
     * <p>2) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate">SimplePredicate</a><p>
     * <p>3) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_CompoundPredicate">CompoundPredicate</a><p>
     * <p>4) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimpleSetPredicate">SimpleSetPredicate</a><p>
     * @param toAccumulate
     * @param statusToSet
     * @param reasonCodeAndValue
     * @param isLastCharacteristic
     */
    public void declareRuleFromPredicate(final Number toAccumulate,
                                         final String statusToSet,
                                         final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                         final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromPredicate {} {} {} {} {}", predicateASTFactoryData.getPredicate(), predicateASTFactoryData.getParentPath(), predicateASTFactoryData.getCurrentRule(), toAccumulate, isLastCharacteristic);
        if (predicateASTFactoryData.getPredicate() instanceof True) {
            KiePMMLTruePredicateASTFactory.factory(predicateASTFactoryData)
                    .declareRuleFromTruePredicateWithAccumulation(toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
        } else if (predicateASTFactoryData.getPredicate() instanceof SimplePredicate) {
            KiePMMLSimplePredicateASTFactory.factory(predicateASTFactoryData)
                    .declareRuleFromSimplePredicate(toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
        } else if (predicateASTFactoryData.getPredicate() instanceof SimpleSetPredicate) {
            KiePMMLSimpleSetPredicateASTFactory.factory(predicateASTFactoryData)
                    .declareRuleFromSimpleSetPredicate(toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
        } else if (predicateASTFactoryData.getPredicate() instanceof CompoundPredicate) {
            KiePMMLCompoundPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromCompoundPredicate(toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
        }
    }

    /**
     * Manage the given <code>Predicate</code>.
     * <p>
     * It creates rules that, in the <b>rhs</b>, eventually set a <b>final</b> result
     * <p>
     * At this point of the execution, <b>predicate</b> could be:
     *
     * <p>1) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_True">True</a><p>
     * <p>2) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimplePredicate">SimplePredicate</a><p>
     * <p>3) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_CompoundPredicate">CompoundPredicate</a><p>
     * <p>4) @see <a href="http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_SimpleSetPredicate">SimpleSetPredicate</a><p>
     * @param result
     * @param isFinalLeaf
     */
    public void declareRuleFromPredicate(final Object result,
                                         final boolean isFinalLeaf) {
        logger.trace("declareRuleFromPredicate {} {} {} {}", predicateASTFactoryData.getPredicate(), predicateASTFactoryData.getParentPath(), predicateASTFactoryData.getCurrentRule(), result);
        if (predicateASTFactoryData.getPredicate() instanceof True) {
            KiePMMLTruePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromTruePredicateWithResult(result, isFinalLeaf);
        } else if (predicateASTFactoryData.getPredicate() instanceof SimplePredicate) {
            KiePMMLSimplePredicateASTFactory.factory(predicateASTFactoryData)
                    .declareRuleFromSimplePredicate(result, isFinalLeaf);
        } else if (predicateASTFactoryData.getPredicate() instanceof SimpleSetPredicate) {
            KiePMMLSimpleSetPredicateASTFactory.factory(predicateASTFactoryData)
                    .declareRuleFromSimpleSetPredicate(result, isFinalLeaf);
        } else if (predicateASTFactoryData.getPredicate() instanceof CompoundPredicate) {
            KiePMMLCompoundPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromCompoundPredicate(result, isFinalLeaf);
        }
    }
}
