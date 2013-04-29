package org.jbpm.executor.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ExecutionResults implements Serializable {

    private static final long serialVersionUID = -1738336024526084091L;
    private Map<String, Object> data = new HashMap<String, Object>();

    public ExecutionResults() {
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
        return "ExecutionResults{" + "data=" + data + '}';
    }
    
    
}
