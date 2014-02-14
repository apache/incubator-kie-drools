package org.jbpm.services.task.jaxb;

import org.yaml.snakeyaml.Yaml;

public class YamlTaskSerializationTest extends AbstractTaskSerializationTest {

    public TestType getType() { 
        return TestType.YAML;
    }
    
    private Yaml yaml = new Yaml();
    
    public Object testRoundTrip(Object in) throws Exception {
        String output = yaml.dump(in);
        logger.debug(output);
        return yaml.load(output);
    }

    @Override
    public void addClassesToSerializationContext(Class<?>... extraClass) {
        // no-op
    }

}
