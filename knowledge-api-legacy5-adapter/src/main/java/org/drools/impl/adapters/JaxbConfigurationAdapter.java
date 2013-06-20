package org.drools.impl.adapters;

import com.sun.tools.xjc.Options;
import org.kie.internal.builder.JaxbConfiguration;

import java.util.List;

public class JaxbConfigurationAdapter implements org.drools.builder.JaxbConfiguration {

    private final JaxbConfiguration delegate;

    public JaxbConfigurationAdapter(JaxbConfiguration delegate) {
        this.delegate = delegate;
    }

    public Options getXjcOpts() {
        return delegate.getXjcOpts();
    }

    public String getSystemId() {
        return delegate.getSystemId();
    }

    public List<String> getClasses() {
        return delegate.getClasses();
    }
}
