/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.workflow.core.node;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.workflow.core.impl.DataDefinition;

public class Assignment implements Serializable {

    private static final long serialVersionUID = 5L;

    private String dialect;
    private DataDefinition from; // this is an expression
    private DataDefinition to; // this is another expression
    private Map<String, Object> metaData = new HashMap<String, Object>();

    public Assignment(String dialect, DataDefinition from, DataDefinition to) {
        this.dialect = dialect;
        this.from = from;
        this.to = to;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public DataDefinition getFrom() {
        return from;
    }

    public void setFrom(DataDefinition from) {
        this.from = from;
    }

    public DataDefinition getTo() {
        return to;
    }

    public void setTo(DataDefinition to) {
        this.to = to;
    }

    public void setMetaData(String name, Object value) {
        this.metaData.put(name, value);
    }

    public Object getMetaData(String name) {
        return this.metaData.get(name);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "dialect='" + dialect + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", metaData=" + metaData +
                '}';
    }
}
