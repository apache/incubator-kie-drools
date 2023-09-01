package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_TextIndex>localTermWeights</a>
 */
public enum LOCAL_TERM_WEIGHTS implements Named {

    TERM_FREQUENCY("termFrequency"),
    BINARY("binary"),
    LOGARITHMIC("logarithmic"),
    AUGMENTED_NORMALIZED_TERM_FREQUENCY("augmentedNormalizedTermFrequency");

    private final String name;

    LOCAL_TERM_WEIGHTS(String name) {
        this.name = name;
    }

    public static LOCAL_TERM_WEIGHTS byName(String name) {
        return Arrays.stream(LOCAL_TERM_WEIGHTS.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find LOCAL_TERM_WEIGHTS with name: " + name));
    }

    @Override
    public String getName() {
        return name;
    }
}
