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
package org.drools.model.codegen.execmodel.domain;

import java.util.ArrayList;
import java.util.List;

public class ToysStore {

    private String cityName;
    private String storeName;

    private List<Toy> firstFloorToys = new ArrayList<>();
    private List<Toy> secondFloorToys = new ArrayList<>();

    public ToysStore(String cityName, String storeName) {
        this.cityName = cityName;
        this.storeName = storeName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public List<Toy> getFirstFloorToys() {
        return firstFloorToys;
    }

    public void setFirstFloorToys(List<Toy> firstFloorToys) {
        this.firstFloorToys = firstFloorToys;
    }

    public List<Toy> getSecondFloorToys() {
        return secondFloorToys;
    }

    public void setSecondFloorToys(List<Toy> secondFloorToys) {
        this.secondFloorToys = secondFloorToys;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Override
    public String toString() {
        return "ToysStore{" +
                "cityName='" + cityName + '\'' +
                ", storeName='" + storeName + '\'' +
                ", firstFloorToys=" + firstFloorToys +
                ", secondFloorToys=" + secondFloorToys +
                '}';
    }
}
