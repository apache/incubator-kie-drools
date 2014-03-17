package org.drools.impl.adapters;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class ResourceAdapter implements org.drools.io.Resource {

    private final Resource delegate;

    public ResourceAdapter(Resource delegate) {
        this.delegate = delegate;
    }

    public InputStream getInputStream() throws IOException {
        return delegate.getInputStream();
    }

    public Reader getReader() throws IOException {
        return delegate.getReader();
    }

    public String getSourcePath() {
        return delegate.getSourcePath();
    }

    public String getTargetPath() {
        return delegate.getTargetPath();
    }

    public ResourceType getResourceType() {
        return delegate.getResourceType();
    }

    public ResourceConfiguration getConfiguration() {
        return delegate.getConfiguration();
    }

    public Resource setSourcePath(String path) {
        return delegate.setSourcePath(path);
    }

    public Resource setTargetPath(String path) {
        return delegate.setTargetPath(path);
    }

    public Resource setResourceType(ResourceType type) {
        return delegate.setResourceType(type);
    }

    public Resource setConfiguration(ResourceConfiguration conf) {
        return delegate.setConfiguration(conf);
    }

    public Resource getDelegate() {
        return delegate;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourceAdapter && delegate.equals(((ResourceAdapter)obj).delegate);
    }
}
