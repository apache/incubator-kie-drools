/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.api.management;

import javax.management.openmbean.CompositeData;

import org.kie.api.builder.ReleaseId;

/**
 * A simple immutable pojo representing Maven GAV coordinates, with a JMX-compliant method in order to be exposed and used via JMX/Monitoring.
 */
/*
 * This class have been introduced because ReleaseIdImpl is part of the drools-core package, and at the same time to avoid any modification to the ReleaseId interface.
 * A proposal of adding 1 static method and 1 static public inner class on ReleaseId interface was abandoned, so to avoid any modification of the ReleaseId interface.
 * This simple immutable pojo can be evaluated to be moved/promoted to another package, as long as current and potentially additional JMX method can be added or modified as needed.
 */
public class GAV implements ReleaseId {
    private final String groupId;
    private final String artifactId;
    private final String version;

    public GAV(String groupId,
            String artifactId,
            String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public String toString() {
        return groupId + ":" + artifactId + ":" + version;
    }

    public String toExternalForm() {
        return toString();
    }

    public boolean isSnapshot() {
        return version.endsWith("-SNAPSHOT");
    }

    public static GAV from(CompositeData cd) {
        return new GAV((String) cd.get("groupId"),
                                 (String) cd.get("artifactId"),
                                 (String) cd.get("version"));
    }

    public static GAV from(ReleaseId rel) {
        return new GAV(rel.getGroupId(),
                                 rel.getArtifactId(),
                                 rel.getVersion());
    }

    public boolean sameGAVof(ReleaseId other) {
        return this.groupId.equals(other.getGroupId())
                && this.artifactId.equals(other.getArtifactId())
                && this.version.equals(other.getVersion()) ;
    }
}
