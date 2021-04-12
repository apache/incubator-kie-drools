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
package org.kie.pmml.api.models;

import java.io.Serializable;
import java.util.List;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;

/**
 * User-friendly representation of a <b>MiningField</b>
 */
public class MiningField implements Serializable {

    private static final long serialVersionUID = -7718478772317847187L;
    private final String name;
    private final FIELD_USAGE_TYPE usageType;
    private final OP_TYPE opType;
    private final DATA_TYPE dataType;
    private final String missingValueReplacement;
    private final List<String> allowedValues;
    private final List<Interval> intervals;

    public MiningField(final String name,
                       final FIELD_USAGE_TYPE usageType,
                       final OP_TYPE opType,
                       final DATA_TYPE dataType,
                       final String missingValueReplacement,
                       final List<String> allowedValues,
                       final List<Interval> intervals) {
        this.name = name;
        this.usageType = usageType;
        this.opType = opType;
        this.dataType = dataType;
        this.missingValueReplacement = missingValueReplacement;
        this.allowedValues = allowedValues;
        this.intervals = intervals;
    }

    public String getName() {
        return name;
    }

    public FIELD_USAGE_TYPE getUsageType() {
        return usageType;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public String getMissingValueReplacement() {
        return missingValueReplacement;
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public List<Interval> getIntervals() {
        return intervals;
    }
}
