package org.drools.pmml.pmml_4_2.model.scorecard;

public class InitialScore extends Score {
	public InitialScore() {
		super();
	}
	
	public InitialScore(String scoreCard, Double value) {
		super(scoreCard,value);
	}
	
	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("InitialScore( ");
		bldr.append("scoreCard=").append(getScoreCard()).append(", ");
		bldr.append("value=").append(getValue()).append(" )");
		return bldr.toString();
	}
}
