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
package org.kie.kogito.traffic;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Violation {

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Date")
    private Date date;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Speed Limit")
    private BigDecimal speedLimit;

    @JsonProperty("Actual Speed")
    private BigDecimal actualSpeed;

    public Violation() {
    }

    public Violation(String type, BigDecimal speedLimit, BigDecimal actualSpeed) {
        this.type = type;
        this.speedLimit = speedLimit;
        this.actualSpeed = actualSpeed;
        this.date = new Date();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(BigDecimal speedLimit) {
        this.speedLimit = speedLimit;
    }

    public BigDecimal getActualSpeed() {
        return actualSpeed;
    }

    public void setActualSpeed(BigDecimal actualSpeed) {
        this.actualSpeed = actualSpeed;
    }
}
