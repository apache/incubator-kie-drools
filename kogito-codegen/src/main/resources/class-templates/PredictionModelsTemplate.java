import java.util.Map;
import java.util.function.Function;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;

public class PredictionModels implements org.kie.kogito.prediction.PredictionModels {

    public final static java.util.function.Function<String, org.kie.api.runtime.KieRuntimeFactory> kieRuntimeFactoryFunction;

    static {
        final java.util.Map<org.kie.api.KieBase, org.kie.api.runtime.KieRuntimeFactory> kieRuntimeFactories = org.kie.kogito.pmml.PMMLKogito.createKieRuntimeFactories();
        kieRuntimeFactoryFunction = new java.util.function.Function<java.lang.String, org.kie.api.runtime.KieRuntimeFactory>() {
            @Override
            public org.kie.api.runtime.KieRuntimeFactory apply(java.lang.String s) {
                return kieRuntimeFactories.keySet().stream()
                        .filter(kieBase -> org.kie.pmml.evaluator.core.utils.KnowledgeBaseUtils.getModel(kieBase, s).isPresent())
                        .map(kieBase ->  kieRuntimeFactories.get(kieBase))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Failed to fine KieRuntimeFactory for model " +s));
            }
        };
    }

    public PredictionModels(org.kie.kogito.Application app) {
    }

    public org.kie.kogito.prediction.PredictionModel getPredictionModel(java.lang.String modelName) {
        return new org.kie.kogito.pmml.PmmlPredictionModel(getPMMLRuntime(modelName), modelName);
    }

    private org.kie.pmml.api.runtime.PMMLRuntime getPMMLRuntime(java.lang.String modelName) {
        return kieRuntimeFactoryFunction.apply(modelName).get(org.kie.pmml.api.runtime.PMMLRuntime.class);
    }
}

