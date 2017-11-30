/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.pmml.pmml_4_2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class provides a vanilla mechanism for sending data to
 * the rule engine, for use in having a PMML model applied against it
 * 
 */
public class PMMLRequestData {
	private String correlationId;
    private String modelName;
    private String source;
    private List<ParameterInfo> requestParams;

    public PMMLRequestData(String correlationId) {
    	this.correlationId = correlationId;
        this.requestParams = new ArrayList<>();
    }

    public PMMLRequestData(String correlationId, String modelName) {
    	this.correlationId = correlationId;
        this.modelName = modelName;
        this.requestParams = new ArrayList<>();
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getCompactCapitalizedModelName() {
        String[] tokens = modelName.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part: tokens) {
            builder.append(part.substring(0,1).toUpperCase()).append(part.substring(1));
        }
        return builder.toString();
    }

    public synchronized Map<String, ParameterInfo> getMappedRequestParams() {
        return requestParams.stream().collect(Collectors.toMap(pi -> pi.getName(),pi -> pi));
    }

    public synchronized Collection<ParameterInfo> getRequestParams() {
        return new ArrayList<>(this.requestParams);
    }

    public synchronized boolean addRequestParam(ParameterInfo parameter) {
        return this.requestParams.add(parameter);
    }

    public synchronized boolean removeRequestParam(ParameterInfo parameter) {
        return this.requestParams.remove(parameter);
    }

    public synchronized boolean addRequestParam(String paramName, Object value) {
    	if (paramName == null || paramName.trim().isEmpty() || value == null) {
    		return false;
    	}
        Class<?> clazz = value.getClass();
        ParameterInfo parameter = new ParameterInfo(this.correlationId, paramName, clazz, value);
        return this.addRequestParam(parameter);
    }
    
    public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public String getSource() {
		return this.source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
		result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
		result = prime * result + ((requestParams == null) ? 0 : requestParams.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		PMMLRequestData other = (PMMLRequestData) obj;
		if (correlationId == null) {
			if (other.correlationId != null) {
				return false;
			}
		} else if (!correlationId.equals(other.correlationId)) {
			return false;
		}
		if (modelName == null) {
			if (other.modelName != null) {
				return false;
			}
		} else if (!modelName.equals(other.modelName)) {
			return false;
		}
		if (requestParams == null) {
			if (other.requestParams != null) {
				return false;
			}
		} else if (!requestParams.equals(other.requestParams)) {
			return false;
		}
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		return true;
	}

	@Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("PMMLRequestData( ");
        stringBuilder.append("correlationId=").append(correlationId).append(", ");
        stringBuilder.append("modelName=").append(modelName).append(", ");
        stringBuilder.append("source=").append(source).append(", requestParams=[");
        Iterator<ParameterInfo> iter = requestParams.iterator();
        boolean firstParam = true;
        while (iter.hasNext()) {
            if (!firstParam) {
                stringBuilder.append(", ");
            } else {
                firstParam = false;
            }
            ParameterInfo pi = iter.next();
            stringBuilder.append(pi.toString());
        }
        stringBuilder.append("] )");
        return stringBuilder.toString();
    }
}
