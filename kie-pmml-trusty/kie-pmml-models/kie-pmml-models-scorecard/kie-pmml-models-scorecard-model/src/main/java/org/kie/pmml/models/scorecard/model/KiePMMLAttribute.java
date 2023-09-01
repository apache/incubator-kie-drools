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
package org.kie.pmml.models.scorecard.model;

import java.util.List;
import java.util.Map;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Scorecard.html#xsdElement_Attribute>Attribute</a>
 */
public class KiePMMLAttribute extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = 2996541076370981543L;
    private final KiePMMLPredicate predicate;
    private KiePMMLComplexPartialScore complexPartialScore;
    private String reasonCode = null;
    private Number partialScore = null;

    private KiePMMLAttribute(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate predicate) {
        super(name, extensions);
        this.predicate = predicate;
    }

    public Number evaluate(final List<KiePMMLDefineFunction> defineFunctions,
                           final List<KiePMMLDerivedField> derivedFields,
                           final List<KiePMMLOutputField> outputFields,
                           final Map<String, Object> inputData) {
        if (!predicate.evaluate(inputData)) {
            return null;
        }
        return complexPartialScore != null ? complexPartialScore.evaluate(defineFunctions, derivedFields, outputFields, inputData) : partialScore;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate predicate) {
        return new Builder(name, extensions, predicate);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLAttribute> {

        private Builder(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate predicate) {
            super("Attribute-", () -> new KiePMMLAttribute(name, extensions, predicate));
        }

        public Builder withComplexPartialScore(KiePMMLComplexPartialScore complexPartialScore) {
            if (complexPartialScore != null) {
                toBuild.complexPartialScore = complexPartialScore;
            }
            return this;
        }
        
        public Builder withReasonCode(String reasonCode) {
            if (reasonCode != null) {
                toBuild.reasonCode = reasonCode;
            }
            return this;
        }
        
        public Builder withPartialScore(Number partialScore) {
            if (partialScore != null) {
                toBuild.partialScore = partialScore;
            }
            return this;
        }
    }
}
