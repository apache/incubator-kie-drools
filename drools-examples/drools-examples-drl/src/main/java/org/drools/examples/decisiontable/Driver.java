/*
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

package org.drools.examples.decisiontable;

/**
 * This represents obviously a driver who is applying for an insurance Policy.
 * @author Michael Neale
 *
 */
public class Driver {

	private String name = "Mr Joe Blogs";
	private Integer age = new Integer(30);
	private Integer priorClaims = new Integer(0);	
	private String  locationRiskProfile = "LOW";
	
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getLocationRiskProfile() {
		return locationRiskProfile;
	}
	public void setLocationRiskProfile(String locationRiskProfile) {
		this.locationRiskProfile = locationRiskProfile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPriorClaims() {
		return priorClaims;
	}
	public void setPriorClaims(Integer priorClaims) {
		this.priorClaims = priorClaims;
	}
	
	
}
