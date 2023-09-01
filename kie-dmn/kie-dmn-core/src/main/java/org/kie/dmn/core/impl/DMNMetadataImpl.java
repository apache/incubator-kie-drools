package org.kie.dmn.core.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNMetadata;

public class DMNMetadataImpl implements DMNMetadata {
    private Map<String, Object> entries = new HashMap<>();

    public DMNMetadataImpl() {
    }

    public DMNMetadataImpl(Map<String, Object> entries) {
        this.entries.putAll(entries);
    }

    @Override
    public Object set(String name, Object value) {
        return entries.put(name, value);
    }

    @Override
    public Object get(String name) {
        return entries.get(name);
    }

    @Override
    public Map<String, Object> asMap() {
        return Collections.unmodifiableMap(entries);
    }

}
