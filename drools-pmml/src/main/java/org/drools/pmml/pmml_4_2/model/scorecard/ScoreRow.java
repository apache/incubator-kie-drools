package org.drools.pmml.pmml_4_2.model.scorecard;

public class ScoreRow extends Score {
	private String characteristic;
	private String reasonCode;
	private String id;
	
	
	public ScoreRow() {
		super();
	}
	
	public ScoreRow(String scoreCard, Double value, String characteristic, String reasonCode, String id) {
		super(scoreCard, value);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
		this.id = id;
	}

	public ScoreRow(String scoreCard, String characteristic, String reasonCode, String id) {
		super(scoreCard);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
		this.id = id;
	}

	public ScoreRow(String scoreCard, Double value, String characteristic, String reasonCode) {
		super(scoreCard, value);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
	}

	public ScoreRow(String scoreCard, String characteristic, String reasonCode) {
		super(scoreCard);
		this.characteristic = characteristic;
		this.reasonCode = reasonCode;
	}

	public ScoreRow(String scoreCard, Double value, String characteristic) {
		super(scoreCard, value);
		this.characteristic = characteristic;
	}

	public ScoreRow(String scoreCard, String characteristic) {
		super(scoreCard);
		this.characteristic = characteristic;
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
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((characteristic == null) ? 0 : characteristic.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((reasonCode == null) ? 0 : reasonCode.hashCode());
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
		ScoreRow other = (ScoreRow) obj;
		if (characteristic == null) {
			if (other.characteristic != null)
				return false;
		} else if (!characteristic.equals(other.characteristic))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (reasonCode == null) {
			if (other.reasonCode != null)
				return false;
		} else if (!reasonCode.equals(other.reasonCode))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder bldr = new StringBuilder("ScoreRow( ");
		bldr.append("scoreCard=").append(getScoreCard()).append(", ");
		bldr.append("value=").append(getValue()).append(", ");
		bldr.append("characteristic=").append(characteristic).append(", ");
		bldr.append("reasonCode=").append(reasonCode).append(", ");
		bldr.append("id=").append(id).append(" )");
		return bldr.toString();
	}
	
	
}
