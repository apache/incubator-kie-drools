package org.drools.benchmark.waltzdb;
//(literalize illegal bp l_id)
public class Illegal {
	private int basePoint;
	private String labelId;
	public Illegal() {
		super();
	}
	public Illegal(int basePoint, String labelId) {
		super();
		this.basePoint = basePoint;
		this.labelId = labelId;
	}
	
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + basePoint;
		result = PRIME * result + ((labelId == null) ? 0 : labelId.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Illegal other = (Illegal) obj;
		if (basePoint != other.basePoint)
			return false;
		if (labelId == null) {
			if (other.labelId != null)
				return false;
		} else if (!labelId.equals(other.labelId))
			return false;
		return true;
	}
	public int getBasePoint() {
		return basePoint;
	}
	public void setBasePoint(int basePoint) {
		this.basePoint = basePoint;
	}
	public String getLabelId() {
		return labelId;
	}
	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}
}
