package $Package$;

import org.kie.kogito.StaticApplication;

public class Application extends StaticApplication {

    public Application() {
        this.config = new ApplicationConfig();
        this.processes = null /* $Processes$ */;
        this.ruleUnits = null /* $RuleUnits$ */;
        this.decisionModels = null /* $DecisionModels$ */;
        this.predictionModels = null /* $PredictionModels$ */;
    }
}
