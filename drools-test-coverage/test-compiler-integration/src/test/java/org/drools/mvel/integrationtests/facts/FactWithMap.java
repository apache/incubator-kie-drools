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
package org.drools.mvel.integrationtests.facts;

import java.util.HashMap;
import java.util.Map;

public class FactWithMap {

    private Map<Integer, String> itemsMap = new HashMap<>();

    public FactWithMap() { }

    public FactWithMap(final Integer key, final String value) {
        this.itemsMap.put(key, value);
    }

    public FactWithMap(final Map<Integer, String> itemsMap) {
        this.itemsMap = itemsMap;
    }

    public Map<Integer, String> getItemsMap() {
        return itemsMap;
    }

    public void setItemsMap(Map<Integer, String> itemsMap) {
        this.itemsMap = itemsMap;
    }
}
