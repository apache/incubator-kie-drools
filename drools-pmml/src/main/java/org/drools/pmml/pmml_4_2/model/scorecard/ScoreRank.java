package org.drools.pmml.pmml_4_2.model.scorecard;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.drools.compiler.lang.dsl.DSLMapParser.key_chunk_return;
import org.drools.pmml.pmml_4_2.model.ScoreCard;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class ScoreRank {
	private ScoreCard scoreCard;
	private Map rank;
	
	public ScoreRank() {
	}
	
	public ScoreRank(ScoreCard scoreCard, Map rank) {
		this.scoreCard = scoreCard;
		this.rank = rank;
	}
	
	public ScoreRank(ScoreCard scoreCard) {
		this.scoreCard = scoreCard;
		this.rank = new HashMap();
	}
	
	public ScoreCard getScoreCard() {
		return scoreCard;
	}
	public void setScoreCard(ScoreCard scoreCard) {
		this.scoreCard = scoreCard;
	}
	public Map getRank() {
		return rank;
	}
	public void setRank(Map rank) {
		this.rank = rank;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((scoreCard == null) ? 0 : scoreCard.hashCode());
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
		ScoreRank other = (ScoreRank) obj;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (scoreCard == null) {
			if (other.scoreCard != null)
				return false;
		} else if (!scoreCard.equals(other.scoreCard))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("ScoreRank = ( ");
		bldr.append("scoreCard = \"").append(this.scoreCard).append("\", ");
		bldr.append("rank = [");
		boolean first = true;
		for (Object key: rank.keySet()) {
			if (!first) {
				bldr.append(", ");
			} else {
				first = false;
			}
			bldr.append("<key = ").append(key).append(", value = ").append((rank.get(key)).toString()).append(">");
		}
		bldr.append("])");
		return bldr.toString();
	}
}
