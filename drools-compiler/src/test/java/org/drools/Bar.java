package org.drools;

public class Bar implements Cloneable {

	private String id;

    public Bar(String id) {
        super();
        this.id = id;
    }

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
