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
package org.kie.pmml.models.drools.tuples;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Tuple representing the <b>Reason Code</b> and its computed <b>value</b> as used inside <b>Scorecard</b>
 *
 * @see <a href=http://dmg.org/pmml/v4-4/Scorecard.html#rankinReasongCodes>Ranking Reason Codes</a>
 */
public class KiePMMLReasonCodeAndValue implements Serializable {

    private static final long serialVersionUID = 5978972455322748898L;
    private final String reasonCode;
    private final double value;

    public KiePMMLReasonCodeAndValue(String reasonCode, double value) {
        this.reasonCode = reasonCode;
        this.value = value;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLReasonCodeAndValue.class.getSimpleName() + "[", "]")
                .add("reasonCode='" + reasonCode + "'")
                .add("value=" + value)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLReasonCodeAndValue that = (KiePMMLReasonCodeAndValue) o;
        return Double.compare(that.value, value) == 0 &&
                Objects.equals(reasonCode, that.reasonCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reasonCode, value);
    }
}
