package org.kie.dmn.core.assembler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kie.api.io.ResourceWithConfiguration;
import org.kie.dmn.model.v1_1.Definitions;

public class DMNResource {

    private final QName modelID;
    private final ResourceWithConfiguration resAndConfig;
    private final Definitions definitions;
    private final List<QName> dependencies = new ArrayList<>();

    public DMNResource(QName modelID, ResourceWithConfiguration resAndConfig, Definitions definitions) {
        this.modelID = modelID;
        this.resAndConfig = resAndConfig;
        this.definitions = definitions;
    }

    public QName getModelID() {
        return modelID;
    }

    public ResourceWithConfiguration getResAndConfig() {
        return resAndConfig;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public void addDependency(QName dep) {
        this.dependencies.add(dep);
    }

    public void addDependencies(List<QName> deps) {
        this.dependencies.addAll(deps);
    }

    public List<QName> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "DMNResource [modelID=" + modelID + ", resource=" + resAndConfig.getResource().getSourcePath() + "]";
    }

}