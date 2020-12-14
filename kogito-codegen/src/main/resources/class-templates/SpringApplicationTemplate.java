package $Package$;

@org.springframework.stereotype.Component
@org.springframework.web.context.annotation.ApplicationScope
public class Application extends org.kie.kogito.StaticApplication {

    @org.springframework.beans.factory.annotation.Autowired()
    public Application(
            org.kie.kogito.Config config,
            java.util.Collection<org.kie.kogito.process.Processes> processes,
            java.util.Collection<org.kie.kogito.rules.RuleUnits> ruleUnits,
            java.util.Collection<org.kie.kogito.decision.DecisionModels> decisionModels,
            java.util.Collection<org.kie.kogito.prediction.PredictionModels> predictionModels) {
        super(config, orNull(processes), orNull(ruleUnits), orNull(decisionModels), orNull(predictionModels));
    }

    private static <T> T orNull(java.util.Collection<T> collection) {
        if (collection.isEmpty()) {
            return null;
        } else {
            if (collection.size() > 1) {
                throw new IllegalArgumentException("Found too many injection candidates " + collection);
            }
            return collection.iterator().next();
        }
    }
}
