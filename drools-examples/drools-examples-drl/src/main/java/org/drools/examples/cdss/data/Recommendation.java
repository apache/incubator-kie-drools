package org.drools.examples.cdss.data;

public class Recommendation {

	private String recommendation;
	
	public Recommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	public String getRecommendation() {
		return recommendation;
	}
	
	public String toString() {
		return "Recommendation: " + recommendation;
	}
	
}
