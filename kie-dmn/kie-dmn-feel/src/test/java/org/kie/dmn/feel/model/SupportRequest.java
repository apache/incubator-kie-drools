/**
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
package org.kie.dmn.feel.model;

public class SupportRequest implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    private String fullName;
    private String account;
    private String email;
    private String mobile;
    private String mailingAddress;
    private boolean premium;
    private String area;
    private String description;
    /**
     * This `priority` is deliberately not part of the arguments constructor
     */
    private String priority;

    public SupportRequest() {
    }

    @org.kie.dmn.feel.lang.FEELProperty("full name")
    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @org.kie.dmn.feel.lang.FEELProperty("mailing address")
    public String getMailingAddress() {
        return this.mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public boolean isPremium() {
        return this.premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SupportRequest(String fullName, String account,
            String email, String mobile,
            String mailingAddress, String area, String description, Boolean premium) {
        this.fullName = fullName;
        this.account = account;
        this.email = email;
        this.mobile = mobile;
        this.mailingAddress = mailingAddress;
        this.premium = premium;
        this.area = area;
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "SupportRequest [account=" + account + ", area=" + area + ", description=" + description + ", email="
                + email + ", fullName=" + fullName + ", mailingAddress=" + mailingAddress + ", mobile=" + mobile
                + ", premium=" + premium + ", priority=" + priority + "]";
    }

}