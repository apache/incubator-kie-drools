package org.drools.scenariosimulation.backend.runner.model;

import java.util.List;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.ScenarioWithIndex;
import org.drools.scenariosimulation.api.model.ScesimModelDescriptor;
import org.drools.scenariosimulation.api.model.Settings;

public class ScenarioRunnerDTO {

    private final String fileName;
    private final Settings settings;
    private final Background background;
    private final List<ScenarioWithIndex> scenarioWithIndices;
    private final ScesimModelDescriptor simulationModelDescriptor;

    public ScenarioRunnerDTO(ScenarioSimulationModel model, String fileName) {
        this(model.getSimulation().getScesimModelDescriptor(), model.getSimulation().getScenarioWithIndex(), fileName, model.getSettings(), model.getBackground());
    }

    public ScenarioRunnerDTO(ScesimModelDescriptor simulationModelDescriptor, List<ScenarioWithIndex> scenarioWithIndices, String fileName, Settings settings, Background background) {
        this.simulationModelDescriptor = simulationModelDescriptor;
        this.scenarioWithIndices = scenarioWithIndices;
        this.fileName = fileName;
        this.settings = settings;
        this.background = background;
    }

    public String getFileName() {
        return fileName;
    }

    public Settings getSettings() {
        return settings;
    }

    public Background getBackground() {
        return background;
    }

    public List<ScenarioWithIndex> getScenarioWithIndices() {
        return scenarioWithIndices;
    }

    public ScesimModelDescriptor getSimulationModelDescriptor() {
        return simulationModelDescriptor;
    }
}
