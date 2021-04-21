/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito;

import java.util.Objects;

public class KogitoGAV {

    public static final KogitoGAV EMPTY_GAV = new KogitoGAV("", "", "");

    protected String groupId;
    protected String artifactId;
    protected String version;

    protected KogitoGAV() {
        // for serialization
    }

    public KogitoGAV(final String groupId,
            final String artifactId,
            final String version) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        KogitoGAV kogitoGAV = (KogitoGAV) o;
        return Objects.equals(groupId, kogitoGAV.groupId) && Objects.equals(artifactId, kogitoGAV.artifactId) && Objects.equals(version, kogitoGAV.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, artifactId, version);
    }
}
