package org.drools.impact.analysis.model.left;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class LeftHandSide {
    private final List<Pattern> patterns = new ArrayList<>();

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    @Override
    public String toString() {
        return "LeftHandSide{" +
                "patterns=" + patterns.stream().map( Object::toString ).collect( joining("\n", "\n", "") ) +
                '}';
    }
}
