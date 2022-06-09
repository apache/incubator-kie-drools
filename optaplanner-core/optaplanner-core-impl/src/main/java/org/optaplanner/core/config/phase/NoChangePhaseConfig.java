package org.optaplanner.core.config.phase;

import java.util.function.Consumer;

public class NoChangePhaseConfig extends PhaseConfig<NoChangePhaseConfig> {

    public static final String XML_ELEMENT_NAME = "noChangePhase";

    @Override
    public NoChangePhaseConfig inherit(NoChangePhaseConfig inheritedConfig) {
        super.inherit(inheritedConfig);
        return this;
    }

    @Override
    public NoChangePhaseConfig copyConfig() {
        return new NoChangePhaseConfig().inherit(this);
    }

    @Override
    public void visitReferencedClasses(Consumer<Class<?>> classVisitor) {
        if (getTerminationConfig() != null) {
            getTerminationConfig().visitReferencedClasses(classVisitor);
        }
    }

}
