/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.scorecards.example;

/**
 * Created with IntelliJ IDEA.
 * User: vinod kiran
 * Date: 14/10/12
 * Time: 11:00 AM
 */
public class Customer {
    double customerScore;
    int customerAge;
    String placeOfResidence;

    public Customer() {
    }

    public double getCustomerScore() {
        return customerScore;
    }

    public void setCustomerScore(double customerScore) {
        this.customerScore = customerScore;
    }

    public int getCustomerAge() {
        return customerAge;
    }

    public void setCustomerAge(int customerAge) {
        this.customerAge = customerAge;
    }

    public String getPlaceOfResidence() {
        return placeOfResidence;
    }

    public void setPlaceOfResidence(String placeOfResidence) {
        this.placeOfResidence = placeOfResidence;
    }
}
