package org.kie.pmml.models.regression.model;

import java.util.Collections;
import java.util.Map;

import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.IsInterpreted;
import org.kie.pmml.commons.model.KiePMMLModel;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/Regression.html>Regression</a>
 */
public class KiePMMLRegressionModel extends KiePMMLModel implements IsInterpreted {

    private static final long serialVersionUID = -6870859552385880008L;
    private AbstractKiePMMLTable regressionTable;

    private KiePMMLRegressionModel(String fileName, String modelName) {
        super(fileName, modelName, Collections.emptyList());
    }

    public static Builder builder(String fileName, String name, MINING_FUNCTION miningFunction) {
        return new Builder(fileName, name, miningFunction);
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        return regressionTable.evaluateRegression(requestData, context);
    }

    public AbstractKiePMMLTable getRegressionTable() {
        return regressionTable;
    }

    public static class Builder extends KiePMMLModel.Builder<KiePMMLRegressionModel> {

        private Builder(String fileName, String name, MINING_FUNCTION miningFunction) {
            super("Regression-", PMML_MODEL.REGRESSION_MODEL, miningFunction,
                  () -> new KiePMMLRegressionModel(fileName, name));
        }

        public Builder withAbstractKiePMMLTable(AbstractKiePMMLTable regressionTable) {
            if (regressionTable != null) {
                toBuild.regressionTable = regressionTable;
            }
            return this;
        }
    }
}
