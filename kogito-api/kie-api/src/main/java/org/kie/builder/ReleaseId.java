package org.kie.builder;

public interface ReleaseId {

    String getGroupId();
    String getArtifactId();
    String getVersion();
    String toExternalForm();
}
