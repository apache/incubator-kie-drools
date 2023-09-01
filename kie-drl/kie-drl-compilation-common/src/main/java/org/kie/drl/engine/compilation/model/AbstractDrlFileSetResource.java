package org.kie.drl.engine.compilation.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.drools.io.FileSystemResource;
import org.kie.drl.api.identifiers.LocalComponentIdDrl;
import org.kie.efesto.compilationmanager.api.model.EfestoFileSetResource;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

public abstract class AbstractDrlFileSetResource extends EfestoFileSetResource implements EfestoResource<Set<File>> {

    private final Set<FileSystemResource> fileSystemResources = new HashSet<>();

    protected AbstractDrlFileSetResource(Set<File> modelFiles, String basePath) {
        super(modelFiles, new LocalComponentIdDrl(basePath));
        for (File modelFile : modelFiles) {
            fileSystemResources.add(new FileSystemResource(modelFile));
        }
    }


    public Set<FileSystemResource> getFileSystemResource() {
        return fileSystemResources;
    }


}
