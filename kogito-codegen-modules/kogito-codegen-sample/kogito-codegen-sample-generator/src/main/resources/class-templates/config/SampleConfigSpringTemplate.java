import org.springframework.beans.factory.annotation.Value;

@org.springframework.stereotype.Component
public class SampleConfig extends org.kie.kogito.codegen.sample.core.SampleConfigImpl {

    public SampleConfig(
            @Value(value = "${kogito.sample.numberOfCopy:1}") int numberOfCopy
    ) {
        super(numberOfCopy);
    }
}
