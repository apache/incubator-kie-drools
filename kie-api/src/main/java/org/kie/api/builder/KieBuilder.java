package org.kie.api.builder;

import org.kie.api.io.Resource;

/**
 * KieBuilder is a builder for the resources contained in a KieModule
 */
public interface KieBuilder {

    /**
     * A marker interace implemented by the different type of projects supported by this KieBuilder
     */
    interface ProjectType { }

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
     * Builds all the KieBases contained in the KieModule for which this KieBuilder has been created
     * creating a project of the type specified by the provided projectClass
     */
    KieBuilder buildAll( Class<? extends ProjectType> projectClass );

    /**
     * Returns the Results of the building process.
     * Invokes <code>buildAll()</code> if the KieModule hasn't been built yet
     */
    Results getResults();

    /**
     * Returns the KieModule for which this KieBuilder has been created
     */
    KieModule getKieModule();

    /**
     * Returns the KieModule for which this KieBuilder has been created of the specific projectClass
     */
    KieModule getKieModule( Class<? extends ProjectType> projectClass );
}
