package org.drools.benchmark.waltzdb;
//(literalize edge_label p1 p2 l_name l_id)
public class EdgeLabel {
	private int p1;
	private int p2;
	private String labelName;
	private String labelId;
	public EdgeLabel() {
		super();
	}
	public EdgeLabel(int p1, int p2, String labelName, String labelId) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.labelName = labelName;
		this.labelId = labelId;
	}
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((labelId == null) ? 0 : labelId.hashCode());
		result = PRIME * result + ((labelName == null) ? 0 : labelName.hashCode());
		result = PRIME * result + p1;
		result = PRIME * result + p2;
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
		final EdgeLabel other = (EdgeLabel) obj;
		if (labelId == null) {
			if (other.labelId != null)
				return false;
		} else if (!labelId.equals(other.labelId))
			return false;
		if (labelName == null) {
			if (other.labelName != null)
				return false;
		} else if (!labelName.equals(other.labelName))
			return false;
		if (p1 != other.p1)
			return false;
		if (p2 != other.p2)
			return false;
		return true;
	}
	public String getLabelId() {
		return labelId;
	}
	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}
	public String getLabelName() {
		return labelName;
	}
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	public int getP1() {
		return p1;
	}
	public void setP1(int p1) {
		this.p1 = p1;
	}
	public int getP2() {
		return p2;
	}
	public void setP2(int p2) {
		this.p2 = p2;
	}
}
