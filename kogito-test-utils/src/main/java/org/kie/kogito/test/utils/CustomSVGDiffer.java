/*
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
package org.kie.kogito.test.utils;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluators;

public class CustomSVGDiffer {

    private DiffBuilder diffBuilder;

    public CustomSVGDiffer(String content) {
        // Configure the DiffBuilder to ignore whitespace, element order and the svf tag
        diffBuilder = DiffBuilder.compare(Input.fromString(content))
                .ignoreWhitespace()
                .ignoreElementContentWhitespace()
                .checkForSimilar()
                .withDifferenceEvaluator(DifferenceEvaluators.chain(
                        DifferenceEvaluators.Default,
                        (comparison, outcome) -> {
                            //this tag may differ from svg processors like batik
                            if (comparison.getControlDetails().getTarget().getNodeName().equals("svg")) {
                                return ComparisonResult.SIMILAR;
                            }
                            if (comparison.getType() == ComparisonType.NAMESPACE_URI) {
                                return ComparisonResult.SIMILAR;
                            }
                            return outcome;
                        }));
    }

    public Diff withTest(Object item) {
        return diffBuilder.withTest(item).build();
    }
}
