package org.drools;

public class Foo implements Cloneable {
	
	private static final long serialVersionUID = -4951171908181131979L;

	private String id;
	private Bar bar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public Bar getBar() {
		return bar;
	}

	public void setBar(Bar bar) {
		this.bar = bar;
	}

	public Foo clone() {
		Foo clone = new Foo();
		clone.id = id;
		clone.bar = bar;
		return clone;
	}

	public String toString(){
		return "Foo: " + this.id + " (" + this.bar + ")";
	}
}
