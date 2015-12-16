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

package org.drools.template.parser;

import org.drools.core.util.StringUtils;

/**
 * A column in a decision table that represents an array (comma-delimited)
 * of values.
 */
public class ArrayColumn extends AbstractColumn {

    private Column type;

    public ArrayColumn(String n, Column typeColumn) {
        super(n);
        this.type = typeColumn;
    }

    public Cell createCell(Row row) {
        return new ArrayCell(row, this);
    }

    public String getCellType() {
        return type.getCellType();
    }

    public Column getType() {
        return type;
    }

    public String getCondition(String condition, int index) {
        if (index == -1) {
            StringBuffer conditionString = new StringBuffer("ArrayCell(row == r, column == $param");
            if (!StringUtils.isEmpty(condition)) {
                conditionString.append(", value ").append(condition);
            }
            conditionString.append(")");
            return conditionString.toString();
        } else {
            return type.getCondition(condition, index);
        }

    }

}
