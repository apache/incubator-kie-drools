package org.kie.pmml.api.enums;

import java.util.Arrays;

import org.kie.pmml.api.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4-1/Transformations.html#xsdElement_TextIndex>countHits</a>
 */
public enum COUNT_HITS implements Named {

    ALL_HITS("allHits"),
    BEST_HITS("bestHits");

    private final String name;

    COUNT_HITS(String name) {
        this.name = name;
    }

    public static COUNT_HITS byName(String name) {
        return Arrays.stream(COUNT_HITS.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find COUNT_HITS with name: " + name));
    }

    @Override
    public String getName() {
        return name;
    }
}
