package org.kie.memorycompiler;

import java.util.Map;

public interface StoreClassLoader {
    Map<String, byte[]> getStore();
}
