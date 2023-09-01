package org.kie.api.fluent;

import java.util.HashMap;
import java.util.Map;

/** 
 * Builder pattern like class used to build a variable.<br>
 * A variable requires a name and a data type.<br> 
 * Value and metadata are optional.<br> 
 * Usage:
 * <pre>
 *  Variable.var("test",String.class)
 *          .value("example value")
 *          .metadata("readOnly",true).
 *          .metadata("required",false)
 * </pre>
 *
 * @param <T> data type of the variable
 * @see NodeContainerBuilder#variable(Variable)
 */
public class Variable<T> {
    
    private String name;
    private T value;
    private Class<T> type;
    private Map<String, Object> metadata;

    private Variable(String name, Class<T> type) {
        this.name = name;
        this.type = type;
    }
    
    public static <T> Variable<T> var(String name, Class<T> type) {
        return new Variable<>(name, type);
    }

    public Variable<T> value(T value) {
        this.value = value;
        return this;
    }

    public Variable<T> metadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public Class<T> getType() {
        return type;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "Variable [name=" + name + ", value=" + value + ", type=" + type + ", metadata=" + metadata + "]";
    }
}
