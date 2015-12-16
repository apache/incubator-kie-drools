/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 * Cell containing an array of values
 */
package org.drools.template.parser;

import org.drools.core.util.StringUtils;
import org.kie.api.runtime.KieSession;

import java.util.Map;

public class ArrayCell implements Cell {
    Row row;

    String value;

    ArrayColumn column;

    private String[] values;

    public ArrayCell(final Row r, final ArrayColumn c) {
        row = r;
        column = c;
    }

    public void addValue(Map<String, Object> vars) {
        for (int i = 0; i < values.length; i++) {
            vars.put(column.getName() + i, values[i]);
        }
    }

    public Column getColumn() {
        return column;
    }

    public Row getRow() {
        return row;
    }

    public String getValue() {
        return value;
    }

    public void insert(KieSession session) {
        session.insert(this);
        for (int i = 0; i < values.length; i++) {
            Cell cell = column.getType().createCell(row);
            cell.setValue(values[i]);
            cell.setIndex(i);
            cell.insert(session);
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
