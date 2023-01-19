package org.optaplanner.migration.jakarta;

import java.util.Optional;
import java.util.function.BiConsumer;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.maven.ChangeDependencyGroupIdAndArtifactId;
import org.openrewrite.maven.MavenIsoVisitor;
import org.openrewrite.maven.MavenVisitor;
import org.openrewrite.maven.RemoveExclusion;
import org.openrewrite.xml.tree.Xml;

public class JakartaHibernateMigrationRecipe extends Recipe {

    private static final String HIBERNATE_GROUP_ID = "org.hibernate";
    private static final String HIBERNATE_ARTIFACT_ID = "hibernate-core";

    @Override
    public String getDisplayName() {
        return "Migrate hibernate artifacts to Jakarta";
    }

    @Override
    public String getDescription() {
        return "Migrates 'org.hibernate:hibernate-core' to 'org.hibernate:hibernate-core-jakarta'.";
    }

    @Override
    protected MavenVisitor<ExecutionContext> getSingleSourceApplicableTest() {
        return new InternalOptaPlannerMavenVisitor();
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MavenIsoVisitor<>() {

            @Override
            public Xml.Tag visitTag(Xml.Tag tag, ExecutionContext executionContext) {
                if (isDependencyTag(HIBERNATE_GROUP_ID, HIBERNATE_ARTIFACT_ID)) {
                    forEachExclusion(tag,
                            (exclusionGroupId, exclusionArtifactId) -> doNext(
                                    new RemoveExclusion(HIBERNATE_GROUP_ID, HIBERNATE_ARTIFACT_ID,
                                            exclusionGroupId, exclusionArtifactId)));

                    doNext(new ChangeDependencyGroupIdAndArtifactId(HIBERNATE_GROUP_ID, HIBERNATE_ARTIFACT_ID,
                            HIBERNATE_GROUP_ID, HIBERNATE_ARTIFACT_ID + "-jakarta", null, null));
                }

                return super.visitTag(tag, executionContext);
            }

            private void forEachExclusion(Xml.Tag tag, BiConsumer<String, String> exclusionConsumer) {
                tag.getChild("exclusions").ifPresent(exclusions -> exclusions.getChildren("exclusion").forEach(exclusion -> {
                    Optional<String> exclusionGroupId = exclusion.getChildValue("groupId");
                    Optional<String> exclusionArtifactId = exclusion.getChildValue("artifactId");
                    if (exclusionGroupId.isPresent() && exclusionArtifactId.isPresent()) {
                        exclusionConsumer.accept(exclusionGroupId.get(), exclusionArtifactId.get());
                    }
                }));
            }
        };
    }
}
