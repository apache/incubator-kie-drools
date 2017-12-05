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
package org.kie.pmml.pmml_4_2.model;

import java.util.Map;

import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class ScoreCard {
    private String modelName;
    private double score;
    private AbstractPMMLData holder;
    private boolean enableRC;
    private boolean pointsBelow;
    private Map ranking;

    public ScoreCard(String modelName) {
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid attempt to create a ScoreCardObject. The modelName cannot be null or empty");
        }
        this.modelName = modelName;
    }

    public ScoreCard(String modelName, double score, AbstractPMMLData holder, boolean enableRC, boolean pointsBelow, Map ranking) {
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid attempt to create a ScoreCardObject. The modelName cannot be null or empty");
        }
        this.modelName = modelName;
        this.score = score;
        this.holder = holder;
        this.enableRC = enableRC;
        this.pointsBelow = pointsBelow;
        this.ranking = ranking;
    }

    public String getModelName() {
        return modelName;
    }

    protected void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public AbstractPMMLData getHolder() {
        return holder;
    }

    public void setHolder(AbstractPMMLData holder) {
        this.holder = holder;
    }

    public boolean isEnableRC() {
        return enableRC;
    }

    public void setEnableRC(boolean enableRC) {
        this.enableRC = enableRC;
    }

    public boolean isPointsBelow() {
        return pointsBelow;
    }

    public void setPointsBelow(boolean pointsBelow) {
        this.pointsBelow = pointsBelow;
    }

    public Map getRanking() {
        return ranking;
    }

    public void setRanking(Map ranking) {
        this.ranking = ranking;
    }


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScoreCard other = (ScoreCard) obj;
		if (modelName == null) {
			if (other.modelName != null) {
				return false;
			}
		} else if (!modelName.equals(other.modelName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ScoreCard [modelName=" + modelName + ", score=" + score + ", holder=" + holder + ", enableRC="
				+ enableRC + ", pointsBelow=" + pointsBelow + ", ranking=" + ranking + "]";
	}
    
    
}
