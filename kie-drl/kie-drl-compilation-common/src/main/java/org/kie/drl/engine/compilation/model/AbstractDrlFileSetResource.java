package org.kie.drl.engine.compilation.model;

import org.drools.util.io.FileSystemResource;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractDrlFileSetResource extends EfestoFileSetResource implements EfestoResource<Set<File>> {

    private final Set<FileSystemResource> fileSystemResources;

    protected AbstractDrlFileSetResource(Set<File> modelFiles, String basePath) {
        super(modelFiles, "drl", basePath);
        this.fileSystemResources =
                modelFiles.stream()
                        .map(FileSystemResource::new)
                        .collect(Collectors.toSet());
    }


    public Set<FileSystemResource> getFileSystemResource() {
        return fileSystemResources;
    }


}
