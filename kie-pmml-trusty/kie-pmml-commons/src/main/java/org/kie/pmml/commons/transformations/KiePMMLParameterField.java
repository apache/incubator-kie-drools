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
package org.kie.pmml.commons.transformations;

import java.io.Serializable;
import java.util.List;

import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLParameterField extends AbstractKiePMMLComponent implements Serializable {

    private static final long serialVersionUID = 9187889880911645935L;

    private DATA_TYPE dataType;
    private OP_TYPE opType;
    private String displayName;

    private KiePMMLParameterField(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public DATA_TYPE getDataType() {
        return dataType;
    }

    public OP_TYPE getOpType() {
        return opType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLParameterField> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("ParameterField-", () -> new KiePMMLParameterField(name, extensions));
        }

        public Builder withDataType(DATA_TYPE dataType) {
            if (dataType != null) {
                toBuild.dataType = dataType;
            }
            return this;
        }

        public Builder withOpType(OP_TYPE opType) {
            if (opType != null) {
                toBuild.opType = opType;
            }
            return this;
        }

        public Builder withDisplayName(String displayName) {
            if (displayName != null) {
                toBuild.displayName = displayName;
            }
            return this;
        }
    }
}
