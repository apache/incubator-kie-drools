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
package org.drools.testcoverage.domain;

import java.io.Serializable;

/**
 * A customer in a bar.
 */
public class Customer implements Serializable {

    private final String name;

    private final int ageInYears;

    public Customer(final String name, final int ageInYears) {
        this.name = name;
        this.ageInYears = ageInYears;
    }

    public String getName() {
        return this.name;
    }

    public int getAgeInYears() {
        return this.ageInYears;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (ageInYears != customer.ageInYears) return false;
        return !(name != null ? !name.equals(customer.name) : customer.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + ageInYears;
        return result;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "name='" + name + '\'' +
                ", ageInYears=" + ageInYears +
                '}';
    }
}
