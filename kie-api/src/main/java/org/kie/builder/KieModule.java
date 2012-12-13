package org.kie.builder;

/**
 * A KieModule is a container of all the resources necessary to define a set of KieBases like
 * a pom.xml defining its ReleaseId, a kmodule.xml file declaring the KieBases names and configurations
 * together with all the KieSession that can be created from them and all the other files
 * necessary to build the KieBases themselves
 */
public interface KieModule {

    /**
     * Returns the ReleaseId identifying this KieModule
     */
    ReleaseId getReleaseId();
}
