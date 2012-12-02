package org.kie.builder;

public interface GAV {

    String getGroupId();
    String getArtifactId();
    String getVersion();
    String toExternalForm();
}
