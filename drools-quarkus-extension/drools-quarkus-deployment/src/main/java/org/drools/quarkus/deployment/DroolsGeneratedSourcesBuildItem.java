package org.drools.quarkus.deployment;

import java.util.Collection;

import io.quarkus.builder.item.SimpleBuildItem;
import org.drools.model.project.codegen.GeneratedFile;

public final class DroolsGeneratedSourcesBuildItem extends SimpleBuildItem {

    private final Collection<GeneratedFile> generatedFiles;

    public DroolsGeneratedSourcesBuildItem(Collection<GeneratedFile> generatedFiles) {
        this.generatedFiles = generatedFiles;
    }

    public Collection<GeneratedFile> getGeneratedFiles() {
        return generatedFiles;
    }

}
