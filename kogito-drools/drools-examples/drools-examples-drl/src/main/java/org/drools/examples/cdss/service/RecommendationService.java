package org.drools.examples.cdss.service;

import java.util.ArrayList;
import java.util.List;

import org.drools.examples.cdss.data.Recommendation;

public class RecommendationService {
	
	private List<Recommendation> recommendations = new ArrayList<Recommendation>();
	
	public void createRecommendation(Recommendation recommendation) {
		recommendations.add(recommendation);
	}
	
	public List<Recommendation> getRecommendations() {
		return recommendations;
	}

}
