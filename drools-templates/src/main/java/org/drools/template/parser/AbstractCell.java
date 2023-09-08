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
package org.drools.template.parser;

import java.util.Map;

public abstract class AbstractCell<T> implements Cell<T> {

    protected final Row row;
    protected final Column column;
    protected int index;

    protected T value;

    protected AbstractCell(Row r, Column c) {
        row = r;
        column = c;
    }

    public String toString() {
        return "Cell[" + column + ": " + value + "]";
    }

    public Row getRow() {
        return row;
    }

    public Column getColumn() {
        return column;
    }

    public T getValue() {
        return value;
    }

    public void addValue(Map<String, Object> vars) {
        vars.put(column.getName(), value);
    }

    public void setIndex(int i) {
        index = i;
    }

    public int getIndex() {
        return index;
    }

    public boolean isEmpty() {
        return value == null;
    }
}
