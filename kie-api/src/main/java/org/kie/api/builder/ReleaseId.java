package org.kie.api.builder;

/**
 * ReleaseId is a full identifier far a given version of an artifact.
 * Following the Maven conventions it is composed of 3 parts: a groupId, an artifactId and a version
 */
public interface ReleaseId extends Comparable<ReleaseId> {

    String getGroupId();
    String getArtifactId();
    String getVersion();
    String toExternalForm();
    boolean isSnapshot();

    @Override
    default int compareTo(ReleaseId that) {
        if (this == that) {
            return 0;
        }
        return new ReleaseIdComparator().compare(this, that);
    }

}
