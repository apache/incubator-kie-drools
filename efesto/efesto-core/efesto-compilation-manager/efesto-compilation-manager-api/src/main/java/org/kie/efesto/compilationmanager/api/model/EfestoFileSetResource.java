package org.kie.efesto.compilationmanager.api.model;

import java.io.File;
import java.util.Set;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class EfestoFileSetResource extends EfestoSetResource<File> {

    public EfestoFileSetResource(Set<File> modelFiles, ModelLocalUriId modelLocalUriId) {
        super(modelFiles, modelLocalUriId);
    }


}
