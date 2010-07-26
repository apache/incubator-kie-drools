/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
