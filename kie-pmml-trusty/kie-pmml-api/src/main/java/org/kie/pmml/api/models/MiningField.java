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

import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;

/**
 * User-friendly representation of a <b>MiningField</b>
 */
public class MiningField {

    private final String name;
    private final FIELD_USAGE_TYPE usageType;
    private final OP_TYPE opType;

    public MiningField(String name, FIELD_USAGE_TYPE usageType, OP_TYPE opType) {
        this.name = name;
        this.usageType = usageType;
        this.opType = opType;
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
}
