package org.kie.efesto.compilationmanager.api.model;

import org.kie.efesto.common.api.utils.FileNameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class EfestoFileResource implements EfestoResource<File> {

    private final File modelFile;

    public EfestoFileResource(File modelFile) {
        this.modelFile = modelFile;
    }

    @Override
    public File getContent() {
        return modelFile;
    }

    public String getModelType() {
        return FileNameUtils.getSuffix(modelFile.getName());
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(modelFile);
    }

    public String getSourcePath() {
        return modelFile.getPath();
    }


}
