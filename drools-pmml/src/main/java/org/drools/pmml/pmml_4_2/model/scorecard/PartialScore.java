package org.drools.pmml.pmml_4_2.model.scorecard;

public class PartialScore extends Score {
	private String characteristic;
	private String reasonCode;
	private Double weight;
	
	
	public PartialScore() {
		super();
	}
	
	public PartialScore(String scoreCard, Double value, String characteristic, String reasonCode, Double weight) {
		super(scoreCard,value);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
		this.weight = weight;
	}
	
	public String getCharacteristic() {
		return characteristic;
	}
	public void setCharacteristic(String characteristic) {
		this.characteristic = characteristic;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((characteristic == null) ? 0 : characteristic.hashCode());
		result = prime * result + ((reasonCode == null) ? 0 : reasonCode.hashCode());
		result = prime * result + ((weight == null) ? 0 : weight.hashCode());
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
		PartialScore other = (PartialScore) obj;
		if (characteristic == null) {
			if (other.characteristic != null)
				return false;
		} else if (!characteristic.equals(other.characteristic))
			return false;
		if (reasonCode == null) {
			if (other.reasonCode != null)
				return false;
		} else if (!reasonCode.equals(other.reasonCode))
			return false;
		if (weight == null) {
			if (other.weight != null)
				return false;
		} else if (!weight.equals(other.weight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PartialScore [characteristic=" + characteristic + ", reasonCode=" + reasonCode + ", weight=" + weight
				+ ", scoreCard=" + getScoreCard() + ", value=" + getValue() + "]";
	}
	
	
}
