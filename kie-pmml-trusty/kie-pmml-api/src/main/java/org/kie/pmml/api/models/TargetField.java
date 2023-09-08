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
package org.kie.pmml.api.models;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.OP_TYPE;

/**
 * User-friendly representation of a <b>TargetField</b>
 */
public class TargetField implements Serializable {

    private static final long serialVersionUID = 7720028373203700981L;

    private final String name;
    private final List<TargetValue> targetValues;
    private final OP_TYPE opType;
    private final String field;
    private final CAST_INTEGER castInteger;
    private final Double min;
    private final Double max;
    private double rescaleConstant = 0;
    private double rescaleFactor = 1;

    public TargetField(List<TargetValue> targetValues,
                       OP_TYPE opType,
                       String field,
                       CAST_INTEGER castInteger,
                       Number min,
                       Number max,
                       Number rescaleConstant,
                       Number rescaleFactor) {
        this.name = UUID.randomUUID().toString();
        this.targetValues = targetValues;
        this.opType = opType;
        this.field = field;
        this.castInteger = castInteger;
        this.min = min != null ? min.doubleValue() : null;
        this.max = max != null ? max.doubleValue() : null;
        if (rescaleConstant != null) {
            this.rescaleConstant = rescaleConstant.doubleValue();
        }
        if (rescaleFactor != null) {
            this.rescaleFactor = rescaleFactor.doubleValue();
        }
    }

    public String getName() {
        return name;
    }

    public List<TargetValue> getTargetValues() {
        return targetValues != null ? Collections.unmodifiableList(targetValues) : Collections.emptyList();
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public String getField() {
        return field;
    }

    public CAST_INTEGER getCastInteger() {
        return castInteger;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public double getRescaleConstant() {
        return rescaleConstant;
    }

    public double getRescaleFactor() {
        return rescaleFactor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TargetField that = (TargetField) o;
        return Double.compare(that.rescaleConstant, rescaleConstant) == 0 && Double.compare(that.rescaleFactor,
                                                                                            rescaleFactor) == 0 && Objects.equals(name, that.name) && Objects.equals(targetValues, that.targetValues) && opType == that.opType && Objects.equals(field, that.field) && castInteger == that.castInteger && Objects.equals(min, that.min) && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetValues, opType, field, castInteger, min, max, rescaleConstant, rescaleFactor);
    }

    @Override
    public String toString() {
        return "TargetField{" +
                "name='" + name + '\'' +
                ", targetValues=" + targetValues +
                ", opType=" + opType +
                ", field='" + field + '\'' +
                ", castInteger=" + castInteger +
                ", min=" + min +
                ", max=" + max +
                ", rescaleConstant=" + rescaleConstant +
                ", rescaleFactor=" + rescaleFactor +
                '}';
    }
}
