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
/**
 * Cell containing an array of values
 */
package org.drools.template.parser;

import java.util.Map;

import org.drools.util.StringUtils;

public class ArrayCell extends AbstractCell<String> {

    private String[] values;

    public ArrayCell(final Row r, final ArrayColumn c) {
        super(r, c);
    }

    public void addValue(Map<String, Object> vars) {
        for (int i = 0; i < values.length; i++) {
            vars.put(column.getName() + i, values[i]);
        }
    }

    public void setIndex(int i) {
        throw new RuntimeException("You cannot call setQueueIndex on an ArrayCell");
    }

    public int getIndex() {
        return -1;
    }

    public void setValue(String v) {
        value = v;
        values = StringUtils.splitPreserveAllTokens(value, ",");
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }

}
