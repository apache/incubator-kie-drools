package org.kie.api.conf;

public class OptionKey<T extends Option> {
    private String type;
    private String name;

    public OptionKey(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String type() {
        return type;
    }

    public String name() {
        return name;
    }
}
