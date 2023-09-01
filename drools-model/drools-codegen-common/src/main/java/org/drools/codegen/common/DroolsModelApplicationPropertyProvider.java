package org.drools.codegen.common;

import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

public interface DroolsModelApplicationPropertyProvider {

    static DroolsModelApplicationPropertyProvider of(Properties properties) {
        return new DroolsModelApplicationPropertyProvider() {
            @Override
            public Optional<String> getApplicationProperty(String property) {
                return Optional.ofNullable(properties.getProperty(property));
            }

            @Override
            public Collection<String> getApplicationProperties() {
                return properties.stringPropertyNames();
            }

            @Override
            public void setApplicationProperty(String key, String value) {
                properties.put(key, value);
            }
        };
    }

    Optional<String> getApplicationProperty(String property);

    Collection<String> getApplicationProperties();

    void setApplicationProperty(String key, String value);
}
