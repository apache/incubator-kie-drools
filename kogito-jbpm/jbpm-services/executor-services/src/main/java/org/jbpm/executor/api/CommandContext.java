package org.jbpm.executor.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommandContext implements Serializable {

    private static final long serialVersionUID = -1440017934399413860L;
    private Map<String, Object> data;

    public CommandContext() {
        data  = new HashMap<String, Object>();
    }

    public CommandContext(Map<String, Object> data) {
        this.data = data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public void setData(String key, Object value) {
        data.put(key, value);
    }

    public Set<String> keySet() {
        return data.keySet();
    }

    @Override
    public String toString() {
        return "CommandContext{" + "data=" + data + '}';
    }
}
