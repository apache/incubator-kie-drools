package org.drools.examples.cdss.service;

import java.util.ArrayList;
import java.util.List;

import org.drools.examples.cdss.data.Recommendation;

public class RecommendationService {
	
	private List recommendations = new ArrayList();
	
	public void createRecommendation(Recommendation recommendation) {
		recommendations.add(recommendation);
	}
	
	public List getRecommendations() {
		return recommendations;
	}

}
