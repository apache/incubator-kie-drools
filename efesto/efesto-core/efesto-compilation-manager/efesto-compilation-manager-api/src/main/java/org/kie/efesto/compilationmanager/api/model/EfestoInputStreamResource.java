package org.kie.efesto.compilationmanager.api.model;

import java.io.InputStream;

import org.kie.efesto.common.api.utils.FileNameUtils;

public final class EfestoInputStreamResource implements EfestoResource<InputStream> {

    private final InputStream inputStream;
    private final String modelType;

    private final String fileName;

    /**
     *
     * @param inputStream
     * @param fileName Name of the file, included suffix.
     */
    public EfestoInputStreamResource(InputStream inputStream, String fileName) {
        this.inputStream = inputStream;
        this.modelType = FileNameUtils.getSuffix(fileName);
        this.fileName = fileName;
    }

    @Override
    public InputStream getContent() {
        return inputStream;
    }

    public String getModelType() {
        return modelType;
    }

    public String getFileName() {
        return fileName;
    }
}
