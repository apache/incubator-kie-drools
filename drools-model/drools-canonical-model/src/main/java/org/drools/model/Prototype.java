package org.drools.model;

import java.util.Collection;
import java.util.function.Function;

public interface Prototype extends NamedModelItem {

    Object UNDEFINED_VALUE = UndefinedValue.INSTANCE;

    Collection<String> getFieldNames();

    Field getField(String name);

    int getFieldIndex(final String name);

    default Function<PrototypeFact, Object> getFieldValueExtractor(String name) {
        Field field = getField(name);
        return field != null ? field.getExtractor() : p -> p.has(name) ? p.get(name) : UNDEFINED_VALUE;
    }

    boolean isEvent();

    Prototype setAsEvent(boolean event);

    interface Field {
        String getName();

        Function<PrototypeFact, Object> getExtractor();

        boolean isTyped();

        Class<?> getType();
    }

    class UndefinedValue {
        static final UndefinedValue INSTANCE = new UndefinedValue();

        static final UnsupportedOperationException HASHCODE_EXCEPTION = new UnsupportedOperationException();

        @Override
        public int hashCode() {
            // throw an Exception to avoid indexing of an undefined value
            throw HASHCODE_EXCEPTION;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }

        @Override
        public String toString() {
            return "$UndefinedValue$";
        }
    }
}
