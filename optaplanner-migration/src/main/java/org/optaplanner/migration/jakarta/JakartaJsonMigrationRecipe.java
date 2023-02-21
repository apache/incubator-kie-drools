package org.optaplanner.migration.jakarta;

import java.nio.file.Path;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.maven.RemoveDependency;
import org.openrewrite.maven.RemoveExclusion;
import org.openrewrite.xml.tree.Xml;

public class JakartaJsonMigrationRecipe extends Recipe {

    private static final String OPTAPLANNER_JSONB_MODULE = "optaplanner-persistence-jsonb";
    private static final String ORG_GLASSFISH = "org.glassfish";
    private static final String ORG_ECLIPSE = "org.eclipse";
    private static final String JAKARTA_JSON = "jakarta.json";
    private static final String JAKARTA_JSON_API = "jakarta.json-api";
    private static final String YASSON = "yasson";

    @Override
    public String getDisplayName() {
        return "Migrate Json artifacts to Jakarta";
    }

    @Override
    public String getDescription() {
        return "Migrates 'org.glassfish:jakarta.json' to 'jakarta.json:jakarta.json-api'.";
    }

    @Override
    protected MavenVisitor<ExecutionContext> getSingleSourceApplicableTest() {
        return new InternalOptaPlannerMavenVisitor();
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {
            @Override
            public Xml.Document visitDocument(Xml.Document document, ExecutionContext executionContext) {
                Path parent = document.getSourcePath().getParent();
                if (parent != null && parent.toString().endsWith(OPTAPLANNER_JSONB_MODULE)) {
                    doNext(new RemoveDependency(ORG_GLASSFISH, JAKARTA_JSON, "runtime"));
                    doNext(new RemoveExclusion(ORG_ECLIPSE, YASSON, JAKARTA_JSON, JAKARTA_JSON_API, false));
                    doNext(new RemoveExclusion(ORG_ECLIPSE, YASSON, ORG_GLASSFISH, JAKARTA_JSON, false));
                } else {
                    doNext(new ChangeDependencyGroupIdAndArtifactId(ORG_GLASSFISH, JAKARTA_JSON, JAKARTA_JSON,
                            JAKARTA_JSON_API, null, null));
                }
                return super.visitDocument(document, executionContext);
            }
        };
    }
}
