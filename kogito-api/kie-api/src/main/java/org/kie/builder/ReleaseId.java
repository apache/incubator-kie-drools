package org.kie.builder;

/**
 * ReleaseId is a full identifier far a given version of an artifact.
 * Following the maven convetions it is composed of 3 parts: a groupId, an artifactId and a version
 */
public interface ReleaseId {

    String getGroupId();
    String getArtifactId();
    String getVersion();
    String toExternalForm();
}
