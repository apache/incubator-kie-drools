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
