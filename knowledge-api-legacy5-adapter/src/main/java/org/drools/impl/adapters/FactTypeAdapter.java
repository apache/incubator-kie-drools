/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.definition.type.Annotation;
import org.drools.definition.type.FactField;
import org.kie.api.definition.type.FactType;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.drools.impl.adapters.AnnotationAdapter.adaptAnnotations;
import static org.drools.impl.adapters.FactFieldAdapter.adaptFactFields;

public class FactTypeAdapter implements org.drools.definition.type.FactType {

    private final FactType delegate;

    public FactTypeAdapter(FactType delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getName();
    }

    public String getSimpleName() {
        return delegate.getSimpleName();
    }

    public String getPackageName() {
        return delegate.getPackageName();
    }

    public String getSuperClass() {
        return delegate.getSuperClass();
    }

    public List<FactField> getFields() {
        return adaptFactFields(delegate.getFields());
    }

    public FactField getField(String name) {
        return new FactFieldAdapter(delegate.getField(name));
    }

    public Class<?> getFactClass() {
        return delegate.getFactClass();
    }

    public Object newInstance() throws InstantiationException, IllegalAccessException {
        return delegate.newInstance();
    }

    public void set(Object bean, String field, Object value) {
        delegate.set(bean, field, value);
    }

    public Object get(Object bean, String field) {
        return delegate.get(bean, field);
    }

    public Map<String, Object> getAsMap(Object bean) {
        return delegate.getAsMap(bean);
    }

    public void setFromMap(Object bean, Map<String, Object> values) {
        delegate.setFromMap(bean, values);
    }

    public List<Annotation> getClassAnnotations() {
        return adaptAnnotations(delegate.getClassAnnotations());
    }

    public Map<String, Object> getMetaData() {
        return delegate.getMetaData();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        delegate.writeExternal(out);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        delegate.readExternal(in);
    }

    public static List<org.drools.definition.type.FactType> adaptFactTypes(Collection<org.kie.api.definition.type.FactType> factTypes) {
        List<org.drools.definition.type.FactType> result = new ArrayList<org.drools.definition.type.FactType>();
        for (org.kie.api.definition.type.FactType factType : factTypes) {
            result.add(new FactTypeAdapter(factType));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FactTypeAdapter && delegate.equals(((FactTypeAdapter)obj).delegate);
    }
}
