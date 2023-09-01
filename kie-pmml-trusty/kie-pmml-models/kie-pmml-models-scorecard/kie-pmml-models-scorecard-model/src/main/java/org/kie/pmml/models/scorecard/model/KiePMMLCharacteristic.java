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
import org.kie.pmml.commons.transformations.KiePMMLDefineFunction;
import org.kie.pmml.commons.transformations.KiePMMLDerivedField;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Scorecard.html#xsdElement_Characteristic>Characteristic</a>
 */
public class KiePMMLCharacteristic extends AbstractKiePMMLComponent {

    private static final long serialVersionUID = 3302920103976105622L;
    private final List<KiePMMLAttribute> attributes;
    private String reasonCode = null;
    private Number baselineScore = null;

    private KiePMMLCharacteristic(String name, List<KiePMMLExtension> extensions, List<KiePMMLAttribute> attributes) {
        super(name, extensions);
        this.attributes = attributes;
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, List<KiePMMLAttribute> attributes) {
        return new Builder(name, extensions, attributes);
    }

    public Number getBaselineScore() {
        return baselineScore;
    }

    public ReasonCodeValue evaluate(final List<KiePMMLDefineFunction> defineFunctions,
                                    final List<KiePMMLDerivedField> derivedFields,
                                    final List<KiePMMLOutputField> outputFields,
                                    final Map<String, Object> inputData) {
        for (KiePMMLAttribute attribute : attributes) {
            Number attributeScore = attribute.evaluate(defineFunctions, derivedFields, outputFields, inputData);
            if (attributeScore != null) {
                String totalReasonCode = attribute.getReasonCode() != null ? attribute.getReasonCode() : reasonCode;
                return new ReasonCodeValue(totalReasonCode, attributeScore);
            }
        }
        return null;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public static class ReasonCodeValue {

        private final String reasonCode;
        private final Number score;

        public ReasonCodeValue(String reasonCode, Number score) {
            this.reasonCode = reasonCode;
            this.score = score;
        }

        public String getReasonCode() {
            return reasonCode;
        }

        public Number getScore() {
            return score;
        }
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLCharacteristic> {

        private Builder(String name, List<KiePMMLExtension> extensions, List<KiePMMLAttribute> attributes) {
            super("Characteristic-", () -> new KiePMMLCharacteristic(name, extensions, attributes));
        }

        public Builder withReasonCode(String reasonCode) {
            if (reasonCode != null) {
                toBuild.reasonCode = reasonCode;
            }
            return this;
        }

        public Builder withBaselineScore(Number baselineScore) {
            if (baselineScore != null) {
                toBuild.baselineScore = baselineScore;
            }
            return this;
        }
    }
}
