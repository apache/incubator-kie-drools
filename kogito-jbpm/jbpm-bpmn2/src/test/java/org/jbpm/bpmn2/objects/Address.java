package org.jbpm.bpmn2.objects;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Address {

	private String street;

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
}
