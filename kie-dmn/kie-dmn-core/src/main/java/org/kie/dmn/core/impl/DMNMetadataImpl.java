package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNMetadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> getAll() {
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public boolean isDefined(String name) {
        return entries.containsKey(name);
    }

}
