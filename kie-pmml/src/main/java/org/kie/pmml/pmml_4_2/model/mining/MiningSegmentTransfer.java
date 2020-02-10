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
package org.kie.pmml.pmml_4_2.model.mining;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;

public class MiningSegmentTransfer {
	private String correlationId;
	private String segmentationId;
	private String fromSegmentId;
	private String toSegmentId;
	private Map<String, String> requestFromResultMap;
	private PMMLRequestData outboundRequest;
	private PMML4Result inboundResult;
	
	public MiningSegmentTransfer(String segmentationId, String fromSegmentId, String toSegmentId) {
		this.segmentationId = segmentationId;
		this.fromSegmentId = fromSegmentId;
		this.toSegmentId = toSegmentId;
		this.requestFromResultMap = new HashMap<>();
	}
	
	public MiningSegmentTransfer(PMML4Result inboundResult, String toSegmentId) {
		this.inboundResult = inboundResult;
		this.correlationId = inboundResult.getCorrelationId();
		this.segmentationId = inboundResult.getSegmentationId();
		this.fromSegmentId = inboundResult.getSegmentId();
		this.toSegmentId = toSegmentId;
		this.requestFromResultMap = new HashMap<>();
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

	public String getFromSegmentId() {
		return fromSegmentId;
	}

	public void setFromSegmentId(String fromSegmentId) {
		this.fromSegmentId = fromSegmentId;
	}

	public String getToSegmentId() {
		return toSegmentId;
	}

	public void setToSegmentId(String toSegmentId) {
		this.toSegmentId = toSegmentId;
	}

	public PMMLRequestData getOutboundRequest() {
		if (outboundRequest == null) {
			outboundRequest = new PMMLRequestData(this.correlationId);
			outboundRequest.setSource("MiningSegmentTransfer:"+this.fromSegmentId+"-"+this.toSegmentId);
			for (String requestField: requestFromResultMap.keySet()) {
				String resultFieldName = requestFromResultMap.get(requestField);
				Object resultFieldValue = getValueFromResult(resultFieldName);
				if (resultFieldValue != null) {
					outboundRequest.addRequestParam(requestField, resultFieldValue);
				}
			}
		}
		return outboundRequest;
	}
	
	public Map<String,String> getRequestFromResultMap() {
		return new HashMap<>(this.requestFromResultMap);
	}

	
	
	private Object getValueFromResult(String fieldName) {
		if (fieldName.contains(".")) {
			String fieldParts[] = fieldName.split("\\.");
			if (fieldParts != null && fieldParts.length == 2) {
				return inboundResult.getResultValue(fieldParts[0],fieldParts[1]);
			} else {
				throw new IllegalStateException("MiningSegmentTransfer: Result field name is invalid - "+fieldName);
			}
		} else {
			return inboundResult.getResultValue(fieldName,null);
		}
	}

	public void setOutboundRequest(PMMLRequestData outboundRequest) {
		this.outboundRequest = outboundRequest;
	}

	public PMML4Result getInboundResult() {
		return inboundResult;
	}

	public void setInboundResult(PMML4Result inboundResult) {
		this.inboundResult = inboundResult;
	}

	public Map<String, String> getResultFieldNameToRequestFieldName() {
		return requestFromResultMap;
	}
	
	public String addResultToRequestMapping(String resultFieldName, String requestFieldName) {
		return this.requestFromResultMap.put(requestFieldName, resultFieldName);
	}
	
	public void addResultToRequestMappings(Map<String,String> collection) {
		this.requestFromResultMap.putAll(collection);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((correlationId == null) ? 0 : correlationId.hashCode());
		result = prime * result + ((fromSegmentId == null) ? 0 : fromSegmentId.hashCode());
		result = prime * result + ((segmentationId == null) ? 0 : segmentationId.hashCode());
		result = prime * result + ((toSegmentId == null) ? 0 : toSegmentId.hashCode());
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
		MiningSegmentTransfer other = (MiningSegmentTransfer) obj;
		if (correlationId == null) {
			if (other.correlationId != null) {
				return false;
			}
		} else if (!correlationId.equals(other.correlationId)) {
			return false;
		}
		if (fromSegmentId == null) {
			if (other.fromSegmentId != null) {
				return false;
			}
		} else if (!fromSegmentId.equals(other.fromSegmentId)) {
			return false;
		}
		if (segmentationId == null) {
			if (other.segmentationId != null) {
				return false;
			}
		} else if (!segmentationId.equals(other.segmentationId)) {
			return false;
		}
		if (toSegmentId == null) {
			if (other.toSegmentId != null) {
				return false;
			}
		} else if (!toSegmentId.equals(other.toSegmentId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("MiningSegmentTransfer [");
		bldr.append("correlationId=").append(correlationId).append(", ");
		bldr.append("segmentationId=").append(segmentationId).append(", ");
		bldr.append("fromSegmentId=").append(fromSegmentId).append(", ");
		bldr.append("toSegmentId=").append(toSegmentId).append(", ");
		bldr.append("resultFieldNameToRequestFieldName=").append(requestFromResultMap).append(", ");
		bldr.append("inboundResult=").append(inboundResult).append(", ");
		bldr.append("outboundRequest=").append(getOutboundRequest());
		
		bldr.append("]");
		return bldr.toString();
	}
	
	

}
