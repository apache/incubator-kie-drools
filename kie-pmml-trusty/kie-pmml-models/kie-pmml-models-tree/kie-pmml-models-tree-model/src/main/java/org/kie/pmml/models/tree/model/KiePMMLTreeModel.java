package  org.kie.pmml.models.tree.model;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.kie.pmml.api.runtime.PMMLRuntimeContext;
import org.kie.pmml.commons.model.KiePMMLModel;

public abstract class KiePMMLTreeModel extends KiePMMLModel {

    private static final long serialVersionUID = -5158590062736070465L;

    protected Function<Map<String, Object>, KiePMMLNodeResult> nodeFunction;

    protected KiePMMLTreeModel(String fileName, String modelName) {
        super(fileName, modelName, Collections.emptyList());
    }

    @Override
    public Object evaluate(final Map<String, Object> requestData,
                           final PMMLRuntimeContext context) {
        KiePMMLNodeResult kiePMMLNodeResult = nodeFunction.apply(requestData);
        context.setProbabilityResultMap(kiePMMLNodeResult.getProbabilityMap());
        return kiePMMLNodeResult.getScore();
    }

}
