package org.drools.pmml.pmml_4_2.model.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public abstract class AbstractTreeToken {
	private String context;
	private String current;
	private String marker;
	private String visitMode;
	private Boolean downward;
	private List trail;
	private Map results;
	private Double confidence;
	private Double totalCount;
	
	public AbstractTreeToken() {
		
	}
	
	public AbstractTreeToken(String context) {
		this.context = context;
	}
	
	
	
	public AbstractTreeToken(String context, String current, String marker, String visitMode, Boolean downward,
			List trail, Map results, Double confidence, Double totalCount) {
		this.context = context;
		this.current = current;
		this.marker = marker;
		this.visitMode = visitMode;
		this.downward = downward;
		this.trail = trail;
		this.results = results;
		this.confidence = confidence;
		this.totalCount = totalCount;
	}

	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	public String getMarker() {
		return marker;
	}
	public void setMarker(String marker) {
		this.marker = marker;
	}
	public String getVisitMode() {
		return visitMode;
	}
	public void setVisitMode(String visitMode) {
		this.visitMode = visitMode;
	}
	public Boolean getDownward() {
		return downward;
	}
	public void setDownward(Boolean downward) {
		this.downward = downward;
	}
	public List getTrail() {
		return trail;
	}
	public void setTrail(List trail) {
		this.trail = trail;
	}
	public Map getResults() {
		return results;
	}
	public void setResults(Map results) {
		this.results = results;
	}
	public Double getConfidence() {
		return confidence;
	}
	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}
	public Double getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Double totalCount) {
		this.totalCount = totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount * 1.0;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("context=").append(this.context).append(", ");
		builder.append("current=").append(this.current).append(", ");
		builder.append("marker=").append(this.marker).append(", ");
		builder.append("visitMode=").append(this.visitMode).append(", ");
		builder.append("downward=").append(this.downward).append(", ");
		builder.append("trail=[");
		Iterator lstIter = this.trail.iterator();
		while (lstIter.hasNext()) {
			builder.append("   ").append(lstIter.next().toString());
			if (lstIter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("], ");
		builder.append("results=[");
		Iterator keyIter = this.results.keySet().iterator();
		while (keyIter.hasNext()) {
			Object key = keyIter.next();
			Object value = this.results.get(key);
			builder.append("   ").append(key.toString()).append("->").append(value != null ? value.toString() : "null");
			if (keyIter.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append("], ");
		builder.append("confidence=").append(this.confidence).append(", ");
		builder.append("totalCount=").append(this.totalCount).append(", ");
		return builder.toString();
	}
}
