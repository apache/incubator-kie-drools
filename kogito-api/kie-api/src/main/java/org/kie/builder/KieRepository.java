package org.kie.builder;

import org.kie.io.Resource;

/**
 * KieRepository is a singleton acting as a repository for all the available KieModules
 * regardless if they are stored in the maven repository or programmatically built by the user
 */
public interface KieRepository {

    /**
     * Returns the defualt ReleaseId used to identify a KieModule in this KieRepository
     * if the user didn't explicitly provide one
     * @return The default ReleaseId
     */
    ReleaseId getDefaultReleaseId();

    /**
     * Adds a new KieModule to this KieRepository
     */
    void addKieModule(KieModule kModule);

    /**
     * Creates a new KieModule using the provided resource and dependencies
     * and automatically adds it to this KieRepository
     * @param resource
     * @param dependencies
     * @return The newly created KieModule
     */
    KieModule addKieModule(Resource resource, Resource... dependencies);

    /**
     * Retrieve a KieModule with the given ReleaseId in this KieRepository.
     * It is possible to use maven's conventions and version ranges like in
     * <pre>
     *     KieModule kieModule = kieRepository.getKieModule( KieServices.Factory.get().newReleaseId("group", "artifact", "LATEST") );
     * </pre>
     * or
     * <pre>
     *     KieModule kieModule = kieRepository.getKieModule( KieServices.Factory.get().newReleaseId("group", "artifact", "[1.0,1.2)") );
     * </pre>
     * @param releaseId The releaseId identifying the KieModule to be returned
     * @return The KieModule identified by the given releaseId or null if such KieModule doesn't exist
     */
    KieModule getKieModule(ReleaseId releaseId);
}
