/*
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

package org.optaplanner.migration.jakarta;

import org.openrewrite.ExecutionContext;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.maven.tree.ResolvedPom;
import org.openrewrite.xml.tree.Xml;

final class InternalOptaPlannerMavenVisitor extends MavenVisitor<ExecutionContext> {

    private static final String GROUP_ID = "org.optaplanner";
    private static final String ARTIFACT_ID_PREFIX = "optaplanner-";

    @Override
    public Xml visitDocument(Xml.Document document, ExecutionContext executionContext) {
        ResolvedPom pom = getResolutionResult().getPom();
        if (pom.getGroupId().equals(GROUP_ID) && pom.getArtifactId().startsWith(ARTIFACT_ID_PREFIX)) {
            return SearchResult.found(document);
        }
        return document;
    }
}
