package org.drools.pmml.pmml_4_2.model.scorecard;

public class Score {
	private String scoreCard;
	private Double value;
	
	public Score() {
		this.scoreCard = null;
		this.value = null;
	}
	
	public Score(String scoreCard) {
		this.scoreCard = scoreCard;
		this.value = null;
	}
	
	public Score(String scoreCard, Double value) {
		this.scoreCard = scoreCard;
		this.value = value;
	}

	public String getScoreCard() {
		return scoreCard;
	}

	public void setScoreCard(String scoreCard) {
		this.scoreCard = scoreCard;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scoreCard == null) ? 0 : scoreCard.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Score other = (Score) obj;
		if (scoreCard == null) {
			if (other.scoreCard != null)
				return false;
		} else if (!scoreCard.equals(other.scoreCard))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("Score( ");
		bldr.append("scoreCard=").append(this.scoreCard).append(", ");
		bldr.append("value = ").append(this.value).append(" )");
		return bldr.toString();
	}
}
