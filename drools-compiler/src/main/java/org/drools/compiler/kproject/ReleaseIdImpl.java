/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.kproject;

import java.io.InputStream;
import java.util.Collection;

import org.appformer.maven.support.PomModel;
import org.kie.api.builder.ReleaseId;

import static java.util.stream.Collectors.toList;

public class ReleaseIdImpl extends org.appformer.maven.support.AFReleaseIdImpl implements ReleaseId {

    private static final String PROJECT_VERSION_MAVEN_PROPERTY = "${project.version}";
    private static final String PARENT_VERSION_MAVEN_PROPERTY = "${project.parent.version}";

    public ReleaseIdImpl() {
    }

    public ReleaseIdImpl(String releaseId) {
        super(releaseId);
    }

    public ReleaseIdImpl(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
    }

    public ReleaseIdImpl(String groupId, String artifactId, String version, String type) {
        super(groupId, artifactId, version, type);
    }

    public static ReleaseId adapt(org.appformer.maven.support.AFReleaseId r) {
        return adapt(r, null);
    }

    public static ReleaseId adapt(org.appformer.maven.support.AFReleaseId r, PomModel pomModel) {
        if (r instanceof ReleaseId) {
            return ( ReleaseId ) r;
        }

        final ReleaseIdImpl newReleaseIdImpl = new ReleaseIdImpl(r.getGroupId(),
                                                                 r.getArtifactId(),
                                                                 resolveVersion(r.getVersion(), pomModel),
                                                                 ((org.appformer.maven.support.AFReleaseIdImpl) r).getType());
        if (r.isSnapshot()) {
            newReleaseIdImpl.setSnapshotVersion(r.getVersion());
        }
        return newReleaseIdImpl;
    }

    public static Collection<ReleaseId> adaptAll( Collection<org.appformer.maven.support.AFReleaseId> rs, PomModel pomModel ) {
        return rs.stream().map(releaseId -> ReleaseIdImpl.adapt(releaseId, pomModel)).collect(toList());
    }

    public static ReleaseId fromPropertiesString( String path ) {
        return adapt( org.appformer.maven.support.AFReleaseIdImpl.fromPropertiesString(path) );
    }

    public static ReleaseId fromPropertiesStream( InputStream stream, String path ) {
        return adapt( org.appformer.maven.support.AFReleaseIdImpl.fromPropertiesStream(stream, path) );
    }

    public static String resolveVersion(String versionString, PomModel projectPomModel) {
        if (projectPomModel != null) {
            if (PROJECT_VERSION_MAVEN_PROPERTY.equals(versionString)) {
                return projectPomModel.getReleaseId().getVersion();
            } else if (PARENT_VERSION_MAVEN_PROPERTY.equals(versionString)
                    && (projectPomModel.getParentReleaseId() != null)) {
                return projectPomModel.getParentReleaseId().getVersion();
            } else {
                return versionString;
            }
        } else {
            return versionString;
        }
    }

}
