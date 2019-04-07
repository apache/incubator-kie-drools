/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.query;

import java.io.Serializable;

import org.dashbuilder.dataset.group.AggregateFunctionType;

public class AggregateColumnFilter implements Serializable {

    private static final long serialVersionUID = -3715417647758217121L;

    private AggregateFunctionType type;
    private String columnId;
    private String newColumnId;
    
    public AggregateColumnFilter(AggregateFunctionType type, String columnId, String newColumnId) {
        this.type = type;
        this.columnId = columnId;
        this.newColumnId = newColumnId;
    }

    public AggregateFunctionType getType() {
        return type;
    }
    
    public void setType(AggregateFunctionType type) {
        this.type = type;
    }
    
    public String getColumnId() {
        return columnId;
    }
    
    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    
    public String getNewColumnId() {
        return newColumnId;
    }

    
    public void setNewColumnId(String newColumnId) {
        this.newColumnId = newColumnId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnId == null) ? 0 : columnId.hashCode());
        result = prime * result + ((newColumnId == null) ? 0 : newColumnId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AggregateColumnFilter other = (AggregateColumnFilter) obj;
        if (columnId == null) {
            if (other.columnId != null)
                return false;
        } else if (!columnId.equals(other.columnId))
            return false;
        if (newColumnId == null) {
            if (other.newColumnId != null)
                return false;
        } else if (!newColumnId.equals(other.newColumnId))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
