package $Package$;

import org.kie.kogito.StaticApplication;

public class Application extends StaticApplication {

    public Application() {
        super(new ApplicationConfig());
        loadEngines(
                $Processes$,
                $RuleUnits$,
                $DecisionModels$,
                $PredictionModels$);
    }
}
