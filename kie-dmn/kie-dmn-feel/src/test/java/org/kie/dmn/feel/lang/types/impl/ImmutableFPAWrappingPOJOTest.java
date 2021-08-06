/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.types.impl;

import org.junit.Test;

public class ImmutableFPAWrappingPOJOTest {

    @Test
    public void testSupportRequest() {
        ImmutableFPAWrappingPOJO fpa = new ImmutableFPAWrappingPOJO(new SupportRequest("John Doe", "47", "info@redhat.com", "+1", "somewhere", false));
        System.out.println(fpa.allFEELProperties());
    }

    public static class SupportRequest implements java.io.Serializable {

        static final long serialVersionUID = 1L;

        private String fullName;
        private String account;
        private String email;
        private String mobile;
        private String mailingAddress;
        private boolean premium;
        /**
         * This is deliberately not part of the constructor
         */
        private String priority;

        public SupportRequest() {}

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

        public void setPremium(boolean premium) {
            this.premium = premium;
        }

        public SupportRequest(String fullName, String account,
                              String email, String mobile,
                              String mailingAddress, boolean premium) {
            this.fullName = fullName;
            this.account = account;
            this.email = email;
            this.mobile = mobile;
            this.mailingAddress = mailingAddress;
            this.premium = premium;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        @Override
        public String toString() {
            return "SupportRequest [account=" + account + ", email=" + email + ", fullName=" + fullName + ", mailingAddress=" + mailingAddress + ", mobile=" + mobile + ", premium=" + premium + ", priority=" + priority +
                   "]";
        }
    }
}

