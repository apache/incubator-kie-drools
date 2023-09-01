package org.kie.maven.plugin;

import java.nio.file.Path;
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLModel;

public class PMMLResource {
    private final List<KiePMMLModel> kiePmmlModels;
    private final Path path;
    private final String modelPath;

    public PMMLResource(List<KiePMMLModel> kiePmmlModels, Path path , String modelPath) {
        this.kiePmmlModels = kiePmmlModels;
        this.path = path;
        this.modelPath = modelPath;
    }

    public List<KiePMMLModel> getKiePmmlModels() {
        return kiePmmlModels;
    }

    public Path getPath() {
        return path;
    }

    public String getModelPath() {
        return modelPath;
    }
}
