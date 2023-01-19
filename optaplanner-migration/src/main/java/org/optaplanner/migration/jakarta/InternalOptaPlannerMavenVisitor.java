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
