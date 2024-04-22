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
package org.kie.dmn.openapi.impl;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.kie.dmn.feel.runtime.UnaryTestImpl;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FEELSchemaEnum {

    private static final Logger LOG = LoggerFactory.getLogger(FEELSchemaEnum.class);

    public static void parseValuesIntoSchema(Schema schema, List<DMNUnaryTest> unaryTests) {
        List<Object> expectLiterals = evaluateUnaryTests(unaryTests);
        try {
            checkEvaluatedUnaryTestsForTypeConsistency(expectLiterals);
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable to parse generic value into the JSON Schema for enumeration");
            return;
        }
        if (expectLiterals.contains(null)) {
            schema.setNullable(true);
        }
        boolean allLiterals = !expectLiterals.isEmpty() && expectLiterals.stream().allMatch(o -> o == null || o instanceof String || o instanceof Number || o instanceof Boolean);
        if (allLiterals) {
            schema.enumeration(expectLiterals);
        } else {
            LOG.warn("Unable to parse generic value into the JSON Schema for enumeration");
        }
    }

    public static void parseRangeableValuesIntoSchema(Schema schema, List<DMNUnaryTest> list, Class<?> expectedType) {
        List<Object> uts = evaluateUnaryTests(list); // we leverage the property of the *base* FEEL grammar(non visited by ASTVisitor, only the ParseTree->AST Visitor) that `>x` is a Range
        boolean allowNull = uts.remove(null);
        if (allowNull) {
            schema.setNullable(true);
        }
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
        } else if (uts.stream().allMatch(expectedType::isInstance)) {
            if (allowNull) {
                uts.add(null);
            }
            schema.enumeration(uts);
        } else {
            LOG.warn("Unable to parse {} value into the JSON Schema for enumeration", expectedType);
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

    static List<Object> evaluateUnaryTests(List<DMNUnaryTest> list) {
        FEEL feelInstance = FEEL.newInstance();
        List<Object> toReturn = list.stream()
                .map(UnaryTestImpl.class::cast)
                .map(UnaryTestImpl::toString)
                .filter(str -> !str.contains("?")) // We have to exclude "TEST" expressions, because they can't be evaluated without a context and their return is meaningless
                .map(feelInstance::evaluate)
                .collect(Collectors.toList());
        checkEvaluatedUnaryTestsForNull(toReturn);
        return toReturn;
    }

    /**
     * Method used to verify if the given <code>List</code> contains at most one <code>null</code>,
     * since those should be put in the "enum" attribute
     *
     * @param toCheck
     */
    static void checkEvaluatedUnaryTestsForNull(List<Object> toCheck) {
        if (toCheck.stream().filter(Objects::isNull).toList().size() > 1) {
            throw new IllegalArgumentException("More then one object is null, only one allowed at maximum");
        }
    }

    /**
     * Method used to verify if the given <code>List</code> contains the same type of <code>Object</code>s,
     * since those should be put in the "enum" attribute
     *
     * @param toCheck
     */
    static void checkEvaluatedUnaryTestsForTypeConsistency(List<Object> toCheck) {
        if (toCheck.stream().filter(Objects::nonNull)
                .map(Object::getClass)
                .collect(Collectors.toUnmodifiableSet())
                .size() > 1) {
            throw new IllegalArgumentException("Different types of objects, only one allowed");
        }
    }

    private FEELSchemaEnum() {
        // deliberate intention not to allow instantiation of this class.
    }
}
