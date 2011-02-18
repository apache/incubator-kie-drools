package org.acme.insurance;

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
