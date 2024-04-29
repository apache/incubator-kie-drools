/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.impl;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

public class RangeNodeSchemaMapper {

    static void populateSchemaFromListOfRanges(Schema toPopulate, List<RangeNode> ranges) {
        Range range = consolidateRanges(ranges);
        if (range != null) {
            if (range.getLowEndPoint() != null) {
                toPopulate.minimum((BigDecimal) range.getLowEndPoint());
                toPopulate.exclusiveMinimum(range.getLowBoundary() == Range.RangeBoundary.OPEN);
            }
            if (range.getHighEndPoint() != null) {
                toPopulate.maximum((BigDecimal) range.getHighEndPoint());
                toPopulate.exclusiveMaximum(range.getHighBoundary() == Range.RangeBoundary.OPEN);
            }
        }
    }

    static Range consolidateRanges(List<RangeNode> ranges) {
        boolean consistent = true;
        Range result = new RangeImpl();
        for (RangeNode r : ranges) {
            Comparable lowValue = null;
            if (r.getStart() instanceof NumberNode startNode) {
                lowValue = startNode.getValue();
            }
            if (lowValue != null) {
                if (result.getLowEndPoint() == null) {
                    result = new RangeImpl(Range.RangeBoundary.valueOf(r.getLowerBound().name()),
                                           lowValue,
                                           result.getHighEndPoint(), result.getHighBoundary());
                } else {
                    consistent = false;
                }
            }
            Comparable highValue = null;
            if (r.getEnd() instanceof NumberNode endNode) {
                highValue = endNode.getValue();
            }
            if (highValue != null) {
                if (result.getHighEndPoint() == null) {
                    result = new RangeImpl(result.getLowBoundary(), result.getLowEndPoint(), highValue,
                                           Range.RangeBoundary.valueOf(r.getUpperBound().name()));
                } else {
                    consistent = false;
                }
            }
        }
        return consistent ? result : null;
    }

    private RangeNodeSchemaMapper() {
        // deliberate intention not to allow instantiation of this class.
    }
}