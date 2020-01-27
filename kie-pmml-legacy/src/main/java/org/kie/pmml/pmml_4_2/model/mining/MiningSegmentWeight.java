package org.kie.pmml.pmml_4_2.model.mining;

import java.util.List;

public class MiningSegmentWeight {
	private String segmentationId;
	private String segmentId;
	private String targetName;
	private Double weight;
	private Number segmentValue;
	
	public MiningSegmentWeight(MiningSegment segment) {
		super();
		this.segmentationId = segment.getOwner().getSegmentationId();
		this.segmentId = segment.getSegmentId();
		List<String> targets = segment.getTargetsForWeighting();
		if (targets != null && !targets.isEmpty()) {
		   this.targetName = targets.get(0);
		}
		this.weight = segment.getWeight();
	}
	public MiningSegmentWeight(String segmentationId, String segmentId, String targetName, Double weight) {
		super();
		this.segmentationId = segmentationId;
		this.segmentId = segmentId;
		this.targetName = targetName;
		this.weight = weight;
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

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Number getSegmentValue() {
		return segmentValue;
	}

	public Double getSegmentValueAsDouble() {
		return segmentValue != null ? segmentValue.doubleValue() : null;
	}

	public void setSegmentValue(Number segmentValue) {
		this.segmentValue = segmentValue;
	}
	
	public Number getWeightedSegmentValue() {
		return segmentValue != null ? segmentValue.doubleValue() * weight : null;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MiningSegmentWeight other = (MiningSegmentWeight) obj;
		if (segmentId == null) {
			if (other.segmentId != null)
				return false;
		} else if (!segmentId.equals(other.segmentId))
			return false;
		if (segmentationId == null) {
			if (other.segmentationId != null)
				return false;
		} else if (!segmentationId.equals(other.segmentationId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "MiningSegmentWeight [segmentationId=" + segmentationId + ", segmentId=" + segmentId + ", targetName="
				+ targetName + ", weight=" + weight + ", segmentValue=" + segmentValue + "]";
	}

	
}
