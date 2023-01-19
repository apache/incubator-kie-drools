package org.optaplanner.migration.jakarta;

import java.nio.file.Path;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.tree.ResolvedPom;
import org.openrewrite.xml.tree.Xml;

public class GraalVmMigrationRecipe extends Recipe {

    private static final String OPTAPLANNER_QUARKUS_MODULE = "optaplanner-quarkus";

    @Override
    public String getDisplayName() {
        return "Migrate GraalVM SDK artifact";
    }

    @Override
    public String getDescription() {
        return "Migrates 'org.graalvm.sdk:graal-sdk' to 'org.graalvm.nativeimage:svm'.";
    }

    @Override
    protected MavenIsoVisitor<ExecutionContext> getSingleSourceApplicableTest() {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext executionContext) {
                ResolvedPom pom = getResolutionResult().getPom();
                if (pom.getGroupId().equals("org.optaplanner") && pom.getArtifactId().startsWith("optaplanner-quarkus")) {
                    return SearchResult.found(document);
                }
                return document;
            }
        };
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext executionContext) {
                doNext(new ChangeDependencyGroupIdAndArtifactId("org.graalvm.sdk", "graal-sdk",
                        "org.graalvm.nativeimage", "svm", null, null));
                return super.visitDocument(document, executionContext);
            }
        };
    }

    private boolean parentEndsWith(Path path, String suffix) {
        return path.getParent() != null && path.getParent().endsWith(suffix);
    }
}
