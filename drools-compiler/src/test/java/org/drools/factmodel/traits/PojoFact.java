package org.drools.factmodel.traits;

public class PojoFact {

	private int id;

	private boolean flag;

	public PojoFact() {
		super();
	}

	public PojoFact(int id, boolean flag) {
		super();
		this.id = id;
		this.flag = flag;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String toString() {
		return "PojoFact [id=" + id + ", flag=" + flag + "]";
	}

    public boolean equals(Object o) {
        if (this == o) return true;
        if ( ! ( o instanceof PojoFact ) ) return false;
        PojoFact pojoFact = (PojoFact) o;
        if ( getId() != pojoFact.getId() ) return false;
        return true;
    }

    public int hashCode() {
        return id;
    }
}