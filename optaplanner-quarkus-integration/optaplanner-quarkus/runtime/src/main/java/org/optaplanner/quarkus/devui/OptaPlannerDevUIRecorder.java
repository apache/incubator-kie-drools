package org.optaplanner.quarkus.devui;

import java.util.function.Supplier;

import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class OptaPlannerDevUIRecorder {

    public Supplier<SolverConfigText> solverConfigTextSupplier(final String solverConfigText) {
        return () -> {
            return new SolverConfigText(solverConfigText);
        };
    }

}
