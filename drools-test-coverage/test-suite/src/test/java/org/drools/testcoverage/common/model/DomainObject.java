/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.common.model;

public class DomainObject {

    private String message;
    private int value;
    private double value2;
    private long id;
    private Interval interval;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public double getValue2() {
        return value2;
    }

    public void setValue2(final double value2) {
        this.value2 = value2;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(final long id ) {
        this.id = id;
    }

    /**
     * @return the interval
     */
    public Interval getInterval() {
        return interval;
    }

    /**
     * @param interval the interval to set
     */
    public void setInterval(final Interval interval ) {
        this.interval = interval;
    }

    
}
