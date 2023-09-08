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
package org.drools.impact.analysis.integrationtests.domain;

import java.util.HashMap;
import java.util.Map;

public class ControlFact {

    private String orderId;
    private String keyword;
    private Map<String, String> mapData = new HashMap<>();
    private Map<String, Integer> mapDataInt = new HashMap<>();

    public ControlFact() {}

    public ControlFact(String orderId) {
        this.orderId = orderId;
    }

    public ControlFact(String orderId, String keyword) {
        this.orderId = orderId;
        this.keyword = keyword;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Map<String, String> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, String> mapData) {
        this.mapData = mapData;
    }

    public Map<String, Integer> getMapDataInt() {
        return mapDataInt;
    }

    public void setMapDataInt(Map<String, Integer> mapDataInt) {
        this.mapDataInt = mapDataInt;
    }

}
