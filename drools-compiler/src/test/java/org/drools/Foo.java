package org.drools;

public class Foo implements Cloneable {
	
	private String id;
	private Bar bar;
	private Interval interval;

    public Foo(String id, Bar bar) {
        super();
        this.id = id;
        this.bar = bar;
    }

    public Foo(String id, Bar bar, Interval interval) {
        super();
        this.id = id;
        this.bar = bar;
        this.interval = interval;
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

	public Interval getInterval() {
		return interval;
	}
	
	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	public Foo clone() {
		return new Foo( id, bar );
	}

	public String toString(){
		return "Foo(id:" + this.id + ", bar:" + this.bar + ", int:" + interval + ")";
	}
}
