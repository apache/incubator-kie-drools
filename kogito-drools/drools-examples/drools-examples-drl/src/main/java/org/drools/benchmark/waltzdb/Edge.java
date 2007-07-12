package org.drools.benchmark.waltzdb;


//(literalize edge type p1 p2 joined)
public class Edge {
	private String type;
	private int p1;
	private int p2;
	private boolean joined;
	public Edge() {
		super();
	}
	public Edge(String type, int p1, int p2, boolean joined) {
		super();
		this.type = type;
		this.p1 = p1;
		this.p2 = p2;
		this.joined = joined;
	}
	public Edge(int p1, int p2, boolean joined) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.joined = joined;
	}
	public Edge(int p1, int p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (joined ? 1231 : 1237);
		result = PRIME * result + p1;
		result = PRIME * result + p2;
		result = PRIME * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Edge other = (Edge) obj;
		if (joined != other.joined)
			return false;
		if (p1 != other.p1)
			return false;
		if (p2 != other.p2)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	public boolean isJoined() {
		return joined;
	}
	public void setJoined(boolean joined) {
		this.joined = joined;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
