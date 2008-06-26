package org.drools.examples.process.order;

import java.util.Calendar;
import java.util.Date;

public class Customer {
	
	private String customerId;
	private String firstName;
	private String lastName;
	private Date birthday;
	
	public Customer(String customerId) {
		this.customerId = customerId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public int getAge() {
		if (birthday == null) {
			return -1;
		}
		Calendar today = Calendar.getInstance();
		Calendar dateOfBirth = Calendar.getInstance();
		dateOfBirth.setTime(birthday);
	    int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
	    dateOfBirth.add(Calendar.YEAR, age);
	    if (today.before(dateOfBirth)) {
	        age--;
	    }
	    return age;
	}

}
