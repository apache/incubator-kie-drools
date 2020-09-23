package $Package$;

import org.kie.kogito.Config;
import org.kie.kogito.StaticApplication;
import org.kie.kogito.process.Processes;
import org.kie.kogito.rules.RuleUnits;

@org.springframework.stereotype.Component
@org.springframework.web.context.annotation.ApplicationScope
public class Application extends StaticApplication {

    @org.springframework.beans.factory.annotation.Autowired()
    public Application(
            Config config,
            java.util.Collection<Processes> processes,
            java.util.Collection<RuleUnits> ruleUnits/*,
            java.util.Collection<DecisionModels> decisionModels,
            java.util.Collection<PredictionModels> predictionModels,
            */) {
        this.config = config;
        this.processes = orNull(processes);
        this.ruleUnits = orNull(ruleUnits);
        this.decisionModels = null /* $DecisionModels$ */;
        this.predictionModels = null /* $PredictionModels$ */;

        if (config().process() != null) {
            unitOfWorkManager().eventManager().setAddons(config().addons());
        }
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
