package org.kie.pmml.api.enums;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * Indicates the possible end-result of a model evaluation
 */
public enum ResultCode implements Named {
    OK("OK"),
    FAIL("FAIL");

    private final String name;

    ResultCode(String name) {
        this.name = name;
    }

    public static ResultCode byName(String name) {
        return Arrays.stream(ResultCode.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find ResultCode with name: " + name));
    }

    public String getName() {
        return name;
    }
}
