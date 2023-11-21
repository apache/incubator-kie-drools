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
package org.drools.base.facttemplates;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//import woolfel.engine.rule.Rule;

public class FactImpl implements Fact, Externalizable {

    private FactTemplate factTemplate = null;
    private Map<String, Object> values = new HashMap<>();

    public FactImpl() {
    }

    public FactImpl(final FactTemplate template) {
        this.factTemplate = template;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        factTemplate    = (FactTemplate)in.readObject();
        values          = (Map<String, Object>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(factTemplate);
        out.writeObject(values);
    }

    public Object get(final String name) {
        return this.values.get( name );
    }

    public void set(final String name, final Object value) {
        this.values.put( name, value );
    }

    /**
     * Return the deftemplate for the fact
     */
    public FactTemplate getFactTemplate() {
        return this.factTemplate;
    }

    @Override
    public Map<String, Object> asMap() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FactImpl fact = (FactImpl) o;
        return factTemplate.equals(fact.factTemplate) && values.equals(fact.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(factTemplate, values);
    }
}
