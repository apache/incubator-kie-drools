package org.drools.pmml.pmml_4_2.model;

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScoreCard scorecard = (ScoreCard) o;

        if (Double.compare(scorecard.getScore(),
                           getScore()) != 0) {
            return false;
        }
        if (isEnableRC() != scorecard.isEnableRC()) {
            return false;
        }
        if (isPointsBelow() != scorecard.isPointsBelow()) {
            return false;
        }
        if (!getModelName().equals(scorecard.getModelName())) {
            return false;
        }
        if (getHolder() != null ? !getHolder().equals(scorecard.getHolder()) : scorecard.getHolder() != null) {
            return false;
        }
        return getRanking() != null ? getRanking().equals(scorecard.getRanking()) : scorecard.getRanking() == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getModelName().hashCode();
        temp = Double.doubleToLongBits(getScore());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (getHolder() != null ? getHolder().hashCode() : 0);
        result = 31 * result + (isEnableRC() ? 1 : 0);
        result = 31 * result + (isPointsBelow() ? 1 : 0);
        result = 31 * result + (getRanking() != null ? getRanking().hashCode() : 0);
        return result;
    }

	@Override
	public String toString() {
		return "ScoreCard [modelName=" + modelName + ", score=" + score + ", holder=" + holder + ", enableRC="
				+ enableRC + ", pointsBelow=" + pointsBelow + ", ranking=" + ranking + "]";
	}
    
    
}
