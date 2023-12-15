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
import java.util.Collections;
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLLocalTransformations extends AbstractKiePMMLComponent implements Serializable {

    private static final long serialVersionUID = 9187889880911645935L;

    private List<KiePMMLDerivedField> derivedFields;

    private KiePMMLLocalTransformations(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    public List<KiePMMLDerivedField> getDerivedFields() {
        return derivedFields != null ? Collections.unmodifiableList(derivedFields) : Collections.emptyList();
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLLocalTransformations> {

        private Builder(String name, List<KiePMMLExtension> extensions) {
            super("LocalTransformations-", () -> new KiePMMLLocalTransformations(name, extensions));
        }

        public Builder withDerivedFields(List<KiePMMLDerivedField> derivedFields) {
            if (derivedFields != null) {
                toBuild.derivedFields = derivedFields;
            }
            return this;
        }
    }


}
