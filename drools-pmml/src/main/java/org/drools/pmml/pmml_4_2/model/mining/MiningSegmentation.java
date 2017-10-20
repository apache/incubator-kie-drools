package org.drools.pmml.pmml_4_2.model.mining;

import java.util.ArrayList;
import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.MULTIPLEMODELMETHOD;
import org.dmg.pmml.pmml_4_2.descr.Segment;
import org.dmg.pmml.pmml_4_2.descr.Segmentation;
import org.drools.pmml.pmml_4_2.model.Miningmodel;
import org.drools.pmml.pmml_4_2.model.PMMLMiningField;

public class MiningSegmentation {
	private Miningmodel owner;
	private String segmentationId;
	private MULTIPLEMODELMETHOD multipleModelMethod;
	private List<MiningSegment> miningSegments;
	private static final String SEGMENTATION_HEADER_SCRIPT = ""
			+ "rule 'Segmentation Start - @{segmentationId}'"
			+ "agenda-group '@{modelId}_@{segmentationId}"
			+ "salience 1000"
			+ "when"
			+ "then"
			+ "end";
	
	public MiningSegmentation(Miningmodel owner, Segmentation segmentation) {
		this.owner = owner;
		this.multipleModelMethod = segmentation.getMultipleModelMethod();
		this.miningSegments = new ArrayList<>();
		initSegments(segmentation.getSegments());
	}
	
	private void initSegments(List<Segment> segments) {
		if (segments != null && !segments.isEmpty()) {
			for (Segment seg: segments) {
				MiningSegment ms = new MiningSegment(this,seg);
				miningSegments.add(ms);
			}
		}
	}
	
	public Miningmodel getOwner() {
		return this.owner;
	}
	
	public List<PMMLMiningField> getMiningFields() {
		return this.owner.getMiningFields();
	}
	
	public String getMiningPojoClassName() {
		return this.owner.getMiningPojoClassName();
	}

	public MULTIPLEMODELMETHOD getMultipleModelMethod() {
		return this.multipleModelMethod;
	}
	
	public void setMultipleModelMethod(MULTIPLEMODELMETHOD multipleModelMethod) {
		this.multipleModelMethod = multipleModelMethod;
	}
	
	public List<MiningSegment> getMiningSegments() {
		return this.miningSegments;
	}
	
	public String getSegmentationId() {
		if (segmentationId == null || segmentationId.trim().length() < 1) {
			segmentationId = owner.getModelId().concat("Segmentation");
		}
		return segmentationId;
	}

	public void setSegmentationId(String segmentationId) {
		this.segmentationId = segmentationId;
	}

	public String generateSegmentationRules() {
		StringBuilder builder = new StringBuilder();
		
		switch (this.multipleModelMethod) {
			case AVERAGE:
				break;
			case MAJORITY_VOTE:
				break;
			case MAX:
				break;
			case MEDIAN:
				break;
			case MODEL_CHAIN:
				break;
			case SELECT_ALL:
				break;
			case SELECT_FIRST:
				for (int x = 0; x < miningSegments.size(); x++) {
					String segmentationAgendaGroup = getOwner().getModelId()+"_"+getSegmentationId();
					String segRules = miningSegments.get(x).generateSegmentRules(segmentationAgendaGroup,x);
					if (segRules != null && !segRules.trim().isEmpty()) {
						builder.append(segRules);
					}
				}
				break;
			case SUM:
				break;
			case WEIGHTED_AVERAGE:
				break;
			case WEIGHTED_MAJORITY_VOTE:
				break;
		}
		
		return builder.toString();
	}
}
