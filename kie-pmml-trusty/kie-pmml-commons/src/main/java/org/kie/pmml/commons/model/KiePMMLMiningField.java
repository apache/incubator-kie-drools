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
package org.kie.pmml.commons.model;

import java.util.Collections;
import java.util.List;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.expressions.KiePMMLInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLMiningField extends AbstractKiePMMLComponent {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLMiningField.class);
    private static final long serialVersionUID = 2408750585433339543L;
    private FIELD_USAGE_TYPE fieldUsageType = FIELD_USAGE_TYPE.ACTIVE;
    private OP_TYPE opType = null;
    private DATA_TYPE dataType;
    private MISSING_VALUE_TREATMENT_METHOD missingValueTreatmentMethod;
    private INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod;
    private List<String> allowedValues;
    private List<KiePMMLInterval> intervals;
    private String missingValueReplacement;
    private String invalidValueReplacement;

    private KiePMMLMiningField(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public boolean isMatching(Object toEvaluate) {
        if ((allowedValues == null || allowedValues.isEmpty())
                && (intervals == null || intervals.isEmpty())) {
            return true;
        }
        if (toEvaluate == null) {
            return false;
        }
        if (allowedValues != null && !allowedValues.isEmpty()) {
            return allowedValues.contains(toEvaluate.toString());
        } else if (toEvaluate instanceof Number) {
            return intervals.stream().anyMatch(interval -> interval.isIn((Number) toEvaluate));
        } else {
            return false;
        }
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public FIELD_USAGE_TYPE getFieldUsageType() {
        return fieldUsageType;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public MISSING_VALUE_TREATMENT_METHOD getMissingValueTreatmentMethod() {
        return missingValueTreatmentMethod;
    }

    public INVALID_VALUE_TREATMENT_METHOD getInvalidValueTreatmentMethod() {
        return invalidValueTreatmentMethod;
    }

    public String getMissingValueReplacement() {
        return missingValueReplacement;
    }

    public String getInvalidValueReplacement() {
        return invalidValueReplacement;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public List<KiePMMLInterval> getIntervals() {
        return Collections.unmodifiableList(intervals);
    }

    public boolean isTarget() {
        return fieldUsageType == FIELD_USAGE_TYPE.TARGET || fieldUsageType == FIELD_USAGE_TYPE.PREDICTED;
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLMiningField> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("MiningField-", () -> new KiePMMLMiningField(name, extensions));
        }

        public Builder withFieldUsageType(FIELD_USAGE_TYPE fieldUsageType) {
            if (fieldUsageType != null) {
                toBuild.fieldUsageType = fieldUsageType;
            }
            return this;
        }

        public Builder withOpType(OP_TYPE opType) {
            if (opType != null) {
                toBuild.opType = opType;
            }
            return this;
        }

        public Builder withDataType(DATA_TYPE dataType) {
            if (dataType != null) {
                toBuild.dataType = dataType;
            }
            return this;
        }

        public Builder withMissingValueTreatmentMethod(MISSING_VALUE_TREATMENT_METHOD missingValueTreatmentMethod) {
            if (missingValueTreatmentMethod != null) {
                toBuild.missingValueTreatmentMethod = missingValueTreatmentMethod;
            }
            return this;
        }

        public Builder withInvalidValueTreatmentMethod(INVALID_VALUE_TREATMENT_METHOD invalidValueTreatmentMethod) {
            if (invalidValueTreatmentMethod != null) {
                toBuild.invalidValueTreatmentMethod = invalidValueTreatmentMethod;
            }
            return this;
        }

        public Builder withMissingValueReplacement(String missingValueReplacement) {
            if (missingValueReplacement != null) {
                toBuild.missingValueReplacement = missingValueReplacement;
            }
            return this;
        }

        public Builder withInvalidValueReplacement(String invalidValueReplacement) {
            if (invalidValueReplacement != null) {
                toBuild.invalidValueReplacement = invalidValueReplacement;
            }
            return this;
        }

        public Builder withAllowedValues(List<String> allowedValues) {
            if (allowedValues != null) {
                toBuild.allowedValues = allowedValues;
            }
            return this;
        }

        public Builder withIntervals(List<KiePMMLInterval> intervals) {
            if (intervals != null) {
                toBuild.intervals = intervals;
            }
            return this;
        }
    }
}
