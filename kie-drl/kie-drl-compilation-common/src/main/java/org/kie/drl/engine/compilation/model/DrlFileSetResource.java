package org.kie.drl.engine.compilation.model;

import java.io.File;
import java.util.Set;

/**
 * File set for "drl" files
 */
public class DrlFileSetResource extends AbstractDrlFileSetResource {


    public DrlFileSetResource(Set<File> modelFiles, String basePath) {
        super(modelFiles, basePath);
    }

}
