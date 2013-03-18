package org.kie.api.builder;

import org.kie.api.io.Resource;

/**
 * KieBuilder is a builder for the resources contained in a KieModule
 */
public interface KieBuilder {

    /**
     * Sets the other KieModules from which the KieModule that has to be built by this KieBuilder depends on
     */
    KieBuilder setDependencies(KieModule... dependencies);

    /**
     * Sets the other Resources from which the KieModule that has to be built by this KieBuilder depends on
     */
    KieBuilder setDependencies(Resource... dependencies);

    /**
     * Builds all the KieBases contained in the KieModule for which this KieBuilder has been created
     */
    KieBuilder buildAll();

    /**
     * Returns the Results of the building process.
     * Invokes <code>buildAll()</code> if the KieModule hasn't been built yet
     */
    Results getResults();

    /**
     * Returns the KieModule for which this KieBuilder has been created
     */
    KieModule getKieModule();
}
