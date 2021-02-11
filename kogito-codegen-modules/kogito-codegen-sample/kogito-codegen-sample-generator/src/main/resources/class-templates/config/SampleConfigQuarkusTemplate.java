import org.eclipse.microprofile.config.inject.ConfigProperty;

@javax.inject.Singleton
public class SampleConfig extends org.kie.kogito.codegen.sample.core.SampleConfigImpl {

    public SampleConfig(
        @ConfigProperty(name = "kogito.sample.numberOfCopy", defaultValue = "1") int numberOfCopy
    ) {
        super(numberOfCopy);
    }
}
