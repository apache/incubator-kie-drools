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
package org.kie.pmml.models.drooled.ast;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;

public class KiePMMLDrooledRule {

    // Rule name
    private final String name;
    // RHS
    private final String statusToSet;
    // LHS
    private String statusConstraint;
    // Constraints put in and
    private Map<String, List<KiePMMLOperatorValue>> andConstraints;
    // Constraints put in or
    private Map<String, List<KiePMMLOperatorValue>> orConstraints;
    // Constraints put in xor
    private Map<String, List<KiePMMLOperatorValue>> xorConstraints;
    // Constraints put in "in"
    private Map<String, List<Object>> inConstraints;
    // Constraints put in "notIn""
    private Map<String, List<Object>> notInConstraints;
    // Used to manage compound predicates
    private String ifBreakField;
    private String ifBreakOperator;
    private Object ifBreakValue;
    private StatusCode resultCode;
    private Object result;

    private KiePMMLDrooledRule(String name, String statusToSet) {
        this.name = name;
        this.statusToSet = statusToSet;
    }

    /**
     * @param name The rule name
     * @param statusToSet The status to set in the rhs' <b>default</b> <code>then</code>; e.g.:
     * <p><code>then</code></p>
     * <p><code>$statusHolder.setStatus(<i>statusToSet</i>);</code></p>
     * </p><code>update($statusHolder);</code></p>
     * <p><b>If</b> there is a <b>break</b> statement in the lhs, then the <i>statusToSet</i>
     * will be applied differently if the node is a <i>final/leaf</i> or not.</p>
     * <p>If the node is a <i>final/leaf</i>, the <i>statusToSet</i> will be applied in the
     * default <code>then</code> directive,
     * while in the default <code>then[match]</code> it will be set as <b>"DONE"</b></p>; e.g.:
     * <p><code>then</code></p>
     * <p><code>$statusHolder.setStatus(<i>statusToSet</i>);</code></p>
     * <p><code>update($statusHolder);</code></p>
     *
     * <p><code>then[match]</code></p>
     * <p><code>$statusHolder.setStatus("DONE");</code></p>
     * <p><code>update($statusHolder);</code></p>
     *
     * <p>If the node is a <b>not</b> final/leaf</b>, the <i>statusToSet</i> will be set in the
     * <code>then[match]</code> directive,
     * while in the default <code>then</code> it will be set as <b>"DONE"</b></p>; e.g.:
     * <p><code>then</code></p>
     * <p><code>$statusHolder.setStatus("DONE");</code></p>
     * <p><code>update($statusHolder);</code></p>
     *
     * <p><code>then[match]</code></p>
     * <p><code>$statusHolder.setStatus(<i>statusToSet</i>);</code></p>
     * <p><code>update($statusHolder);</code></p>
     * @return
     */
    public static Builder builder(String name, String statusToSet) {
        return new Builder(name, statusToSet);
    }

    public String getName() {
        return name;
    }

    /**
     * The status to set in the rhs
     * (<code>$statusHolder.setStatus("DONE");</code>
     * <code>update($statusHolder);</code>)
     * @return
     */
    public String getStatusToSet() {
        return statusToSet;
    }

    public String getStatusConstraint() {
        return statusConstraint;
    }

    public Map<String, List<KiePMMLOperatorValue>> getAndConstraints() {
        return andConstraints != null ? Collections.unmodifiableMap(andConstraints) : null;
    }

    public Map<String, List<KiePMMLOperatorValue>> getOrConstraints() {
        return orConstraints != null ? Collections.unmodifiableMap(orConstraints) : null;
    }

    public Map<String, List<KiePMMLOperatorValue>> getXorConstraints() {
        return xorConstraints != null ? Collections.unmodifiableMap(xorConstraints) : null;
    }

    public Map<String, List<Object>> getInConstraints() {
        return inConstraints != null ? Collections.unmodifiableMap(inConstraints) : null;
    }

    public Map<String, List<Object>> getNotInConstraints() {
        return notInConstraints != null ? Collections.unmodifiableMap(notInConstraints) : null;
    }

    public String getIfBreakField() {
        return ifBreakField;
    }

    public String getIfBreakOperator() {
        return ifBreakOperator;
    }

    public Object getIfBreakValue() {
        return ifBreakValue;
    }

    public StatusCode getResultCode() {
        return resultCode;
    }

    public Object getResult() {
        return result;
    }

    public static class Builder {

        protected KiePMMLDrooledRule toBuild;

        public Builder(String name, String statusToSet) {
            this.toBuild = new KiePMMLDrooledRule(name, statusToSet);
        }

        public Builder withStatusConstraint(String constraint) {
            this.toBuild.statusConstraint = constraint;
            return this;
        }

