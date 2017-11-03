package org.drools.pmml.pmml_4_2;

import java.util.HashMap;
import java.util.Map;

import org.drools.pmml.pmml_4_2.model.mining.SegmentExecution;

public class PMML4Result {
	private String resultCode;
	private String segmentationId;
	private String segmentId;
	private int segmentIndex;
	private Map<String, Object> resultVariables;
	
	public PMML4Result() {
		resultVariables = new HashMap<>();
	}
	
	public PMML4Result(SegmentExecution segEx) {
		this.segmentationId = segEx.getSegmentationId();
		this.segmentId = segEx.getSegmentId();
		this.segmentIndex = segEx.getSegmentIndex();
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

	public Map<String, Object> getResultVariables() {
		return resultVariables;
	}

	public void setResultVariables(Map<String, Object> resultVariables) {
		this.resultVariables = resultVariables;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		PMML4Result other = (PMML4Result) obj;
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

	
}
