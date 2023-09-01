package org.kie.pmml.models.regression.model;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;

public final class KiePMMLRegressionTable extends AbstractKiePMMLTable {

    private static final long serialVersionUID = -7899446939844650691L;

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    private KiePMMLRegressionTable(String name, List<KiePMMLExtension> extensions) {
        // Keeping private to implement Builder pattern
        super(name, extensions);
    }

    public static class Builder extends AbstractKiePMMLTable.Builder<KiePMMLRegressionTable> {

        protected Builder(String name, List<KiePMMLExtension> extensions) {
            super("KiePMMLRegressionTable-", () -> new KiePMMLRegressionTable(name, extensions));
        }
    }
}
