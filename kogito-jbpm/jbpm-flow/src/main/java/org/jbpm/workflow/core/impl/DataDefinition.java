/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.impl;

import java.io.Serializable;
import java.util.UUID;

public class DataDefinition implements Serializable {

    private static final long serialVersionUID = -1819075545956349183L;

    private String id;
    private String label;
    private String type;
    private String expression;

    public DataDefinition(String expression) {
        this.id = UUID.randomUUID().toString();
        this.label = "EXPRESSION - (" + expression + ")";
        this.type = "java.lang.Object";
        this.expression = expression;
    }

    public DataDefinition(String id, String label, String type, String expression) {
        this.id = id;
        this.label = label != null && !label.isEmpty() ? label : id;
        this.type = type != null ? type : "java.lang.Object";
        this.expression = expression;
    }

    public DataDefinition(String id, String label, String type) {
        this(id, label, type, null);
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        String expr = hasExpression() ? ", expression=" + expression : "";
        return "DataSpec [id=" + id + ", label=" + label + ", type=" + type + expr + "]";
    }

    public static DataDefinition toExpression(String expression) {
        return new DataDefinition(expression);
    }

    public static DataDefinition toSimpleDefinition(String id) {
        if (id.contains("#{")) {
            return new DataDefinition(id);
        } else {
            return new DataDefinition(id, id, "java.lang.Object");
        }
    }

    public boolean hasExpression() {
        return expression != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expression == null) ? 0 : expression.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        DataDefinition other = (DataDefinition) obj;
        if (expression == null) {
            if (other.expression != null)
                return false;
        } else if (!expression.equals(other.expression))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
