package org.drools;

public class Foo implements Cloneable {
	
	private String id;
	private Bar bar;

    public Foo(String id, Bar bar) {
        super();
        this.id = id;
        this.bar = bar;
    }

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
		return new Foo( id, bar );
	}

	public String toString(){
		return "Foo: " + this.id + " (" + this.bar + ")";
	}
}
