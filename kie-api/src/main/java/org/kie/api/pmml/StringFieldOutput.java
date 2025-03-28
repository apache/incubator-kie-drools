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
package org.kie.api.pmml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="value")
@XmlAccessorType(XmlAccessType.FIELD)
public class StringFieldOutput extends AbstractOutput<String> {
    private String value;

    public StringFieldOutput() {
        super();
    }
    
    public StringFieldOutput(String correlationId, String name, String displayValue, Double weight, String value) {
        super(correlationId, name, displayValue, weight);
        this.value = value;
    }

    public StringFieldOutput(String correlationId, String segmentationId, String segmentId, String name,
            String displayValue, Double weight, String value) {
        super(correlationId, segmentationId, segmentId, name, displayValue, weight);
        this.value = value;
    }

    public StringFieldOutput(String correlationId, String segmentationId, String segmentId, String name, String value) {
        super(correlationId, segmentationId, segmentId, name);
        this.value = value;
    }

    public StringFieldOutput(String correlationId, String name, String value) {
        super(correlationId, name);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        StringFieldOutput other = (StringFieldOutput) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "StringFieldOutput [correlationId=" + getCorrelationId() + ", segmentationId="
                + getSegmentationId() + ", segmentId=" + getSegmentId() + ", name=" + getName()
                + ", displayValue=" + getDisplayValue() + ", value=" + value + ", weight=" + weight + "]";
    }
    
}
