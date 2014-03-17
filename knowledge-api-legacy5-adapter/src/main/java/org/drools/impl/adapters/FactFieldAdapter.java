package org.drools.impl.adapters;

import org.drools.definition.type.Annotation;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactField;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.drools.impl.adapters.AnnotationAdapter.adaptAnnotations;

public class FactFieldAdapter implements org.drools.definition.type.FactField {

    private final FactField delegate;

    public FactFieldAdapter(FactField delegate) {
        this.delegate = delegate;
    }

    public Class<?> getType() {
        return delegate.getType();
    }

    public String getName() {
        return delegate.getName();
    }

    public boolean isKey() {
        return delegate.isKey();
    }

    public void set(Object bean, Object value) {
        delegate.set(bean, value);
    }

    public Object get(Object bean) {
        return delegate.get(bean);
    }

    public int getIndex() {
        return delegate.getIndex();
    }

    public List<Annotation> getFieldAnnotations() {
        return adaptAnnotations(delegate.getFieldAnnotations());
    }

    public Map<String, Object> getMetaData() {
        return delegate.getMetaData();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        delegate.writeExternal(out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        delegate.readExternal(in);
    }

    public static List<org.drools.definition.type.FactField> adaptFactFields(Collection<org.kie.api.definition.type.FactField> factFields) {
        List<org.drools.definition.type.FactField> result = new ArrayList<org.drools.definition.type.FactField>();
        for (org.kie.api.definition.type.FactField factField : factFields) {
            result.add(new FactFieldAdapter(factField));
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FactFieldAdapter && delegate.equals(((FactFieldAdapter)obj).delegate);
    }
}
