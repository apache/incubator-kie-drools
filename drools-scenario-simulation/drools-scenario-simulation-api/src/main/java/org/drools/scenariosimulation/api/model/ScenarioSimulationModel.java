package org.drools.scenariosimulation.api.model;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.drools.scenariosimulation.api.model.imports.HasImports;
import org.drools.scenariosimulation.api.model.imports.Imports;

public class ScenarioSimulationModel
        implements HasImports {

    public enum Type {
        RULE,
        DMN
    }

    @XStreamAsAttribute()
    private String version = "1.8";

    private Simulation simulation;

    private Background background;

    private Settings settings;

    private Imports imports = new Imports();

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    @Override
    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports(Imports imports) {
        this.imports = imports;
    }

    public String getVersion() {
        return version;
    }
}
