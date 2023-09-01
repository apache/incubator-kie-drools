package org.kie.internal.simulation;

import java.util.List;

public interface SimulationPath {

    String getName();

    List<SimulationStep> getSteps();

}
