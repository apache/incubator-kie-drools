package org.kie.api.builder;

import org.kie.api.io.Resource;

/**
 * KieFileSystem is an in memory file system used to programmatically define
 * the resources composing a KieModule
 */
public interface KieFileSystem {

    /**
     * Generates a basic maven pom file with the given ReleaseId (groupId, artifactId and version)
     * and adds it to this KieFileSystem
     */
    KieFileSystem generateAndWritePomXML(ReleaseId releaseId);

    /**
     * Adds the given pom.xml file to this KieFileSystem
     */
    KieFileSystem writePomXML(byte[] content);

    /**
     * Adds the given pom.xml file to this KieFileSystem
     */
    KieFileSystem writePomXML(String content);

    /**
     * Adds the given kmodule.xml file to this KieFileSystem
     */
    KieFileSystem writeKModuleXML(byte[] content);

    /**
     * Adds the given kmodule.xml file to this KieFileSystem
     */
    KieFileSystem writeKModuleXML(String content);

    /**
     * Adds the given content to this KieFileSystem in the specified path
     */
    KieFileSystem write(String path, byte[] content);

    /**
     * Adds the given content to this KieFileSystem in the specified path
     */
    KieFileSystem write(String path, String content);

    /**
     * Adds the given Resource to this KieFileSystem in the specified path
     */
    KieFileSystem write(String path, Resource resource);

    /**
     * Adds the given Resource to this KieFileSystem
     */
    KieFileSystem write(Resource resource);

    /**
     * Removes the files in the given paths from this KieFileSystem
     */
    void delete(String... paths);

    /**
     * Returns the content of the file in the specified path as a byte[]
     */
    byte[] read(String path);
}
