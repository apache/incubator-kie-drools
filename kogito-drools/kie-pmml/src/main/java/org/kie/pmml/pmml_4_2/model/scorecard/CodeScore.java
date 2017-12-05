/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.scorecard;


import org.kie.pmml.pmml_4_2.model.ScoreCard;

public class CodeScore {
	private ScoreCard scoreCard;
	private String code;
	private Double score;
	
	
	public CodeScore(ScoreCard scoreCard, String code, Double score) {
		super();
		this.scoreCard = scoreCard;
		this.code = code;
		this.score = score;
	}


	public ScoreCard getScoreCard() {
		return scoreCard;
	}


	public void setScoreCard(ScoreCard scoreCard) {
		this.scoreCard = scoreCard;
	}


	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public Double getScore() {
		return score;
	}


	public void setScore(Double score) {
		this.score = score;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((score == null) ? 0 : score.hashCode());
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
		CodeScore other = (CodeScore) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (score == null) {
			if (other.score != null)
				return false;
		} else if (!score.equals(other.score))
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
		return "CodeScore [scoreCard=" + scoreCard.toString() + ", code=" + code + ", score=" + score + "]";
	}
	
	
}
