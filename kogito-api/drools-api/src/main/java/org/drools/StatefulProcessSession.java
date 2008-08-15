package org.drools;

import java.util.Map;

public interface StatefulProcessSession {
    void startProcess(String processId);
    void startProcess(String processId, Map<String, Object> parameters);
}
