package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNMetadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DMNMetadataImpl implements DMNMetadata {
    private Map<String, Object> attributes = new HashMap<>();

    public DMNMetadataImpl() {
    }

    public DMNMetadataImpl(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}