        /**
         * @param constraints The <b>key</b> of the map is the name of the generated type, while the <b>value</b> is the <code>List&lt;KiePMMLOperatorValue&gt;</code>
         * to use for evaluation. Implicitly, the latter is evaluated with the <b>value</b> field of the former
         * (e.g entry <b>"OUTLOOK"/List(KiePMMLOperatorValue("==", "sunny"))</b> generates OUTLOOK(value == "sunny")
         * (e.g entry <b>"TEMPERATURE"/List(KiePMMLOperatorValue("<", 90), KiePMMLOperatorValue(">", 50))</b> generates TEMPERATURE( value < 90 && value > 50 )
         * @return
         */
        public Builder withAndConstraints(Map<String, List<KiePMMLOperatorValue>> constraints) {
            this.toBuild.andConstraints = constraints;
            return this;
        }

        /**
         * @param constraints The <b>key</b> of the map is the name of the generated type, while the <b>value</b> is the <code>List&lt;KiePMMLOperatorValue&gt;</code>
         * to use for evaluation. Implicitly, the latter is evaluated with the <b>value</b> field of the former
         * (e.g entry <b>"OUTLOOK"/List(KiePMMLOperatorValue("==", "sunny"))</b> generates OUTLOOK(value == "sunny")
         * (e.g entry <b>"TEMPERATURE"/List(KiePMMLOperatorValue("<", 90), KiePMMLOperatorValue(">", 50))</b> generates TEMPERATURE( value < 90 && value > 50 )
         * @return
         */
        public Builder withOrConstraints(Map<String, List<KiePMMLOperatorValue>> constraints) {
            this.toBuild.orConstraints = constraints;
            return this;
        }

        /**
         * @param constraints The <b>key</b> of the map is the name of the generated type, while the <b>value</b> is the <code>List&lt;KiePMMLOperatorValue&gt;</code>
         * to use for evaluation. Implicitly, the latter is evaluated with the <b>value</b> field of the former
         * (e.g entry <b>"OUTLOOK"/List(KiePMMLOperatorValue("==", "sunny"))</b> generates OUTLOOK(value == "sunny")
         * (e.g entry <b>"TEMPERATURE"/List(KiePMMLOperatorValue("<", 90), KiePMMLOperatorValue(">", 50))</b> generates TEMPERATURE( value < 90 && value > 50 )
         * @return
         */
        public Builder withXorConstraints(Map<String, List<KiePMMLOperatorValue>> constraints) {
            this.toBuild.xorConstraints = constraints;
            return this;
        }

        /**
         * @param constraints The <b>key</b> of the map is the name of the generated type, while the <b>value</b> is the <code>List&lt;Object&gt;</code>
         * to use for evaluation. Implicitly, the latter is evaluated with the <b>value</b> field of the former
         * (e.g entry <b>"INPUT1"/List(-5,  0.5, 1, 10)</b> generates INPUT1(value in (-5,  0.5, 1, 10))
         * @return
         */
        public Builder withInConstraints(Map<String, List<Object>> constraints) {
            this.toBuild.inConstraints = constraints;
            return this;
        }

        /**
         * @param constraints The <b>key</b> of the map is the name of the generated type, while the <b>value</b> is the <code>List&lt;Object&gt;</code>
         * to use for evaluation. Implicitly, the latter is evaluated with the <b>value</b> field of the former
         * (e.g entry <b>"INPUT2"/List(3, 8.5)</b> generates not(INPUT2(value in(3, 8.5)))
         * @return
         */
        public Builder withNotInConstraints(Map<String, List<Object>> constraints) {
            this.toBuild.notInConstraints = constraints;
            return this;
        }

        /**
         * Add a <b>break</b> statement to the lhs of the rule
         * (e.g. ifBreakField = "SEPAL_WIDTH"; ifBreakOperator = ">="; ifBreakValue = 5.45 generates</br>
         * <p><code>$inputField: SEPAL_WIDTH()</code></p>
         * <p><code>if ($inputField.getValue() >= 5.45) break[match]</code></p>
         * @param ifBreakField
         * @param ifBreakOperator
         * @param ifBreakValue
         * @return
         */
        public Builder withIfBreak(String ifBreakField, String ifBreakOperator, Object ifBreakValue) {
            this.toBuild.ifBreakField = ifBreakField;
            this.toBuild.ifBreakOperator = ifBreakOperator;
            this.toBuild.ifBreakValue = ifBreakValue;
            return this;
        }

        public Builder withResultCode(StatusCode resultCode) {
            this.toBuild.resultCode = resultCode;
            return this;
        }

        public Builder withResult(Object result) {
            this.toBuild.result = result;
            return this;
        }

        public KiePMMLDrooledRule build() {
            return toBuild;
        }
    }
}
