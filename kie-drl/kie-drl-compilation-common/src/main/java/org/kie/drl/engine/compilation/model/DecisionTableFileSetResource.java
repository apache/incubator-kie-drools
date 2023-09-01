package org.kie.drl.engine.compilation.model;

import java.io.File;
import java.util.Set;

/**
 * File set for "decision tables" files
 */
public class DecisionTableFileSetResource extends AbstractDrlFileSetResource {

    public DecisionTableFileSetResource(Set<File> modelFiles, String basePath) {
        super(modelFiles, basePath);
    }

}
