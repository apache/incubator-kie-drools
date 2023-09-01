package org.kie.pmml.api.utils;

import java.util.Arrays;
import java.util.Objects;

import org.kie.pmml.api.enums.Named;
import org.kie.pmml.api.exceptions.KieEnumException;

public class EnumUtils {

    public static <K extends Enum<K> & Named> K enumByName(Class<K> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(value -> Objects.equals(name, value.getName()))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find " + enumClass.getSimpleName() + " enum constant with name: " + name));
    }

    private EnumUtils() {
        // Avoid instantiation
    }

}
