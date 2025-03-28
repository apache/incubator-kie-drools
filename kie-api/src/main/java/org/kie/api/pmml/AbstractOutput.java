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
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="pmmlOutput")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractOutput<T> implements PMML4Output<T> {
    @XmlAttribute(name="correlationId", required=true)
    private String correlationId;
    @XmlAttribute(name="segmentationId")
    private String segmentationId;
    @XmlAttribute(name="segmentId")
    private String segmentId;
    @XmlAttribute(name="name", required=true)
    private String name;
    @XmlAttribute(name="displayValue")
    private String displayValue;
    @XmlElement(name="weight")
    protected Double weight;
    
    public AbstractOutput() {
        
    }
    
    public AbstractOutput(String correlationId, String name) {
        super();
        this.correlationId = correlationId;
        this.name = name;
    }

    public AbstractOutput(String correlationId, String segmentationId, String segmentId, String name) {
        super();
        this.correlationId = correlationId;
        this.segmentationId = segmentationId;
        this.segmentId = segmentId;
        this.name = name;
    }

    public AbstractOutput(String correlationId, String name, String displayValue, Double weight) {
        super();
        this.correlationId = correlationId;
        this.name = name;
        this.displayValue = displayValue;
        this.weight = weight;
    }

    public AbstractOutput(String correlationId, String segmentationId, String segmentId, String name,
            String displayValue, Double weight) {
        super();
        this.correlationId = correlationId;
        this.segmentationId = segmentationId;
        this.segmentId = segmentId;
        this.name = name;
        this.displayValue = displayValue;
        this.weight = weight;
    }

    public String getCorrelationId() {
        return correlationId;
    }
    
    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
    
    public String getSegmentationId() {
        return segmentationId;
    }

    public void setSegmentationId(String segmentationId) {
        this.segmentationId = segmentationId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(String segmentId) {
        this.segmentId = segmentId;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
    
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
    
    
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((segmentId == null) ? 0 : segmentId.hashCode());
        result = prime * result + ((segmentationId == null) ? 0 : segmentationId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractOutput other = (AbstractOutput) obj;
        if (correlationId == null) {
            if (other.correlationId != null) {
                return false;
            }
        } else if (!correlationId.equals(other.correlationId)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (segmentId == null) {
            if (other.segmentId != null) {
                return false;
            }
        } else if (!segmentId.equals(other.segmentId)) {
            return false;
        }
        if (segmentationId == null) {
            if (other.segmentationId != null) {
                return false;
            }
        } else if (!segmentationId.equals(other.segmentationId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AbstractOutput [correlationId=" + correlationId + ", segmentationId=" + segmentationId + ", segmentId="
                + segmentId + ", name=" + name + ", displayValue=" + displayValue + ", weight=" + weight + "]";
    }

}
