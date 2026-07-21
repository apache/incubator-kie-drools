/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.traffic.licensevalidation;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Driver {

    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("State")
    private String state;

    @JsonProperty("City")
    private String city;

    @JsonProperty("Points")
    private Integer points;

    @JsonProperty("Age")
    private Integer age;

    private Date licenseExpiration;

    @JsonProperty("ValidLicense")
    private Boolean validLicense = Boolean.FALSE;

    public Driver() {
    }

    public Driver(String id, String name, String state, String city, Integer points, Integer age, Date licenseExpiration) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.city = city;
        this.points = points;
        this.age = age;
        this.licenseExpiration = licenseExpiration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getLicenseExpiration() {
        return licenseExpiration;
    }

    public void setLicenseExpiration(Date licenseExpiration) {
        this.licenseExpiration = licenseExpiration;
    }

    public Boolean isValidLicense() {
        return validLicense;
    }

    public Boolean getValidLicense() {
        return validLicense;
    }

    public void setValidLicense(Boolean validLicense) {
        this.validLicense = validLicense;
    }
}
