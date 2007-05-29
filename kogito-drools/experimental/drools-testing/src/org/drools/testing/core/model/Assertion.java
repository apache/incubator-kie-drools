package org.drools.testing.core.model;

/**
 * 
 * @author Matt
 *
 * (c) Matt Shaw
 */
public class Assertion {

	private String name;
	private String beanName;
	private String propertyName;
	private String propertyValue;
	private String expectedValue;
	private boolean success;
	
	public Assertion () {}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyValue() {
		return propertyValue;
	}

	public void setPropertyValue(String propertyValue) {
		this.propertyValue = propertyValue;
	}
}
