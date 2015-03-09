package org.jbpm.examples.request;

import java.io.Serializable;

public class Person implements Serializable {
	
	private static final long serialVersionUID = 6L;
	
	private String id;
	private String name;
	private int age;
	
	public Person(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
