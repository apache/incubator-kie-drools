/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
