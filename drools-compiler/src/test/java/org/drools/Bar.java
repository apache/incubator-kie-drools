package org.drools;

public class Bar implements Cloneable {

	private static final long serialVersionUID = -412418281393275010L;

	private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
	
	public String toString(){
		return "Bar: " + this.id;
	}
}
