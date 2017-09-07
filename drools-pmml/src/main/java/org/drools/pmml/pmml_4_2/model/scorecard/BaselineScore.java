package org.drools.pmml.pmml_4_2.model.scorecard;

public class BaselineScore extends Score {
	private String characteristic;
	
	public BaselineScore() {
		super();
	}
	
	public BaselineScore(String scoreCard, Double value, String characteristic) {
		super(scoreCard,value);
		this.characteristic = characteristic;
	}

	public String getCharacteristic() {
		return characteristic;
	}

	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((characteristic == null) ? 0 : characteristic.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaselineScore other = (BaselineScore) obj;
		if (characteristic == null) {
			if (other.characteristic != null)
				return false;
		} else if (!characteristic.equals(other.characteristic))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("BaselineScore( ");
		bldr.append("scoreCard=").append(getScoreCard()).append(", ");
		bldr.append("value=").append(getValue()).append(", ");
		bldr.append("characteristic=").append(this.characteristic).append(" )");
		return bldr.toString();
	}
}
