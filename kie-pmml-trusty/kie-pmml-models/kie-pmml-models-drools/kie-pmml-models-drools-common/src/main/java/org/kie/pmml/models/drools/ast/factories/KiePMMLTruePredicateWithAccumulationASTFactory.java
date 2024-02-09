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

import java.util.List;

import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate a <code>KiePMMLDroolsRule</code> out of a <code>True</code> predicate to be used with <b>accumulation</b>
 */
public class KiePMMLTruePredicateWithAccumulationASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicateWithAccumulationASTFactory.class.getName());

    public static void declareRuleFromTruePredicate(KiePMMLDroolsRule.Builder builder,
                                                    final List<KiePMMLDroolsRule> rules,
                                                    final String statusToSet,
                                                    final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                    boolean isLastCharacteristic) {
        logger.trace("declareRuleFromTruePredicate {} {} {}", builder, statusToSet, isLastCharacteristic);
        if (isLastCharacteristic) {
            builder = builder.withAccumulationResult(true)
                    .withResultCode(ResultCode.OK);

        }
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        rules.add(builder.build());
    }
}
