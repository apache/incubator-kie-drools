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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.kie.api.definition.type.PropertyReactive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PropertyReactive
@XmlType(name = "PMML4Result")
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "pmml4Result")
public class PMML4Result {
    private static final Logger LOG = LoggerFactory.getLogger(PMML4Result.class);
    @XmlAttribute(name="correlationId", required=true)
    private String correlationId;
    @XmlElement(name="segmentationId")
    private String segmentationId;
    @XmlElement(name="segmentId")
    private String segmentId;
    @XmlElement(name="segmentIndex")
    private int segmentIndex;
    @XmlAttribute(name="resultCode", required=true)
    private String resultCode;
    @XmlAttribute(name="resultObjectName")
    private String resultObjectName;
    @XmlElementWrapper(name="resultVariables")
    private Map<String, Object> resultVariables;

    
    public PMML4Result() {
        resultVariables = new HashMap<>();
    }
    
    public PMML4Result(String correlationId) {
        this.correlationId = correlationId;
        this.resultVariables = new HashMap<>();
    }
    
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
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

    public int getSegmentIndex() {
        return segmentIndex;
    }

    public void setSegmentIndex(int segmentIndex) {
        this.segmentIndex = segmentIndex;
    }

    
    public String getResultObjectName() {
        return resultObjectName;
    }

    public void setResultObjectName(String resultObjectName) {
        this.resultObjectName = resultObjectName;
    }

    public Map<String, Object> getResultVariables() {
        if (resultVariables == null) {
            resultVariables = new HashMap<>();
        }
        return resultVariables;
    }
    
    public void updateResultVariable(String objName, Object obj) {
        if (this.resultVariables == null) {
            this.resultVariables = new HashMap<>();
        }
        resultVariables.put(objName,obj);
    }

    public void setResultVariables(Map<String, Object> resultVariables) {
        this.resultVariables = resultVariables;
    }
    
    public void addResultVariable(String objName, Object object) {
        if (this.resultVariables == null) {
            this.resultVariables = new HashMap<>();
        }
        this.resultVariables.put(objName, object);
    }
    
    private String getGetterMethodName(Object wrapper, String fieldName, String prefix) {
        String capFieldName = fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
        return prefix + capFieldName;
    }
    
    public <T> Optional<T> getResultValue(String objName, String objField, Class<T> clazz, Object...params) {
        T value = null;
        Object obj = getResultValue(objName, objField, params);
        if (clazz != null && obj != null && clazz.isAssignableFrom(obj.getClass())) {
            value = (T)obj;
        }
        return value != null ? Optional.of(value) : Optional.empty();
    }
    
    public Object getResultValue(String objName, String objField, Object...params) {
        Object value = null;
        Object holder = getResultVariables().get(objName);
        if (holder != null) {
            if (objField != null && !objField.trim().isEmpty()) {
                if (Map.class.isAssignableFrom(holder.getClass())) {
                    value = ((Map) holder).get(objField);
                    return value;
                }
                String defFldRetriever = getGetterMethodName(holder,objField,"get");
                try {
                    Class[] paramTypes = null;
                    Method m = null;
                    boolean retry = true;
                    if (params != null && params.length > 0) {
                        paramTypes = new Class[params.length];
                        for (int x = 0; x < params.length;x++) {
                            paramTypes[x] = params[x].getClass();
                        }
                        do {
                            try {
                                m = holder.getClass().getMethod(defFldRetriever, paramTypes);
                            } catch (NoSuchMethodException nsmx) {
                                if (m == null && defFldRetriever.startsWith("get")) {
                                    defFldRetriever = getGetterMethodName(holder,objField,"is");
                                } else {
                                    retry = false;
                                }
                            }
                        } while (m == null && retry);
                    } else {
                        do {
                            try {
                                m = holder.getClass().getMethod(defFldRetriever);
                            } catch (NoSuchMethodException nsmx) {
                                if (m == null && defFldRetriever.startsWith("get")) {
                                    defFldRetriever = getGetterMethodName(holder,objField,"is");
                                } else {
                                    retry = false;
                                }
                            }
                        } while (m == null && retry);
                    }
                    if (m != null) {
                        if (params != null && params.length > 0) {
                            value = m.invoke(holder, params);
                        } else {
                            value = m.invoke(holder);
                        }
                    }
                } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    LOG.error("Exception", e1);
                }
            } else {
                value = holder;
            }
            
        }
        return value;
    }
    
    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
        result = prime * result + ((segmentId == null) ? 0 : segmentId.hashCode());
        result = prime * result + segmentIndex;
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
        PMML4Result other = (PMML4Result) obj;
        if (correlationId == null) {
            if (other.correlationId != null) {
                return false;
            }
        } else if (!correlationId.equals(other.correlationId)) {
            return false;
        }
        if (segmentId == null) {
            if (other.segmentId != null) {
                return false;
            }
        } else if (!segmentId.equals(other.segmentId)) {
            return false;
        }
        if (segmentIndex != other.segmentIndex) {
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
        return "PMML4Result [correlationId=" + correlationId + ", segmentationId=" + segmentationId + ", segmentId="
                + segmentId + ", segmentIndex=" + segmentIndex + ", resultCode=" + resultCode + ", resultVariables="
                + resultVariables + "]";
    }

    
}
