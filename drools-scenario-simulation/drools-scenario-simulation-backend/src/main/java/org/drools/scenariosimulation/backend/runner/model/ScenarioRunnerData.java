package org.drools.scenariosimulation.backend.runner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Class to group all runner data: given data, expected data and results
 */
public class ScenarioRunnerData {

    private final List<InstanceGiven> backgrounds = new ArrayList<>();
    private final List<InstanceGiven> givens = new ArrayList<>();
    private final List<ScenarioExpect> expects = new ArrayList<>();
    private final List<ScenarioResult> results = new ArrayList<>();
    private ScenarioResultMetadata metadata;

    public void addBackground(InstanceGiven backgroundInstance) {
        backgrounds.add(backgroundInstance);
    }

    public void addGiven(InstanceGiven input) {
        givens.add(input);
    }

    public void addExpect(ScenarioExpect output) {
        expects.add(output);
    }

    public void addResult(ScenarioResult result) {
        results.add(result);
    }

    public void setMetadata(ScenarioResultMetadata metadata) {
        this.metadata = metadata;
    }

    public List<InstanceGiven> getBackgrounds() {
        return Collections.unmodifiableList(backgrounds);
    }

    public List<InstanceGiven> getGivens() {
        return Collections.unmodifiableList(givens);
    }

    public List<ScenarioExpect> getExpects() {
        return Collections.unmodifiableList(expects);
    }

    public List<ScenarioResult> getResults() {
        return Collections.unmodifiableList(results);
    }

    public Optional<ScenarioResultMetadata> getMetadata() {
        return Optional.ofNullable(metadata);
    }
}
