package $Package$;

@javax.inject.Singleton
public class Application extends org.kie.kogito.StaticApplication {

    @javax.inject.Inject
    public Application(
            org.kie.kogito.Config config,
            javax.enterprise.inject.Instance<org.kie.kogito.process.Processes> processes,
            javax.enterprise.inject.Instance<org.kie.kogito.rules.RuleUnits> ruleUnits,
            javax.enterprise.inject.Instance<org.kie.kogito.decision.DecisionModels> decisionModels,
            javax.enterprise.inject.Instance<org.kie.kogito.prediction.PredictionModels> predictionModels) {
        super(config, orNull(processes), orNull(ruleUnits), orNull(decisionModels), orNull(predictionModels));
    }

    private static <T> T orNull(javax.enterprise.inject.Instance<T> instance) {
        if (instance.isUnsatisfied()) {
            return null;
        } else {
            return instance.get();
        }
    }

}
