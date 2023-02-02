package org.kie.api.conf;

public class ConfigurationKey<T extends OptionsConfiguration> {
    private String type;

    public ConfigurationKey(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
