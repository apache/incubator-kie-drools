package org.jbpm.persistence.processinstance.objects;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class NonSerializableClass {
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String someString;
	@SuppressWarnings("unused")
    private Date creationDate;

	public NonSerializableClass() { 
	    creationDate = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSomeString() {
		return someString;
	}

	public void setString(String someString) {
		this.someString = someString;
	}

}
