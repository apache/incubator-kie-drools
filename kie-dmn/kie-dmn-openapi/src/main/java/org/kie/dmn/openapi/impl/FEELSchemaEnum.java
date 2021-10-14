/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.openapi.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.UnaryTestImpl;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FEELSchemaEnum {

    private static final Logger LOG = LoggerFactory.getLogger(FEELSchemaEnum.class);

    public static void parseAllowedValuesIntoSchema(Schema schema, List<DMNUnaryTest> list) {
        List<Object> expectLiterals = evaluateUnaryTests(list);
        boolean allLiterals = expectLiterals.stream().allMatch(o -> o instanceof String || o instanceof Number || o instanceof Boolean);
        if (allLiterals) {
            schema.enumeration(expectLiterals);
        } else {
            LOG.warn("Unable to parse generic allowed value into the JSON Schema for enumeration");
        }
    }
    
    public static void parseNumberAllowedValuesIntoSchema(Schema schema, List<DMNUnaryTest> list) {
        List<Object> uts = evaluateUnaryTests(list); // we leverage the property of the *base* FEEL grammar(non visited by ASTVisitor, only the ParseTree->AST Visitor) that `>x` is a Range
        if (uts.size() <= 2 && uts.stream().allMatch(o -> o instanceof Range)) {
            Range range = consolidateRanges((List) uts); // cast intentional.
            if (range != null) {
                if (range.getLowEndPoint() != null) {
                    schema.minimum((BigDecimal) range.getLowEndPoint());
                    schema.exclusiveMinimum(range.getLowBoundary() == RangeBoundary.OPEN);
                }
                if (range.getHighEndPoint() != null) {
                    schema.maximum((BigDecimal) range.getHighEndPoint());
                    schema.exclusiveMaximum(range.getHighBoundary() == RangeBoundary.OPEN);
                }
            }
        } else if (uts.stream().allMatch(o -> o instanceof Number)) {
            schema.enumeration(uts);
        } else {
            LOG.warn("Unable to parse generic allowed value into the JSON Schema for enumeration");
        }
    }

    public static Range consolidateRanges(List<Range> ranges) {
        boolean consistent = true;
        Range result = new RangeImpl();
        for (Range r : ranges) {
            if (r.getLowEndPoint() != null) {
                if (result.getLowEndPoint() == null) {
                    result = new RangeImpl(r.getLowBoundary(), r.getLowEndPoint(), result.getHighEndPoint(), result.getHighBoundary());
                } else {
                    consistent = false;
                }
            }
            if (r.getHighEndPoint() != null) {
                if (result.getHighEndPoint() == null) {
                    result = new RangeImpl(result.getLowBoundary(), result.getLowEndPoint(), r.getHighEndPoint(), r.getHighBoundary());
                } else {
                    consistent = false;
                }
            }
        }
        return consistent ? result : null;
    }

    private static List<Object> evaluateUnaryTests(List<DMNUnaryTest> list) {
    	FEEL SimpleFEEL = FEEL.newInstance();
        List<Object> utEvaluated = list.stream().map(UnaryTestImpl.class::cast)
                                       .map(UnaryTestImpl::toString)
                                       .map(SimpleFEEL::evaluate)
                                       .collect(Collectors.toList());
        return utEvaluated;
    }

    private FEELSchemaEnum() {
        // deliberate intention not to allow instantiation of this class.
    }
}
