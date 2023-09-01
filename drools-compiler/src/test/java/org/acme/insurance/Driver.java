package org.acme.insurance;

/**
 * This represents obviously a driver who is applying for an insurance Policy.
 */
public class Driver {

    private String  name                = "Mr Joe Blogs";
    private Integer age                 = new Integer( 30 );
    private Integer priorClaims         = new Integer( 0 );
    private String  locationRiskProfile = "LOW";

    public Integer getAge() {
        return this.age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public String getLocationRiskProfile() {
        return this.locationRiskProfile;
    }

    public void setLocationRiskProfile(final String locationRiskProfile) {
        this.locationRiskProfile = locationRiskProfile;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getPriorClaims() {
        return this.priorClaims;
    }

    public void setPriorClaims(final Integer priorClaims) {
        this.priorClaims = priorClaims;
    }

}
